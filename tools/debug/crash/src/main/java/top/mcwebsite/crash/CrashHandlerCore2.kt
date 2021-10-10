package top.mcwebsite.crash

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import top.mcwebsite.crash.activity.DefaultCrashActivity
import java.io.PrintWriter
import java.io.Serializable
import java.io.StringWriter
import java.lang.Exception
import java.lang.ref.WeakReference
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipFile

@SuppressLint("NewApi")
object CrashHandlerCore2 {
    private const val TAG = "CustomActivityOnCrash"

    //Extras passed to the error activity
    private const val EXTRA_RESTART_ACTIVITY_CLASS =
        "cat.ereza.customactivityoncrash.EXTRA_RESTART_ACTIVITY_CLASS"
    private const val EXTRA_SHOW_ERROR_DETAILS =
        "cat.ereza.customactivityoncrash.EXTRA_SHOW_ERROR_DETAILS"
    private const val EXTRA_STACK_TRACE = "cat.ereza.customactivityoncrash.EXTRA_STACK_TRACE"
    private const val EXTRA_IMAGE_DRAWABLE_ID =
        "cat.ereza.customactivityoncrash.EXTRA_IMAGE_DRAWABLE_ID"
    private const val EXTRA_EVENT_LISTENER = "cat.ereza.customactivityoncrash.EXTRA_EVENT_LISTENER"

    //General constants
    private const val INTENT_ACTION_ERROR_ACTIVITY = "cat.ereza.customactivityoncrash.ERROR"
    private const val INTENT_ACTION_RESTART_ACTIVITY = "cat.ereza.customactivityoncrash.RESTART"
    private const val CAOC_HANDLER_PACKAGE_NAME = "cat.ereza.customactivityoncrash"
    private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
    private const val MAX_STACK_TRACE_SIZE = 131071 //128 KB - 1
    private const val TIMESTAMP_DIFFERENCE_TO_AVOID_RESTART_LOOPS_IN_MILLIS = 2000

    //Shared preferences
    private const val SHARED_PREFERENCES_FILE = "custom_activity_on_crash"
    private const val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp"

    //Internal variables
    private var application: Application? = null
    private var lastActivityCreated = WeakReference<Activity?>(null)
    private var isInBackground = false
    /**
     * Returns if the error activity must be launched when the app is on background.
     *
     * @return true if it will be launched, false otherwise.
     */
    /**
     * Defines if the error activity must be launched when the app is on background.
     * Set it to true if you want to launch the error activity when the app is in background,
     * false if you want it not to launch and crash silently.
     * This has no effect in API<14 and the error activity is always launched.
     * The default is true (the app will be brought to front when a crash occurs).
     */
    //Settable properties and their defaults
    var isLaunchErrorActivityWhenInBackground = true
    /**
     * Returns if the error activity will show the error details button.
     *
     * @return true if it will be shown, false otherwise.
     */
    /**
     * Defines if the error activity must shown the error details button.
     * Set it to true if you want to show the full stack trace and device info,
     * false if you want it to be hidden.
     * The default is true.
     */
    var isShowErrorDetails = true
    /**
     * Returns if the error activity should show a restart button.
     * Note that even if restart is enabled, a valid restart activity could not be found.
     * In that case, a close button will still be used.
     *
     * @return true if a restart button should be shown, false if a close button must be used.
     */
    /**
     * Defines if the error activity should show a restart button.
     * Set it to true if you want to show a restart button,
     * false if you want to show a close button.
     * Note that even if restart is enabled, a valid restart activity could not be found.
     * In that case, a close button will still be used.
     * The default is true.
     */
    var isEnableAppRestart = true
    private var errorActivityClass: Class<out Activity>? = null
    private var restartActivityClass: Class<out Activity>? = null
    private var eventListener: EventListener? = null

    /**
     * Installs CustomActivityOnCrash on the application using the default error activity.
     *
     * @param context Context to use for obtaining the ApplicationContext. Must not be null.
     */
    fun install(context: Context?) {
        try {
            if (context == null) {
                Log.e(TAG, "Install failed: context is null!")
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Log.w(
                        TAG,
                        "CustomActivityOnCrash will be installed, but may not be reliable in API lower than 14"
                    )
                }

                //INSTALL!
                val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                if (oldHandler != null && oldHandler.javaClass.name.startsWith(
                        CAOC_HANDLER_PACKAGE_NAME
                    )
                ) {
                    Log.e(TAG, "You have already installed CustomActivityOnCrash, doing nothing!")
                } else {
                    if (oldHandler != null && !oldHandler.javaClass.name.startsWith(
                            DEFAULT_HANDLER_PACKAGE_NAME
                        )
                    ) {
                        Log.e(
                            TAG,
                            "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use ACRA, Crashlytics or similar libraries, you must initialize them AFTER CustomActivityOnCrash! Installing anyway, but your original handler will not be called."
                        )
                    }
                    application = context.applicationContext as Application

                    //We define a default exception handler that does what we want so it can be called from Crashlytics/ACRA
                    Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { thread, throwable ->
                        Log.e(
                            TAG,
                            "App has crashed, executing CustomActivityOnCrash's UncaughtExceptionHandler",
                            throwable
                        )
                        if (hasCrashedInTheLastSeconds(application)) {
                            Log.e(
                                TAG,
                                "App already crashed in the last 2 seconds, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?",
                                throwable
                            )
                            if (oldHandler != null) {
                                oldHandler.uncaughtException(thread, throwable)
                                return@UncaughtExceptionHandler
                            }
                        } else {
                            setLastCrashTimestamp(application, Date().time)
                            if (errorActivityClass == null) {
                                errorActivityClass = guessErrorActivityClass(application)
                            }
                             if (isLaunchErrorActivityWhenInBackground || !isInBackground) {
                                val intent = Intent(application, errorActivityClass)
                                val sw = StringWriter()
                                val pw = PrintWriter(sw)
                                throwable.printStackTrace(pw)
                                var stackTraceString = sw.toString()

                                //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
                                //The limit is 1MB on Android but some devices seem to have it lower.
                                //See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
                                //And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
                                if (stackTraceString.length > MAX_STACK_TRACE_SIZE) {
                                    val disclaimer = " [stack trace too large]"
                                    stackTraceString = stackTraceString.substring(
                                        0,
                                        MAX_STACK_TRACE_SIZE - disclaimer.length
                                    ) + disclaimer
                                }
                                if (isEnableAppRestart && restartActivityClass == null) {
                                    //We can set the restartActivityClass because the app will terminate right now,
                                    //and when relaunched, will be null again by default.
                                    restartActivityClass = guessRestartActivityClass(application)
                                } else if (!isEnableAppRestart) {
                                    //In case someone sets the activity and then decides to not restart
                                    restartActivityClass = null
                                }
                                intent.putExtra(EXTRA_STACK_TRACE, stackTraceString)
                                intent.putExtra(EXTRA_RESTART_ACTIVITY_CLASS, restartActivityClass)
                                intent.putExtra(EXTRA_SHOW_ERROR_DETAILS, isShowErrorDetails)
                                intent.putExtra(EXTRA_EVENT_LISTENER, eventListener)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                if (eventListener != null) {
                                    eventListener!!.onLaunchErrorActivity()
                                }
                                application!!.startActivity(intent)
                            }
                        }
                        val lastActivity = lastActivityCreated.get()
                        if (lastActivity != null) {
                            //We finish the activity, this solves a bug which causes infinite recursion.
                            //This is unsolvable in API<14, so beware!
                            //See: https://github.com/ACRA/acra/issues/42
                            lastActivity.finish()
                            lastActivityCreated.clear()
                        }
                        killCurrentProcess()
                    })
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        application!!.registerActivityLifecycleCallbacks(object :
                            Application.ActivityLifecycleCallbacks {
                            var currentlyStartedActivities = 0
                            override fun onActivityCreated(
                                activity: Activity,
                                savedInstanceState: Bundle?
                            ) {
                                if (activity.javaClass != errorActivityClass) {
                                    // Copied from ACRA:
                                    // Ignore activityClass because we want the last
                                    // application Activity that was started so that we can
                                    // explicitly kill it off.
                                    lastActivityCreated = WeakReference(activity)
                                }
                            }

                            override fun onActivityStarted(activity: Activity) {
                                currentlyStartedActivities++
                                isInBackground = currentlyStartedActivities == 0
                                //Do nothing
                            }

                            override fun onActivityResumed(activity: Activity) {
                                //Do nothing
                            }

                            override fun onActivityPaused(activity: Activity) {
                                //Do nothing
                            }

                            override fun onActivityStopped(activity: Activity) {
                                //Do nothing
                                currentlyStartedActivities--
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivitySaveInstanceState(
                                activity: Activity,
                                outState: Bundle
                            ) {
                                //Do nothing
                            }

                            override fun onActivityDestroyed(activity: Activity) {
                                //Do nothing
                            }
                        })
                    }
                    Log.i(TAG, "CustomActivityOnCrash has been installed.")
                }
            }
        } catch (t: Throwable) {
            Log.e(
                TAG,
                "An unknown error occurred while installing CustomActivityOnCrash, it may not have been properly initialized. Please report this as a bug if needed.",
                t
            )
        }
    }

    /**
     * Given an Intent, returns if the error details button should be displayed.
     *
     * @param intent The Intent. Must not be null.
     * @return true if the button must be shown, false otherwise.
     */
    fun isShowErrorDetailsFromIntent(intent: Intent): Boolean {
        return intent.getBooleanExtra(EXTRA_SHOW_ERROR_DETAILS, true)
    }

    /**
     * Given an Intent, returns the stack trace extra from it.
     *
     * @param intent The Intent. Must not be null.
     * @return The stacktrace, or null if not provided.
     */
    fun getStackTraceFromIntent(intent: Intent): String {
        return intent.getStringExtra(EXTRA_STACK_TRACE)
    }

    /**
     * Given an Intent, returns several error details including the stack trace extra from the intent.
     *
     * @param context A valid context. Must not be null.
     * @param intent  The Intent. Must not be null.
     * @return The full error details.
     */
    fun getAllErrorDetailsFromIntent(context: Context, intent: Intent): String {
        //I don't think that this needs localization because it's a development string...
        val currentDate = Date()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        //Get build date
        val buildDateAsString = getBuildDateAsString(context, dateFormat)

        //Get app version
        val versionName = getVersionName(context)
        var errorDetails = ""
        errorDetails += "Build version: $versionName \n"
        errorDetails += "Build date: $buildDateAsString \n"
        errorDetails += """Current date: ${dateFormat.format(currentDate)} 
"""
        //Added a space between line feeds to fix #18.
        //Ideally, we should not use this method at all... It is only formatted this way because of coupling with the default error activity.
        //We should move it to a method that returns a bean, and let anyone format it as they wish.
        errorDetails += """Device: $deviceModelName 
 
"""
        errorDetails += "Stack trace:  \n"
        errorDetails += getStackTraceFromIntent(intent)
        return errorDetails
    }

    /**
     * Given an Intent, returns the restart activity class extra from it.
     *
     * @param intent The Intent. Must not be null.
     * @return The restart activity class, or null if not provided.
     */
    fun getRestartActivityClassFromIntent(intent: Intent): Class<out Activity>? {
        val serializedClass = intent.getSerializableExtra(EXTRA_RESTART_ACTIVITY_CLASS)
        return if (serializedClass != null && serializedClass is Class<*>) {
            serializedClass as Class<out Activity>
        } else {
            null
        }
    }

    /**
     * Given an Intent, returns the event listener extra from it.
     *
     * @param intent The Intent. Must not be null.
     * @return The event listener, or null if not provided.
     */
    fun getEventListenerFromIntent(intent: Intent): EventListener? {
        val serializedClass = intent.getSerializableExtra(EXTRA_EVENT_LISTENER)
        return if (serializedClass != null && serializedClass is EventListener) {
            serializedClass
        } else {
            null
        }
    }

    /**
     * Given an Intent, restarts the app and launches a startActivity to that intent.
     * The flags NEW_TASK and CLEAR_TASK are set if the Intent does not have them, to ensure
     * the app stack is fully cleared.
     * Must only be used from your error activity.
     *
     * @param activity The current error activity. Must not be null.
     * @param intent   The Intent. Must not be null.
     */
    @Deprecated(
        """You should use restartApplicationWithIntent(activity, intent, eventListener).
      If you don't want to use an eventListener, just pass null."""
    )
    fun restartApplicationWithIntent(activity: Activity, intent: Intent) {
        restartApplicationWithIntent(activity, intent, null)
    }

    /**
     * Given an Intent, restarts the app and launches a startActivity to that intent.
     * The flags NEW_TASK and CLEAR_TASK are set if the Intent does not have them, to ensure
     * the app stack is fully cleared.
     * If an event listener is provided, the restart app event is invoked.
     * Must only be used from your error activity.
     *
     * @param activity      The current error activity. Must not be null.
     * @param intent        The Intent. Must not be null.
     * @param eventListener The event listener as obtained by calling getEventListenerFromIntent.
     */
    fun restartApplicationWithIntent(
        activity: Activity,
        intent: Intent,
        eventListener: EventListener?
    ) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        eventListener?.onRestartAppFromErrorActivity()
        activity.finish()
        activity.startActivity(intent)
        killCurrentProcess()
    }

    /**
     * Closes the app. Must only be used from your error activity.
     *
     * @param activity The current error activity. Must not be null.
     */
    @Deprecated(
        """You should use closeApplication(activity, eventListener).
      If you don't want to use an eventListener, just pass null."""
    )
    fun closeApplication(activity: Activity) {
        closeApplication(activity, null)
    }

    /**
     * Closes the app.
     * If an event listener is provided, the close app event is invoked.
     * Must only be used from your error activity.
     *
     * @param activity      The current error activity. Must not be null.
     * @param eventListener The event listener as obtained by calling getEventListenerFromIntent.
     */
    fun closeApplication(activity: Activity, eventListener: EventListener?) {
        eventListener?.onCloseAppFromErrorActivity()
        activity.finish()
        killCurrentProcess()
    }
    /// SETTERS AND GETTERS FOR THE CUSTOMIZABLE PROPERTIES
    /**
     * Returns the error activity class to launch when a crash occurs.
     *
     * @return The class, or null if not set.
     */
    fun getErrorActivityClass(): Class<out Activity>? {
        return errorActivityClass
    }

    /**
     * Sets the error activity class to launch when a crash occurs.
     * If null,the default error activity will be used.
     */
    fun setErrorActivityClass(errorActivityClass: Class<out Activity>?) {
        CrashHandlerCore2.errorActivityClass = errorActivityClass
    }

    /**
     * Returns the main activity class that the error activity must launch when a crash occurs.
     *
     * @return The class, or null if not set.
     */
    fun getRestartActivityClass(): Class<out Activity>? {
        return restartActivityClass
    }

    /**
     * Sets the main activity class that the error activity must launch when a crash occurs.
     * If not set or set to null, the default error activity will close instead.
     */
    fun setRestartActivityClass(restartActivityClass: Class<out Activity>?) {
        CrashHandlerCore2.restartActivityClass = restartActivityClass
    }

    /**
     * Returns the event listener to be called when events occur, so they can be reported
     * by the app as, for example, Google Analytics events.
     *
     * @return The event listener, or null if not set.
     */
    fun getEventListener(): EventListener? {
        return eventListener
    }

    /**
     * Sets an event listener to be called when events occur, so they can be reported
     * by the app as, for example, Google Analytics events.
     * If not set or set to null, no events will be reported.
     *
     * @param eventListener The event listener.
     * @throws IllegalArgumentException if the eventListener is an inner or anonymous class
     */
    fun setEventListener(eventListener: EventListener?) {
        require(
            !(eventListener != null && eventListener.javaClass.enclosingClass != null && !Modifier.isStatic(
                eventListener.javaClass.modifiers
            ))
        ) { "The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class." }
        CrashHandlerCore2.eventListener = eventListener
    }
    /// INTERNAL METHODS NOT TO BE USED BY THIRD PARTIES
    /**
     * INTERNAL method that checks if the stack trace that just crashed is conflictive. This is true in the following scenarios:
     * - The application has crashed while initializing (handleBindApplication is in the stack)
     * - The error activity has crashed (activityClass is in the stack)
     *
     * @param throwable     The throwable from which the stack trace will be checked
     * @param activityClass The activity class to launch when the app crashes
     * @return true if this stack trace is conflictive and the activity must not be launched, false otherwise
     */
    private fun isStackTraceLikelyConflictive(
        throwable: Throwable,
        activityClass: Class<out Activity>?
    ): Boolean {
        var throwable = throwable
        do {
            val stackTrace = throwable.stackTrace
            for (element in stackTrace) {
                if (element.className == "android.app.ActivityThread" && element.methodName == "handleBindApplication" || element.className == activityClass!!.name) {
                    return true
                }
            }
        } while (throwable.cause.also { throwable = it!! } != null)
        return false
    }

    /**
     * INTERNAL method that returns the build date of the current APK as a string, or null if unable to determine it.
     *
     * @param context    A valid context. Must not be null.
     * @param dateFormat DateFormat to use to convert from Date to String
     * @return The formatted date, or "Unknown" if unable to determine it.
     */
    private fun getBuildDateAsString(context: Context, dateFormat: DateFormat): String {
        var buildDate: String
        try {
            val ai = context.packageManager.getApplicationInfo(context.packageName, 0)
            val zf = ZipFile(ai.sourceDir)
            val ze = zf.getEntry("classes.dex")
            val time = ze.time
            buildDate = dateFormat.format(Date(time))
            zf.close()
        } catch (e: Exception) {
            buildDate = "Unknown"
        }
        return buildDate
    }

    /**
     * INTERNAL method that returns the version name of the current app, or null if unable to determine it.
     *
     * @param context A valid context. Must not be null.
     * @return The version name, or "Unknown if unable to determine it.
     */
    private fun getVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * INTERNAL method that returns the device model name with correct capitalization.
     * Taken from: http://stackoverflow.com/a/12707479/1254846
     *
     * @return The device model name (i.e., "LGE Nexus 5")
     */
    private val deviceModelName: String
        private get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    /**
     * INTERNAL method that capitalizes the first character of a string
     *
     * @param s The string to capitalize
     * @return The capitalized string
     */
    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    /**
     * INTERNAL method used to guess which activity must be called from the error activity to restart the app.
     * It will first get activities from the AndroidManifest with intent filter <action android:name="cat.ereza.customactivityoncrash.RESTART"></action>,
     * if it cannot find them, then it will get the default launcher.
     * If there is no default launcher, this returns null.
     *
     * @param context A valid context. Must not be null.
     * @return The guessed restart activity class, or null if no suitable one is found
     */
    private fun guessRestartActivityClass(context: Context?): Class<out Activity>? {
        var resolvedActivityClass: Class<out Activity>?

        //If action is defined, use that
        resolvedActivityClass = getRestartActivityClassWithIntentFilter(context)

        //Else, get the default launcher activity
        if (resolvedActivityClass == null) {
            resolvedActivityClass = getLauncherActivity(context)
        }
        return resolvedActivityClass
    }

    /**
     * INTERNAL method used to get the first activity with an intent-filter <action android:name="cat.ereza.customactivityoncrash.RESTART"></action>,
     * If there is no activity with that intent filter, this returns null.
     *
     * @param context A valid context. Must not be null.
     * @return A valid activity class, or null if no suitable one is found
     */
    private fun getRestartActivityClassWithIntentFilter(context: Context?): Class<out Activity>? {
        val searchedIntent = Intent().setAction(INTENT_ACTION_RESTART_ACTIVITY).setPackage(
            context!!.packageName
        )
        val resolveInfos = context.packageManager.queryIntentActivities(
            searchedIntent,
            PackageManager.GET_RESOLVED_FILTER
        )
        if (resolveInfos != null && resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            try {
                return Class.forName(resolveInfo.activityInfo.name) as Class<out Activity>
            } catch (e: ClassNotFoundException) {
                //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the restart activity class via intent filter, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    /**
     * INTERNAL method used to get the default launcher activity for the app.
     * If there is no launchable activity, this returns null.
     *
     * @param context A valid context. Must not be null.
     * @return A valid activity class, or null if no suitable one is found
     */
    private fun getLauncherActivity(context: Context?): Class<out Activity>? {
        val intent = context!!.packageManager.getLaunchIntentForPackage(
            context.packageName
        )
        if (intent != null) {
            try {
                return Class.forName(intent.component!!.className) as Class<out Activity>
            } catch (e: ClassNotFoundException) {
                //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    /**
     * INTERNAL method used to guess which error activity must be called when the app crashes.
     * It will first get activities from the AndroidManifest with intent filter <action android:name="cat.ereza.customactivityoncrash.ERROR"></action>,
     * if it cannot find them, then it will use the default error activity.
     *
     * @param context A valid context. Must not be null.
     * @return The guessed error activity class, or the default error activity if not found
     */
    private fun guessErrorActivityClass(context: Context?): Class<out Activity> {
        var resolvedActivityClass: Class<out Activity>?

        //If action is defined, use that
        resolvedActivityClass = getErrorActivityClassWithIntentFilter(context)

        //Else, get the default launcher activity
        if (resolvedActivityClass == null) {
            resolvedActivityClass = DefaultCrashActivity::class.java
        }
        return resolvedActivityClass
    }

    /**
     * INTERNAL method used to get the first activity with an intent-filter <action android:name="cat.ereza.customactivityoncrash.ERROR"></action>,
     * If there is no activity with that intent filter, this returns null.
     *
     * @param context A valid context. Must not be null.
     * @return A valid activity class, or null if no suitable one is found
     */
    private fun getErrorActivityClassWithIntentFilter(context: Context?): Class<out Activity>? {
        val searchedIntent = Intent().setAction(INTENT_ACTION_ERROR_ACTIVITY).setPackage(
            context!!.packageName
        )
        val resolveInfos = context.packageManager.queryIntentActivities(
            searchedIntent,
            PackageManager.GET_RESOLVED_FILTER
        )
        if (resolveInfos != null && resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            try {
                return Class.forName(resolveInfo.activityInfo.name) as Class<out Activity>
            } catch (e: ClassNotFoundException) {
                //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the error activity class via intent filter, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    /**
     * INTERNAL method that kills the current process.
     * It is used after restarting or killing the app.
     */
    private fun killCurrentProcess() {
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    /**
     * INTERNAL method that stores the last crash timestamp
     *
     * @param timestamp The current timestamp.
     */
    private fun setLastCrashTimestamp(context: Context?, timestamp: Long) {
        context!!.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
            .putLong(
                SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp
            ).commit()
    }

    /**
     * INTERNAL method that gets the last crash timestamp
     *
     * @return The last crash timestamp, or -1 if not set.
     */
    private fun getLastCrashTimestamp(context: Context?): Long {
        return context!!.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
            .getLong(
                SHARED_PREFERENCES_FIELD_TIMESTAMP, -1
            )
    }

    /**
     * INTERNAL method that tells if the app has crashed in the last seconds.
     * This is used to avoid restart loops.
     *
     * @return true if the app has crashed in the last seconds, false otherwise.
     */
    private fun hasCrashedInTheLastSeconds(context: Context?): Boolean {
        val lastTimestamp = getLastCrashTimestamp(context)
        val currentTimestamp = Date().time
        return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < TIMESTAMP_DIFFERENCE_TO_AVOID_RESTART_LOOPS_IN_MILLIS
    }

    /**
     * Interface to be called when events occur, so they can be reported
     * by the app as, for example, Google Analytics events.
     */
    interface EventListener : Serializable {
        fun onLaunchErrorActivity()
        fun onRestartAppFromErrorActivity()
        fun onCloseAppFromErrorActivity()
    }
}
