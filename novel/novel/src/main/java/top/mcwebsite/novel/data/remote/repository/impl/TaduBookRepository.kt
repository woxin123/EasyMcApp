package top.mcwebsite.novel.data.remote.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import top.mcwebsite.novel.api.ITaduApi
import top.mcwebsite.novel.data.remote.repository.IBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.net.RetrofitFactory

class TaduBookRepository() : IBookRepository {

    companion object {
        const val baseUrl = "http://www.tadu.com"

        val source = "tadu.com"
    }

    private val retrofit by lazy {
        RetrofitFactory.factory(baseUrl).create(ITaduApi::class.java)
    }

    override suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        val result = retrofit.getBookInfo(book.url)
        return parseBookInfo(result, book)
    }

    override suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        val result = retrofit.searchBook(mapOf("query" to key))
        return parseSearchBook(result)
    }

    override suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<Chapter> {
        TODO("Not yet implemented")
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
                element.getElementsByClass("bookImg")[0].getElementsByTag("img").attr("src")

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
}