package top.mcwebsite.novel.data.remote.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.remote.repository.impl.IdejianBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.model.RankCategory
import java.lang.IllegalStateException

class BookRepositoryManager :  KoinComponent {

    private val yb3BookRepository by inject<Yb3BookRepository>()

    private val taduBookRepository by inject<TaduBookRepository>()

    private val idejianBookRepository by inject<IdejianBookRepository>()

    private val repositories = mutableListOf<IBookRepository>()
    private val searchRepositories = mutableListOf<IBookRepository>()

    init {
        repositories.add(yb3BookRepository)
        repositories.add(taduBookRepository)
        repositories.add(idejianBookRepository)
        searchRepositories.addAll(repositories)
    }


    suspend fun getBookInfo(book: BookModel): Flow<BookModel> {
        return getRepositoryBySource(book.source).getBookInfo(book)
    }

    suspend fun searchBook(key: String, page: Int, pageSize: Int): Flow<List<BookModel>> {
        val res = searchRepositories
            .map {
                it.searchBook(key, page, pageSize)
            }
            .toTypedArray()
        return combine(*res) { arrays ->
            // 优化排序
            val list = mutableListOf<BookModel>()
            arrays.forEach {
                list.addAll(it)
            }
            list
        }
    }

    suspend fun getBookChapters(book: BookModel): Flow<List<Chapter>> {
        return getRepositoryBySource(book.source).getBookChapters(book)
    }

    suspend fun getChapterInfo(book: BookModel, chapter: Chapter): Flow<String> {
        return getRepositoryBySource(book.source).getChapterInfo(book, chapter)
    }

    suspend fun getRankList(source: String): Flow<List<RankCategory>> {
        return getRepositoryBySource(source).getRankList()
    }

    private fun getRepositoryBySource(source: String): IBookRepository {
        return when (source) {
            Yb3BookRepository.source -> yb3BookRepository
            TaduBookRepository.source -> taduBookRepository
            IdejianBookRepository.SOURCE -> idejianBookRepository
            else -> throw IllegalStateException("unknown source")
        }
    }

    fun removeSearchBookRepositoryBySource(source: String) {
        searchRepositories.remove(getRepositoryBySource(source))
    }
}