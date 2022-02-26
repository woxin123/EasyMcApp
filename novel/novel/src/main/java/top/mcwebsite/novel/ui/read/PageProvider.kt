package top.mcwebsite.novel.ui.read

import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.model.Page
import top.mcwebsite.novel.ui.read.page.PageViewDrawer
import java.util.concurrent.atomic.AtomicInteger

/**
 * 用来提供 page
 */
class PageProvider(
    private val bookEntity: BookEntity,
    private val requestChapter: (Int) -> ChapterEntity,
    private val chapterSize: () -> Int,
    private val requestChapterContent: suspend (Int) -> Flow<String>,
    private val scope: CoroutineScope,
) {

    companion object {
        const val NEXT_PAGE = 1
        const val PRE_PAGE = -1
    }

    private val positionChangeEvent: MutableStateFlow<Int> =
        MutableStateFlow(bookEntity.lastReadPosition)

    private val _chapterPosChangeEvent: MutableStateFlow<Int> =
        MutableStateFlow(bookEntity.lastReadChapterPos)

    val chapterPosChangeEvent = _chapterPosChangeEvent.asStateFlow()

    var position = 0
    var chapterPos = 0

    /**
     * 0 表示没有翻页
     * 1 表示下一页
     * -1 表示上一页
     */
    var lastTurnPageState = 0

    /**
     * lru cache
     */
    private val cache = LruCache<Int, Pair<List<Page>, Int>>(20)

    lateinit var pageDrawer: PageViewDrawer

    private val _curChapterLoadedEvent = MutableSharedFlow<Int>(replay = 1)

    val curChapterLoadedEvent = _curChapterLoadedEvent.asSharedFlow()

    private var curPageList: List<Page> = emptyList()

    // 这个变量用于表示当前 page 的版本，如果 page 的版本更新了，那么当前缓存中的 page 就需要重新计算了
    private val pageVersion: AtomicInteger = AtomicInteger(0)

    init {
        if (bookEntity.lastReadChapterPos == -1) {
            _chapterPosChangeEvent.value = 0
            chapterPos = 0
        }
        scope.launch {
            positionChangeEvent.collect {
                position = it
                bookEntity.lastReadPosition = it
            }
        }
        scope.launch {
            _chapterPosChangeEvent.collect {
                chapterPos = it
                bookEntity.lastReadChapterPos = it
                if (chapterSize() > bookEntity.lastReadChapterPos) {
                    bookEntity.lastReadChapterTitle = requestChapter(it).title
                }
            }
        }
    }

    private fun adjustCurPageList() {
        curPageList = getChapterPage(chapterPos) ?: throw IllegalStateException("错误的状态")
        if (position == Int.MIN_VALUE) {
            positionChangeEvent.value = curPageList.size - 1
        }
        if (position == Int.MAX_VALUE) {
            positionChangeEvent.value = 0
        }
        pageDrawer.status = PageViewDrawer.STATUS_FINISH
    }

    fun initCurChapter() {
        curPageList = getChapterPage(chapterPos) ?: emptyList()
    }

    private fun getChapterPage(chapterPosition: Int): List<Page>? {
        if (chapterPosition < 0 || chapterPosition >= chapterSize()) {
            return null
        }
        if (cache[chapterPosition] != null) {
            val pair = cache[chapterPosition]
            if (pair.second == pageVersion.get()) {
                return pair.first
            }
        }
        // 分配到 IO 线程执行，这里立即返回
        scope.launch(Dispatchers.IO) {
            requestChapterContent(chapterPosition).collect {
                val pages = pageDrawer.loadPage(requestChapter(chapterPosition), it)
                cache.put(chapterPosition, pages to pageVersion.get())
                if (chapterPosition == chapterPos) {
                    adjustCurPageList()
                }
                _curChapterLoadedEvent.emit(chapterPosition)
            }
        }
        return null
    }

    fun reloadCurPage() {
        pageVersion.incrementAndGet()
        // 分配到 IO 线程执行，这里立即返回
        scope.launch(Dispatchers.IO) {
            requestChapterContent(chapterPos).collect {
                val pages = pageDrawer.loadPage(requestChapter(chapterPos), it)
                cache.put(chapterPos, pages to pageVersion.get())
                curPageList = pages
            }
        }
    }

    fun getCurPage(): Page {
        return curPageList[position]
    }

    fun getCurChapter(): ChapterEntity {
        return requestChapter(chapterPos)
    }

    fun getCurPageCount(): Int {
        return curPageList.size
    }

    fun changeToNextPage(): Boolean {
        if (position + 1 < curPageList.size && position != Int.MAX_VALUE && position != Int.MIN_VALUE) {
            lastTurnPageState = NEXT_PAGE
            positionChangeEvent.value += 1
            return true
        }
        if (chapterPos + 1 >= chapterSize()) {
            return false
        }
        lastTurnPageState = NEXT_PAGE
        _chapterPosChangeEvent.value += 1
        // 获取当前章节
        curPageList = getChapterPage(chapterPos) ?: emptyList()
        // 提前加载一章
        getChapterPage(chapterPos + 1)
        if (curPageList.isEmpty()) {
            pageDrawer.status = PageViewDrawer.STATUS_LOADING

            positionChangeEvent.value = Int.MAX_VALUE
            return true
        }

        positionChangeEvent.value = 0
        return true
    }

    fun changeToPrePage(): Boolean {
        if (position - 1 >= 0 && position != Int.MAX_VALUE && position != Int.MIN_VALUE) {
            lastTurnPageState = PRE_PAGE
            positionChangeEvent.value -= 1
            return true
        }
        if (chapterPos - 1 < 0) {
            return false
        }
        lastTurnPageState = PRE_PAGE
        _chapterPosChangeEvent.value -= 1
        curPageList = getChapterPage(chapterPos) ?: emptyList()
        if (curPageList.isEmpty()) {
            pageDrawer.status = PageViewDrawer.STATUS_LOADING
            positionChangeEvent.value = Int.MIN_VALUE
            return true
        }
        positionChangeEvent.value = curPageList.size - 1
        // 提前加载一章
        getChapterPage(chapterPos - 1)

        return true
    }

    fun cancelPage() {
        Log.d("mengchen", "cancel page")
        when (lastTurnPageState) {
            NEXT_PAGE -> changeToPrePage()
            PRE_PAGE -> changeToNextPage()
        }
    }

    fun openChapter(chapterIndex: Int) {
        val chapterPages = getChapterPage(chapterIndex)
        _chapterPosChangeEvent.value = chapterIndex
        positionChangeEvent.value = 0
        // 没有立即获取到数据需要加载
        if (chapterPages == null) {
            pageDrawer.status = PageViewDrawer.STATUS_LOADING
            return
        }
        curPageList = chapterPages
    }
}
