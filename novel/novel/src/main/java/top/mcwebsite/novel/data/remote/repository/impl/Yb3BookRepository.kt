package top.mcwebsite.novel.data.remote.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import top.mcwebsite.novel.api.IYb3Api
import top.mcwebsite.novel.data.remote.repository.IBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.data.remote.net.RetrofitFactory
import top.mcwebsite.novel.data.remote.repository.exception.BookSourceException

class Yb3BookRepository  : IBookRepository {

    companion object {
        const val baseUrl = "https://www.yb3.cc"
        val source = "yb3.cc"
    }

    private val retrofit by lazy {
        RetrofitFactory.factory(baseUrl).create(IYb3Api::class.java)
    }


    override suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        val result = retrofit.getBookInfo(book.url)
        return parseBookInfo(result, book)
    }

    override suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        return try {
            val result = retrofit.searchBook(key)
            parseSearchBook(result)
        } catch (e: Exception) {
            flow {
                throw BookSourceException(source, e)
            }
        }
    }

    override suspend fun getBookChapters(bookModel: BookModel): Flow<List<Chapter>> {
        val result = retrofit.getChapterLists(bookModel.chapterUrl!!)
        return parseBookChapters(result)
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<String> {
        val result = retrofit.getChapterInfo(chapter.url)
        return parseChapter(chapter, result)
    }

    private fun parseSearchBook(html: String): Flow<List<BookModel>> {
        return flow {
            val doc = Jsoup.parse(html)
            val bookElements = doc.getElementsByClass("novelslist2")[0].getElementsByTag("li")
            val bookList = mutableListOf<BookModel>()
            if (bookElements != null && bookElements.size > 1) {
                for ((index, bookElement) in bookElements.withIndex()) {
                    if (index == 0) continue
                    bookList.add(
                        BookModel(
                            name = bookElement.getElementsByClass("s2")[1].getElementsByTag("a")
                                .text(),
                            coverUrl = null,
                            url = baseUrl + bookElement.getElementsByClass("s2")[1].getElementsByTag(
                                "a"
                            )[0].attr("href"),
                            author = bookElement.getElementsByClass("s4")[0].text(),
                            bookType = bookElement.getElementsByClass("s2")[0].text(),
                            lastChapter = bookElement.getElementsByClass("s3")[0].getElementsByTag("a")[0].text(),
                            source = source,
                            update = bookElement.getElementsByClass("s5")[0].text()
                        )
                    )
                }
            }
            emit(bookList)
        }
    }

    private fun parseBookInfo(html: String, bookModel: BookModel): Flow<BookModel> {
        return flow {
            val doc = Jsoup.parse(html)
            val bookElement = doc.getElementsByClass("box_con")[0]
            bookModel.coverUrl =
                bookElement.getElementById("fmimg").getElementsByTag("img")[0].attr("src")
            val introElements = bookElement.getElementById("intro").textNodes()
            val intro = StringBuilder()
            for ((index, elem) in introElements.withIndex()) {
                val temp = elem.text().trim()
                    .replace(" ", "").replace("  ", "");
                if (temp.isNotEmpty()) {
                    intro.append("\u3000\u3000" + temp)
                    if (index < introElements.size - 1) {
                        intro.append("\r\n")
                    }

                }
            }
            bookModel.bookType = doc.getElementsByClass("box_con")[0].getElementsByTag("a")[1].text()
            bookModel.introduce = intro.toString()
            bookModel.update =
                bookElement.getElementById("info").getElementsByTag("p")[2].text().toString().trim()
                    .substring(5)
            bookModel.lastChapter = bookElement.getElementById("info")
                .getElementsByTag("p")[3].getElementsByTag("a")[0].text()
            bookModel.chapterUrl = bookModel.url
            emit(bookModel)
        }
    }

    private fun parseBookChapters(html: String): Flow<List<Chapter>> {
        return flow {
            val chapterList = mutableListOf<Chapter>()
            val doc = Jsoup.parse(html)
            val chapterElemList = doc.getElementById("list").getElementsByTag("dd")
            for ((index, elem) in chapterElemList.withIndex()) {
                chapterList.add(
                    Chapter(
                        title = elem.getElementsByTag("a")[0].text(),
                        index = index,
                        url = baseUrl + elem.getElementsByTag("a")[0].attr("href"),
                    )
                )
            }
            emit(chapterList)
        }
    }

    private fun parseChapter(chapter: Chapter, html: String): Flow<String> {
        return flow {
            val doc = Jsoup.parse(html)
            val elements = doc.getElementById("content").textNodes()
            val content = StringBuilder()
            for ((index, elem) in elements.withIndex()) {
                val temp = elem.text().trim()
                    .replace(" ", "").replace(" ", "");
                if (temp.isNotEmpty()) {
                    content.append("" + temp)
                    if (index < elements.size - 1) {
                        content.append("\r\n")
                    }
                }
            }
            emit(
                content.toString()
            )
        }
    }
}
