package top.mcwebsite.common.ui.utils

import android.content.Context
import java.io.File

object FileUtils {

    fun getCachePath(context: Context): String {
        val cachePath = context.filesDir!!.absolutePath + "/book"
        val cacheFile = File(cachePath)
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        return cachePath
    }

}