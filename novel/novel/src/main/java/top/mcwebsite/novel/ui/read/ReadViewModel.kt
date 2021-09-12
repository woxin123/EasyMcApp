package top.mcwebsite.novel.ui.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.data.cache.BookCache
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.ui.read.page.PageViewDrawer

@ExperimentalCoroutinesApi
class ReadViewModel(
    private val bookRepository: BookRepositoryManager,
    private val bookDataSource: IBookDatasource,
    private val chapterDatasource: IChapterDatasource,
) : ViewModel(), KoinComponent {

    companion object {
        const val TAG = "ReadViewModel"
    }

    lateinit var bookEntity: BookEntity
        private set

    private val readConfig: ReadConfig by inject()

    private lateinit var bookModel: BookModel

    lateinit var pageProvider: PageProvider


    private var _chapterList = MutableSharedFlow<List<ChapterEntity>>()
    val chapterList = _chapterList.asSharedFlow()
    private var chapters: List<ChapterEntity> = emptyList()

    private val _drawReadPageEvent = MutableSharedFlow<Unit>()
    val drawReadPageEvent = _drawReadPageEvent.asSharedFlow()

    fun setBook(bookEntity: BookEntity) {
        this.bookEntity = bookEntity
        init()
    }

    private fun init() {
        bookModel = bookEntity.transform()

        pageProvider = PageProvider(
            bookEntity,
            requestChapter = {
                chapters[it]
            },
            chapterSize = {
                chapters.size
            },
            requestChapterContent = { chapterPos: Int ->
                var content = ""
                if (readConfig.enableFileCache) {
                    // 先去缓存找
                    content = BookCache.getChapter(bookEntity.bid.toString(), chapters[chapterPos])
                }
                if (content.isBlank()) {
                    bookRepository.getChapterInfo(
                        bookModel,
                        chapters[chapterPos].transformToModel()
                    )
                        .apply {
                            if (readConfig.enableFileCache) {
                                collect {
                                    BookCache.cacheChapter(
                                        bookEntity.bid.toString(),
                                        chapters[chapterPos],
                                        it
                                    )
                                }
                            }
                        }
                } else {
                    flow { emit(content) }
                }
            },
            viewModelScope,
        )

        viewModelScope.launch {

            getBookChapters(bookEntity).collect {
                _chapterList.emit(it)
                initData(it)
            }
        }
    }

    private suspend fun getBookChapters(book: BookEntity): Flow<List<ChapterEntity>> {
        val chapters = chapterDatasource.getChaptersByBid(book.bid)
        return if (chapters.isEmpty()) {
            bookRepository.getBookChapters(bookModel).map { list ->
                val entities = mutableListOf<ChapterEntity>()
                list.forEach { entities.add(it.transformToEntity()) }
                // 顺便存储一下
                chapterDatasource.insert(*list.toTypedArray())
                entities.toList()
            }
        } else {
            flow { emit(chapters) }
        }
    }

    private fun initData(chapterList: List<ChapterEntity>) {
        chapters = chapterList
        pageProvider.initCurChapter()
    }

    fun updateBookEntity() {
        viewModelScope.launch {
            bookDataSource.update(bookEntity)
        }

    }

    fun openChapter(chapterIndex: Int) {
        if (chapterIndex > chapters.size || chapterIndex < 0) {
            // 发送事件
            return
        }
        if (chapterIndex == pageProvider.chapterPos) {
            // 发送事件
            return
        }
        pageProvider.openChapter(chapterIndex)
        viewModelScope.launch {
            _drawReadPageEvent.emit(Unit)
        }
    }


}