package top.mcwebsite.novel.data.remote.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import top.mcwebsite.novel.api.IIdejianAPI
import top.mcwebsite.novel.data.remote.net.RetrofitFactory
import top.mcwebsite.novel.data.remote.repository.IBookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.model.RankCategory
import java.lang.StringBuilder
import java.net.URL

class IdejianBookRepository : IBookRepository {

    companion object {
        const val BASE_URL = "https://www.idejian.com"
        const val SOURCE = "idejian.com"
    }

    private val dejianApi by lazy {
        RetrofitFactory.factory(BASE_URL).create(IIdejianAPI::class.java)
    }

    override suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        val html = dejianApi.getBookInfo(book.url)
        return parseBookInfo(html, book)
    }

    private fun parseBookInfo(html: String, book: BookModel): Flow<BookModel> {
        return flow {
            val document = Jsoup.parse(html)
//            println(html)
            val detailBook = document.getElementsByClass("detail_bkinfo")[0]
            val bookCover = detailBook.getElementsByTag("img")[0].attr("src")
            val bookName = detailBook.select("dl.detail_center_info dt div.detail_bkname a").text()
            val author =
                detailBook.select("dl.detail_center_info dt div.detail_bkauthor").text().run {
                    if (endsWith("著")) {
                        this.substring(0, this.length - 1)
                    } else {
                        this
                    }
                }
            val bookType =
                detailBook.select("dl.detail_center_info dd.detail_bkgrade span")[2].text()
            val lastChapter = document.select(
                "body div.man_wrap.man_detail_wrap " +
                        "div.main div.border_box.bk_detail div.book_page div.link_name a"
            ).text()
            val bookIntroduce = document.select(
                "body div.man_wrap.man_detail_wrap" +
                        " div.main div.border_box div.bk_brief div.brief_con"
            ).text()
            println("$bookCover $bookName $author")
            book.lastChapter = lastChapter
            book.bookType = bookType
            book.coverUrl = bookCover
            book.introduce = bookIntroduce
            book.chapterUrl = book.url
            emit(book)
        }
    }

    override suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        return flow {
            val html = dejianApi.searchBook(key)
            val books = parseSearchBook(html)
            emit(books)
        }
    }

    private fun parseSearchBook(html: String): List<BookModel> {
        val doc = Jsoup.parse(html)
        val bookElements =
            doc.select("body div.man_wrap.man_search_wrap div.main div.search_list ul.rank_ullist li")
        val books = mutableListOf<BookModel>()
        for (ele in bookElements) {
            val item = ele.getElementsByClass("rank_items")[0]
            val bookCover = item.select("div.items_l a.book_img img[src]").attr("src")
            val bookNameNode = item.select("div.items_center div.rank_bkname a")
            val bookName = bookNameNode.text()
            val bookUrl = bookNameNode.attr("href")
            val author = item.select("div.items_center div.rank_bkinfo span.author").text()
            val introduce = item.select("div.items_center div.rank_bkbrief").text()
            val lastChapter =
                item.select("div.items_center div.rank_bkother div.rank_newpage a").text().run {
                    if (startsWith("更新章节：")) {
                        this.substring(5)
                    } else {
                        this
                    }
                }
            val updateTime = item.select("div.items_center div.rank_bkother div.rank_bktime").text()
            val bookModel = BookModel(
                name = bookName,
                author = author,
                coverUrl = bookCover,
                introduce = introduce,
                bookType = null,
                url = BASE_URL + bookUrl,
                chapterUrl = null,
                lastChapter = lastChapter,
                source = SOURCE,
                update = updateTime,
                atBookShelf = false,
            )
            books.add(bookModel)
        }
        return books
    }

    override suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        return flow {
            val html = dejianApi.getBookInfo(book.url)
            val pageNum = parseChapterNum(html)
            emit(parseChapters(book, pageNum))
        }
    }

    private fun parseChapterNum(html: String): Int {
        val doc = Jsoup.parse(html)
        return doc.getElementsByClass("catelog_list")[0].attr("data-size").toInt()
    }

    private suspend fun parseChapters(book: BookModel, pageNum: Int): List<Chapter> {
        val id = URL(book.url).path.split("/")[2]
        val chapters = mutableListOf<Chapter>()
        var chapterIndex = 0
        for (i in 0..pageNum) {
            val chapterPage = dejianApi.getChapterLists(id, i)
            val doc = Jsoup.parse(chapterPage.html)
            for ((index, ele) in doc.getElementsByTag("li").withIndex()) {
                val url = BASE_URL + ele.getElementsByTag("a")[0].attr("href")
                val title = ele.text()
                chapters.add(Chapter(chapterIndex++, title, url))
            }
        }
        return chapters
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<String> {
        return flow {
            val html = dejianApi.getChapterInfo(chapter.url)
            emit(parseChapterInfo(html))
        }
    }

    private fun parseChapterInfo(html: String): String {
        val doc = Jsoup.parse(html)
        val bodyNodes = doc.getElementsByClass("h5_mainbody")
        val sb = StringBuilder()
        for (bodyNode in bodyNodes) {
            val textNodes = bodyNode.getElementsByClass("bodytext")
            for (node in textNodes) {
                sb.append(node.text() + "\n")
            }
        }
        return sb.toString()
    }

    override suspend fun getRankList(): Flow<List<RankCategory>> {
        TODO("Not yet implemented")
    }
}