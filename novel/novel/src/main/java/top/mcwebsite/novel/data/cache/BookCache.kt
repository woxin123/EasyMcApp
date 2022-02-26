package top.mcwebsite.novel.data.cache

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object BookCache: KoinComponent {

    private val context: Context by inject()

    fun getCachePath(bid: String, title: String): String {
        val file = File(getCachePath(context) + File.separator + bid)
        if (!file.exists()) {
            file.mkdirs()
        }
        return getCachePath(context) + File.separator + bid + File.separator + title
    }

    fun cacheChapter(bid: String, chapter: ChapterEntity, content: String) {
        val file = File(getCachePath(bid, chapter.title))
        // 文件存在，说明缓存成功了
        if (file.exists()) {
            return
        }
        BufferedWriter(FileWriter(file)).use {
            it.write(content)
            it.flush()
        }
    }

    fun getChapter(bid: String, chapter: ChapterEntity): String {
        val file = File(getCachePath(bid, chapter.title))
        if (!file.exists()) {
            return ""
        }
        val sb = StringBuilder()
        FileReader(file).use {
            it.readLines().forEach { sb.append(it + "\n") }
        }
        return sb.toString()
    }

    private fun getCachePath(context: Context): String {
        val cachePath = context.filesDir!!.absolutePath + "/book"
        val cacheFile = File(cachePath)
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        return cachePath
    }
}

fun ChapterEntity.isCached(bid: String): Boolean {
    val file = File(BookCache.getCachePath(bid, this.title))
    return file.exists() && file.length() > 0
}
