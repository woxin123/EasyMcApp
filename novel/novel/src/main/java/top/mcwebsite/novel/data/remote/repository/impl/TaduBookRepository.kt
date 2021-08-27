package top.mcwebsite.novel.data.remote.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import top.mcwebsite.novel.api.ITaduApi
import top.mcwebsite.novel.data.remote.repository.IBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.data.remote.net.RetrofitFactory

class TaduBookRepository() : IBookRepository {

    companion object {
        const val baseUrl = "http://www.tadu.com"

        val source = "tadu.com"
    }

    private val taudApi by lazy {
        RetrofitFactory.factory(baseUrl).create(ITaduApi::class.java)
    }

    override suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        val result = taudApi.getBookInfo(book.url)
        return parseBookInfo(result, book)
    }

    override suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        val result = taudApi.searchBook(mapOf("query" to key))
        return parseSearchBook(result)
    }

    override suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        if (book.chapterUrl.isNullOrBlank()) {
            return flow { error("book chapter 为空") }
        }
        val result = taudApi.getChapterList(book.chapterUrl!!)
        return parseBookChapters(result)
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<Chapter> {
        val result = taudApi.getChapterInfo(chapter.url)
        return parseChapterInfo(chapter, result)
    }

    private fun parseSearchBook(html: String): Flow<List<BookModel>> {
        return flow {
            try {
                val books = mutableListOf<BookModel>()
                val doc = Jsoup.parse(html)
                val elements = doc.getElementsByClass("bookList")[0].getElementsByTag("li")
                if (elements.isNullOrEmpty()) {
                    emit(emptyList<BookModel>())
                }
                for (elem in elements) {
                    books.add(
                        BookModel(
                            name = parseSearchBookName(elem.getElementsByClass("bookNm")),
                            coverUrl = elem.getElementsByClass("bookImg")[0].getElementsByTag("img")
                                .attr("data-src"),
                            author = elem.getElementsByClass("authorNm")[0].text(),
                            bookType = elem.getElementsByClass("bot_list")[0].getElementsByTag("span")[0].text(),
                            lastChapter = null,
                            url = baseUrl + elem.getElementsByClass("bookImg")[0].attr("href"),
                            source = source,
                            update = null,
                        )
                    )
                }
                emit(books)
            } catch (e: Exception) {
                e.printStackTrace()
                error(e)
            }
        }
    }

    private fun parseSearchBookName(elements: List<Element>): String {
        val sb = StringBuilder()
        for (elem in elements) {
            sb.append(elem.text().trim())
        }
        return sb.toString()
    }

    private fun parseBookInfo(html: String, book: BookModel): Flow<BookModel> {
        return flow {
            val doc = Jsoup.parse(html)
            val element = doc.getElementsByClass("bookIntro")[0]
             book.coverUrl =
                element.getElementsByClass("bookImg")[0].getElementsByTag("img").attr("data-src")

            try {
                book.update =
                    doc.getElementsByClass("newUpdate")[0].getElementsByTag("span").text().trim()
                        .replace(" ", "").replace("  ", "").substring(5)
                book.lastChapter = doc.getElementsByClass("newUpdate")[0].getElementsByTag("a").text().toString().trim()
            } catch (e: Exception) {
                book.update = null
                book.lastChapter = null
            }

            val introElements = element.getElementsByTag("p")[0].textNodes()
            val sb = StringBuilder()
            for ((index, elem) in introElements.withIndex()) {
                val temp = elem.text().trim()
                    .replace(" ", "").replace(" ", "")
                if (temp.isNotEmpty()) {
                    sb.append("\u3000\u3000" + temp)
                    if (index < introElements.size - 1) {
                        sb.append("\r\n")
                    }
                }
            }
            book.introduce = sb.toString()
            book.chapterUrl = baseUrl + element.getElementsByClass("readBtn")[0].getElementsByTag("a")[1].attr("href")
            emit(book)
        }
    }

    private fun parseBookChapters(html: String): Flow<List<Chapter>> {
        return flow {
            val doc = Jsoup.parse(html)
            val elements = doc.getElementsByClass("chapter")[0].getElementsByTag("a")
            val chapters = mutableListOf<Chapter>()
            for ((index, ele) in elements.withIndex()) {
                val url = baseUrl + ele.attr("href").trim()
                val title = ele.text()
                val chapter = Chapter(index, title, url, "")
                chapters.add(chapter)
            }
            emit(chapters)
        }
    }

    private fun parseChapterInfo(chapter: Chapter, html: String): Flow<Chapter> {
        return flow {
            val docHtml = Jsoup.parse(html)
            val realChapterUrl = docHtml.getElementById("bookPartResourceUrl").attr("value")
            val result = taudApi.getChapterInfo(realChapterUrl)
            val realHtmlContent = result.replace("callback{content:'", "<html><body>")
                .replace("'}", "</body></html>")
            val doc = Jsoup.parse(realHtmlContent)
            val ps = doc.getElementsByTag("p")
            val sb = StringBuilder()
            for ((i, ele) in ps.withIndex()) {
                val text = ele.text().trim()
                text.replace(" ", "").replace(" ", "")
                if (text.isNotEmpty()) {
                    sb.append("\u3000\u3000" + text)
                    if (i < ps.size - 1) {
                        sb.append("\r\n")
                    }
                }
            }
            chapter.content = sb.toString()
            emit(chapter)
        }
    }
}