package top.mcwebsite.novel.ui.read

import android.util.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.model.Page
import top.mcwebsite.novel.ui.read.view.PageViewDrawer

/**
 * 用来提供 page
 */
class PageProvider(
    private val bookEntity: BookEntity,
    private val chapterList: List<Chapter>,
    private val requestChapter: suspend (Int) -> Flow<Chapter>,
    private val scope: CoroutineScope,
    private val pageDrawer: PageViewDrawer
) {

    private var position: Int = 0
    private var chapterPos: Int = 0

    private var prePageList: List<Page> = emptyList()

    private var curPageList: List<Page> = emptyList()

    private var nextPageList: List<Page> = emptyList()

    // TODO 搞一个内存层面的缓存
    private val cache = LruCache<Int, List<Page>>(20)

    private val _curChapterLoadedEvent = MutableSharedFlow<Unit>()
    private val _nextChapterLoadedEvent = MutableSharedFlow<Unit>()
    private val _preChapterLoadedEvent = MutableSharedFlow<Unit>()

    val curChapterLoadedEvent = _curChapterLoadedEvent.asSharedFlow()
    val nextChapterLoadedEvent = _nextChapterLoadedEvent.asSharedFlow()
    val preChapterLoadedEvent = _preChapterLoadedEvent.asSharedFlow()

    init {
        getCurrentChapterPage()
        getNextChapterPage()
        getPreChapterPage()
    }

    private fun getCurrentChapterPage() {
        scope.launch {
            requestChapter(chapterPos).collect {
                curPageList = pageDrawer.loadPage(it)
                pageDrawer.status = PageViewDrawer.STATUS_FINISH
                _curChapterLoadedEvent.emit(Unit)
            }
        }
    }

    private fun getNextChapterPage(): Boolean {
        if (chapterPos + 1 >= chapterList.size) {
            return false
        }
        scope.launch {
            requestChapter(chapterPos + 1).collect {
                nextPageList = pageDrawer.loadPage(it)
            }
        }
        return true
    }

    private fun getPreChapterPage(): Boolean {
        if (chapterPos - 1 < 0) {
            return false
        }
        scope.launch {
            requestChapter(chapterPos - 1).collect {
                nextPageList = pageDrawer.loadPage(it)
            }
        }
        return true
    }

    fun getCurPage(): Page {
        return curPageList[position]
    }

    fun getCurChapter(): Chapter {
        return chapterList[chapterPos]
    }

    fun getCurPageCount(): Int {
        return curPageList.size
    }

    fun changeToNextPage(): Boolean {
        if (position >= curPageList.size - 1) {
            if (nextPageList.isEmpty()) {
                if (!getNextChapterPage()) {
                    return false
                }
            }
            prePageList = curPageList
            curPageList = nextPageList
            chapterPos++
            val hasNexChapter = getNextChapterPage()
            if (!hasNexChapter) {
                return false
            }
            position = 0
            return true
        } else {
            position += 1
            return true
        }
    }

    fun changeToPrePage(): Boolean {
        if (position <= 0) {
            if (prePageList.isEmpty()) {
                return if (!getPreChapterPage()) {
                    false
                } else {
                    pageDrawer.status = PageViewDrawer.STATUS_LOADING
                    scope.launch {
                        _preChapterLoadedEvent.emit(Unit)
                    }
                    true
                }
            }
            nextPageList = curPageList
            curPageList = prePageList
            prePageList = emptyList()
            getPreChapterPage()
            position = curPageList.size -1
            chapterPos--
            return true
        } else {
            position -= 1
            return true
        }
    }

}