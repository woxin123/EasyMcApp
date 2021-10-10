package top.mcwebsite.crash.config

import android.app.Activity
import android.content.Context
import top.mcwebsite.crash.activity.DefaultCrashActivity
import java.io.Serializable

data class CrashConfig(
    val enable: Boolean = true,
    val crashIntervalTimeMs: Long = 1000,
    val errorCrashActivityClass: Class<out Activity> = DefaultCrashActivity::class.java,
    val backgroundMode: Int = BACKGROUND_MODE_SHOW_CUSTOM,
    val isShowRestartButton: Boolean = true,
    var restartActivityClass: Context? = null,
) : Serializable {

    companion object {
        const val BACKGROUND_MODE_SILENT = 0
        const val BACKGROUND_MODE_SHOW_CUSTOM = 1
        const val BACKGROUND_MODE_CRASH = 2
    }

}