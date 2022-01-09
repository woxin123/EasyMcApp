package top.mcwebsite.novel.data.local.datasource

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.BookModel

/**
 * 如果以后有多个数据源可以抽象一个 Repository，但是现在没有必要
 */
interface IBookDatasource {

    suspend fun getBookAll(): Flow<List<BookEntity>>

    suspend fun isExistByUrl(url: String): Boolean

    suspend fun insert(bookModel: BookModel)

    suspend fun update(bookEntity: BookEntity)

    suspend fun removeBooks(books: List<BookEntity>)

}