package top.mcwebsite.novel.data.cache

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.common.ui.utils.FileUtils
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import java.io.*

class BookCache(
    private val bookEntity: BookEntity
): KoinComponent {

    private val context: Context by inject()

    private fun getCachePath(bid: String, title: String): String {
        return FileUtils.getCachePath(context) + bid + File.separator + title
    }

    private fun cacheChapter(bid: String, chapter: Chapter) {
        val file = File(getCachePath(bid, chapter.title))
        // 文件存在，说明缓存成功了
        if (!file.exists()) {
            return
        }
        BufferedWriter(FileWriter(file)).use {
            it.write(chapter.content)
            it.flush()
        }
    }

    private fun getChapter(bid: String, chapter: Chapter) {
        val file = File(getCachePath(bid, chapter.title))
        val sb = StringBuilder()
        FileReader(file).use {
            it.readLines().forEach { sb.append(it) }
        }
        chapter.content = sb.toString()
    }



}