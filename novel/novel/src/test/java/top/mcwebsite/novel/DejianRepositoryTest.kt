package top.mcwebsite.novel

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import top.mcwebsite.novel.data.remote.repository.impl.IdejianBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter

class DejianRepositoryTest {

    private val dejianRepository: IdejianBookRepository = IdejianBookRepository()

    @Test
    fun testGetBookInfo() = runBlocking {
        val bookModel = BookModel(
            name = "",
            author = "",
            coverUrl = "",
            introduce = "",
            bookType = "",
            lastChapter = null,
            url = "https://www.idejian.com/book/11687133/",
            chapterUrl = null,
            source = "tadu.com",
            update = null,
            atBookShelf = false
        )

        dejianRepository.getBookInfo(bookModel).collect {
            println(it)
        }
    }

    @Test
    fun testSearchBook() = runBlocking {
        dejianRepository.searchBook("龙族", 0, 0).collect {
            println(it)
        }
    }

    @Test
    fun testGetChapters() = runBlocking {
        val bookModel = BookModel(
            name = "",
            author = "",
            coverUrl = "",
            introduce = "",
            bookType = "",
            lastChapter = null,
            url = "https://www.idejian.com/book/11687133/",
            chapterUrl = null,
            source = "tadu.com",
            update = null,
            atBookShelf = false
        )
        dejianRepository.getBookChapters(bookModel).collect {
            println(it)
        }
    }

    @Test
    fun testChapterInfo() = runBlocking {
        val chapter = Chapter(index = 223, title = "第1章 星辰的礼物", url = "https://www.idejian.com/book/11756307/5.html")
        val bookModel = BookModel(
            name = "",
            author = "",
            coverUrl = "",
            introduce = "",
            bookType = "",
            lastChapter = null,
            url = "https://www.idejian.com/book/11687133/",
            chapterUrl = null,
            source = "tadu.com",
            update = null,
            atBookShelf = false
        )
        dejianRepository.getChapterInfo(bookModel, chapter).collect {
            println(it)
        }
    }
}