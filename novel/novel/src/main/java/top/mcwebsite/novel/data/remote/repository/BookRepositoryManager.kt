package top.mcwebsite.novel.data.remote.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import java.lang.IllegalStateException

class BookRepositoryManager : IBookRepository, KoinComponent{

    private val yb3BookRepository by inject<Yb3BookRepository>()

    private val taduBookRepository by inject<TaduBookRepository>()

    private val repositories = mutableListOf<IBookRepository>()

    init {
        repositories.add(yb3BookRepository)
        repositories.add(taduBookRepository)
    }


    override suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        return getRepositoryBySource(book.source).getBookInfo(book)
    }

    override suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        val res = repositories.map { it.searchBook(key, page, pageSize) }.toTypedArray()
        return combine(*res) { arrays ->
            // 优化排序
            val list = mutableListOf<BookModel>()
            arrays.forEach { list.addAll(it) }
            list
        }
    }

    override suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        return getRepositoryBySource(book.source).getBookChapters(book)
    }

    override suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<Chapter> {
        return getRepositoryBySource(book.source).getChapterInfo(book, chapter)
    }

    private fun getRepositoryBySource(source: String) : IBookRepository {
        return when (source) {
            Yb3BookRepository.source -> yb3BookRepository
            TaduBookRepository.source -> taduBookRepository
            else -> throw IllegalStateException("unknown source")
        }
    }
}