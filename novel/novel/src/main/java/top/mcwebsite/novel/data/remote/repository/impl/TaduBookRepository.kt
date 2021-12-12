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
import top.mcwebsite.novel.data.remote.repository.exception.BookSourceException
import top.mcwebsite.novel.model.RankCategory

class TaduBookRepository() : IBookRepository {

    companion object {
        const val baseUrl = "https://www.tadu.com"

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
        return try {
            val result = taudApi.searchBook(mapOf("query" to key))
            parseSearchBook(result)
        } catch (e: Exception) {
            flow {
                throw BookSourceException(source, e)
            }
        }
    }

    override suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        if (book.chapterUrl.isNullOrBlank()) {
            return flow { error("book chapter 为空") }
        }
        val result = taudApi.getChapterList(book.chapterUrl!!)
        return parseBookChapters(result)
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<String> {
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
                    emit(emptyList())
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
                book.lastChapter = doc.selectFirst("#content > div.boxCenter.box.clearfix > div.lf > div > div:nth-child(2) > div.upDate > a").text()
                book.update = doc.selectFirst("#content > div.boxCenter.box.clearfix > div.lf > div > div:nth-child(2) > div.upDate > span").text()
            } catch (e: Exception) {
                book.update = null
                book.lastChapter = null
            }

            val introElements = doc.getElementsByClass("boxT")[0].getElementsByTag("p")[0].textNodes()
            val sb = StringBuilder()
            for ((index, elem) in introElements.withIndex()) {
                val temp = elem.text().trim()
                    .replace(" ", "").replace(" ", "")
                if (temp.isNotEmpty()) {
                    sb.append("" + temp)
                    if (index < introElements.size - 1) {
                        sb.append("\r\n")
                    }
                }
            }
            book.introduce = sb.toString()
            book.chapterUrl = book.url
            emit(book)
        }
    }

    private fun parseBookChapters(html: String): Flow<List<Chapter>> {
        return flow {
            val doc = Jsoup.parse(html)
            val eme = doc.selectFirst("#content > div.boxCenter.boxT.clearfix > div.lf.lfT")
            val aItem = eme.getElementsByTag("a")
            val chapters = mutableListOf<Chapter>()
            for ((index, item) in aItem.withIndex()) {
                val title = item.text()
                val url = baseUrl + item.attr("href")
                val chapter = Chapter(index, title, url)
                chapters.add(chapter)
            }
            emit(chapters)
        }
    }

    private fun parseChapterInfo(chapter: Chapter, html: String): Flow<String> {
        return flow {
            val docHtml = Jsoup.parse(html)
            val realChapterUrl = docHtml.getElementById("bookPartResourceUrl").attr("values")
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
                    sb.append(text)
                    if (i < ps.size - 1) {
                        sb.append("\r\n")
                    }
                }
            }

            emit(sb.toString())
        }
    }

    override suspend fun getRankList(): Flow<List<RankCategory>> {
        TODO("Not yet implemented")
    }
}