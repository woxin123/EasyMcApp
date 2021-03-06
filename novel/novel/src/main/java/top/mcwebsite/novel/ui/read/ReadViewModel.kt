package top.mcwebsite.novel.ui.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.data.cache.BookCache
import top.mcwebsite.novel.data.cache.isCached
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.BookModel

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
    var chapters: List<ChapterEntity> = emptyList()

    private val _drawReadPageEvent = MutableSharedFlow<Unit>()
    val drawReadPageEvent = _drawReadPageEvent.asSharedFlow()

    private val _menuStatus = MutableStateFlow(false)
    val menuStatus = _menuStatus.asStateFlow()

    private val _bookMenuStatus = MutableStateFlow(false)
    val bookMenuStatus = _bookMenuStatus.asStateFlow()

    private val _openChapter = MutableSharedFlow<Int>()
    val openChapter = _openChapter.asSharedFlow()

    fun setBook(bookEntity: BookEntity) {
        this.bookEntity = bookEntity
        init()
    }

    private fun init() {
        bookModel = bookEntity.transform()
        viewModelScope.launch(Dispatchers.IO) {
            bookEntity.isUpdate = false
            bookDataSource.update(bookEntity)
        }

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
                    // ???????????????
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
        val chapters = chapterDatasource.getChaptersByBid(book.bid!!)
        Log.e(TAG, "book = $book chapters = $chapters")
        return if (chapters.isEmpty()) {
            bookRepository.getBookChapters(bookModel).map { list ->
                val entities = mutableListOf<ChapterEntity>()
                list.forEach { entities.add(it.transformToEntity(book.bid!!)) }
                // ??????????????????
                chapterDatasource.insert(*entities.toTypedArray())
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
            bookEntity.lastReadTime = System.currentTimeMillis()
            bookDataSource.update(bookEntity)
        }
    }

    fun openChapter(chapterIndex: Int) {
        if (chapterIndex > chapters.size || chapterIndex < 0) {
            // ????????????
            return
        }
        if (chapterIndex == pageProvider.chapterPos) {
            // ????????????
            return
        }
        pageProvider.openChapter(chapterIndex)
        viewModelScope.launch {
            _openChapter.emit(chapterIndex)
            _drawReadPageEvent.emit(Unit)
        }
    }

    fun changeMenuStatus(isShow: Boolean) {
        viewModelScope.launch {
            _menuStatus.emit(isShow)
        }
    }

    fun changeBookMenuStatus(isShow: Boolean) {
        viewModelScope.launch {
            _bookMenuStatus.emit(isShow)
            if (isShow) {
                _menuStatus.emit(false)
            }
        }
    }

    fun download() {
        viewModelScope.launch {
            chapters.forEach { chapterEntity ->
                if (!chapterEntity.isCached(bookEntity.bid.toString())) {
                    bookRepository.getChapterInfo(bookModel, chapterEntity.transformToModel())
                        .collect {
                            BookCache.cacheChapter(bookEntity.bid.toString(), chapterEntity, it)
                        }
                }
            }
        }
    }
}
