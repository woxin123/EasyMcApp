package top.mcwebsite.novel.data.remote.repository

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter

interface IBookRepository {
    suspend fun getBookInfo(book: BookModel): Flow<BookModel>

    suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>>

    suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>>

    suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<String>
}