package top.mcwebsite.common.ui.utils

import android.content.Context

object FileUtils {

    fun getCachePath(context: Context): String {
        return context.externalCacheDir!!.absolutePath
    }

}