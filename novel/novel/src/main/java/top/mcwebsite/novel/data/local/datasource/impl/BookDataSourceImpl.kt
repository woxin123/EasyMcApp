package top.mcwebsite.novel.data.local.datasource.impl

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.db.dao.BookDao
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.BookModel

class BookDataSourceImpl(
    private val bookDao: BookDao
) : IBookDatasource {
    override suspend fun getBookAll(): Flow<List<BookEntity>> {
        return bookDao.getAll()
    }

    override suspend fun isExistByUrl(url: String): Boolean {
        return bookDao.isExistsByUrl(url)
    }

    override suspend fun insert(bookModel: BookModel) {
        bookDao.insert(bookModel.transformToEntity())
    }

    override suspend fun update(bookEntity: BookEntity) {
        bookDao.update(bookEntity)
    }

    override suspend fun removeBooks(books: List<BookEntity>) {
        bookDao.delete(books)
    }
}