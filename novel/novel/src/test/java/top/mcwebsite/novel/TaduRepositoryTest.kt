package top.mcwebsite.novel

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter

class TaduRepositoryTest {

    private val taduRepositoy: TaduBookRepository = TaduBookRepository()

    companion object {
        private const val SEARCH_KEY = "龙族"
    }

    @Test
    fun searchBook() = runBlocking {
        taduRepositoy.searchBook(SEARCH_KEY, 0, 10).collect {
            println(it.toString())
        }
    }

    @Test
    fun getBookInfo() = runBlocking {
        val bookModel = BookModel(
            name = "龙族IV",
            author = "独尊Vip",
            coverUrl = "",
            introduce = "",
            bookType = "东方玄幻",
            lastChapter = null,
            url = "http://www.tadu.com/book/597817/",
            chapterUrl = null,
            source = "tadu.com",
            update = null,
            atBookShelf = false
        )
        taduRepositoy.getBookInfo(bookModel).collect {
            println(it.toString())
        }
    }

    @Test
    fun getChapterInfo() = runBlocking {
        val bookModel = BookModel(
            name = "龙族IV",
            author = "独尊Vip",
            coverUrl = "",
            introduce = "",
            bookType = "东方玄幻",
            lastChapter = null,
            url = "http://www.tadu.com/book/597817/",
            chapterUrl = null,
            source = "tadu.com",
            update = null,
            atBookShelf = false
        )
        val chapter = Chapter(
            index = 0,
            title = "龙血沸腾",
            url = "http://www.tadu.com/book/597817/77701629/",
        )
        taduRepositoy.getChapterInfo(bookModel, chapter).collect {
            println(it.toString())
        }
    }
}

