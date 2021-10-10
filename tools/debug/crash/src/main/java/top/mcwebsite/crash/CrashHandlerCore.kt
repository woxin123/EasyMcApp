package top.mcwebsite.crash

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import top.mcwebsite.crash.activity.TestActivity
import top.mcwebsite.crash.config.CrashConfig
import top.mcwebsite.crash.sp.CrashPreferences
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipFile
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object CrashHandlerCore {

    private const val TAG = "CrashHandler"
    private const val CRASH_HANDLER_PACKAGE_NAME = "top.mcwebsite.crash"
    private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
    private const val EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE"
    internal const val EXTRA_CONFIG = "extra_config"

    private const val TIME_NO_CUSTOM_FOREGROUND_MS = 500
    private const val MAX_STICK_TRACE_SIZE = 1024 * 128 - 1


    private lateinit var application: Application

    private lateinit var config: CrashConfig

    // 防止内存泄露
    private var lastActivityCreated = WeakReference<Activity>(null)
    private var lastActivityCreatedTime: Long = 0L

    private var isInBackground = true
    private var currentStartedActivities = 0


    fun install(context: Context, configInit: CrashConfig = CrashConfig()) {
        this.config = configInit
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()?.apply {
            if (this::class.java.name.startsWith(CRASH_HANDLER_PACKAGE_NAME)) {
                Log.e(TAG, "CrashHandler was already installed, doing nothing!")
            } else {
                Log.e(TAG, "WARNING!!! You already have an UncaughtException.")
            }
        }
        application = context.applicationContext as Application
        val configInternal = this.config
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val nConfig = config
            if (!configInternal.enable) {
                oldHandler?.uncaughtException(thread, exception)
                return@setDefaultUncaughtExceptionHandler
            }
            Log.e(TAG, "App has crashed, executing CrashHandler's UncaughtException", exception)

//            if (hasCrashInTheLastSeconds(context)) {
//                Log.e(
//                    TAG,
//                    "App already crash recently, not staring custom error activity because we could enter a restart loop."
//                )
//                oldHandler?.uncaughtException(thread, exception)
//                return@setDefaultUncaughtExceptionHandler
//            }
//            CrashPreferences.saveLastCrashTime(context, System.currentTimeMillis())
            if (isStackTraceLikelyConflictive(exception)) {
                Log.e(
                    TAG,
                    "Your application class or your error activity have crashed, the custom activity will not be launched!"
                )
                oldHandler?.uncaughtException(thread, exception)
                return@setDefaultUncaughtExceptionHandler
            }
            if (configInternal.backgroundMode == CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM || !isInBackground
                || (lastActivityCreatedTime >= System.currentTimeMillis() - TIME_NO_CUSTOM_FOREGROUND_MS)
            ) {
                val intent = Intent(application, configInternal.errorCrashActivityClass)
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                exception.printStackTrace(pw)
                val stackTraceString = sw.toString().let {
                    // 限制堆栈最大 128K，太长会导致 Intent 传递的数据过大
                    if (it.length > MAX_STICK_TRACE_SIZE) {
                        val disclaimer = "[stack trace too large]"
                        it.substring(0, MAX_STICK_TRACE_SIZE - disclaimer.length) + disclaimer
                    } else {
                        it
                    }
                }
//                intent.putExtra(EXTRA_CONFIG, nConfig)
                intent.putExtra(EXTRA_STACK_TRACE, stackTraceString)
                if (configInternal.isShowRestartButton && configInternal.restartActivityClass == null) {
                    configInternal.restartActivityClass = application
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                Log.e(TAG, "start error activity")
                application.startActivity(intent)
                lastActivityCreated.get()?.apply {
                    finish()
                    lastActivityCreated.clear()
                }
                killCurrentProcess()
            } else if (configInternal.backgroundMode == CrashConfig.BACKGROUND_MODE_CRASH) {
                oldHandler?.uncaughtException(thread, exception)
                return@setDefaultUncaughtExceptionHandler
            }
        }
        registerActivityLifecycleCallbacks(configInternal)
    }

    private fun registerActivityLifecycleCallbacks(config: CrashConfig) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity::class.java != config.errorCrashActivityClass) {
                    lastActivityCreated = WeakReference(activity)
                    lastActivityCreatedTime = System.currentTimeMillis()
                }
            }

            override fun onActivityStarted(activity: Activity) {
                currentStartedActivities++
                isInBackground = currentStartedActivities == 0
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                currentStartedActivities--
                isInBackground = currentStartedActivities == 0
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })
    }

    private fun killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }

    private fun isStackTraceLikelyConflictive(throwable: Throwable): Boolean {
        val process = BufferedReader(FileReader("/proc/self/cmdline")).use {
            it.readLine().trim()
        }
        if (process.endsWith(":error_activity")) {
            return true
        }
        var throwableInternal: Throwable? = throwable
        while (throwableInternal != null) {
            val stackTrace = throwableInternal.stackTrace
            for (ele in stackTrace) {
                if (ele.className == "android.app.ActivityThread" && ele.methodName == "handleBindApplication") {
                    return true
                }
            }
            throwableInternal = throwableInternal.cause
        }
        return false
    }

    private fun hasCrashInTheLastSeconds(context: Context): Boolean {
        val lastTimeStamp = CrashPreferences.getLastCrashTime(context)
        val currentTime = System.currentTimeMillis()
        return (lastTimeStamp < currentTime && currentTime - lastTimeStamp < config?.crashIntervalTimeMs ?: 1000)
    }

    private fun getBuildDateAsString(context: Context, dateFormat: DateFormat): String {
        return try {
            val ai = context.packageManager.getApplicationInfo(context.packageName, 0)
            ZipFile(ai.sourceDir).use {
                dateFormat.format(it.getEntry("class.dex").time)
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getVersionName(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getDeviceModelName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize("$manufacturer $model")
        }
    }

    private fun capitalize(s: String): String {
        if (s.isBlank()) return ""
        return if (s.first().isUpperCase()) {
            s
        } else {
            s.first().uppercaseChar() + s.substring(1)
        }
    }


    fun getStackTraceFromIntent(intent: Intent): String {
        return intent.getStringExtra(EXTRA_STACK_TRACE) ?: ""
    }

    fun getConfigFromIntent(intent: Intent): CrashConfig {
        return intent.getSerializableExtra(EXTRA_CONFIG) as CrashConfig
    }


    fun getAllErrorDetailFromIntent(context: Context, intent: Intent): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Get build date
        val buildDateAsString = getBuildDateAsString(context, dateFormat)

        // Get app version
        val versionName = getVersionName(context)

        val packageName = context.packageName

        return StringBuilder().apply {
            append("Build version: $versionName\n")
            append("Build date: $buildDateAsString\n")
            append("Current date ${dateFormat.format(currentDate)}\n")
            append("package name: $packageName\n")
            append("Device: ${getDeviceModelName()}\n")
            append("System Version: ${Build.VERSION.RELEASE}\n\n")
            append("Stack trace:  \n")
            append(getStackTraceFromIntent(intent))

        }.toString()

    }

}