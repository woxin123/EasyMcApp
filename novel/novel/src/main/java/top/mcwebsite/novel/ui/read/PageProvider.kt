package top.mcwebsite.novel.ui.read

import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.model.Page
import top.mcwebsite.novel.ui.read.view.PageViewDrawer
import java.lang.IllegalStateException

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

    private val positionChangeEvent: MutableStateFlow<Int> =
        MutableStateFlow(bookEntity.lastReadPosition)
    private val chapterPosChangeEvent: MutableStateFlow<Int> =
        MutableStateFlow(bookEntity.lastReadChapterPos)

    var position = 0
    var chapterPos = 0

    private val cache = LruCache<Int, List<Page>>(20)

    private val _curChapterLoadedEvent = MutableSharedFlow<Int>()

    val curChapterLoadedEvent = _curChapterLoadedEvent.asSharedFlow()

    private var curPageList: List<Page> = emptyList()

    init {
        if (bookEntity.lastReadChapterPos == -1) {
            chapterPosChangeEvent.value = 0
            chapterPos = 0
        }
        scope.launch {
            positionChangeEvent.collect {
                position = it
                bookEntity.lastReadPosition = it
            }
        }
        scope.launch {
            chapterPosChangeEvent.collect {
                chapterPos = it
                bookEntity.lastReadChapterPos = it
                bookEntity.lastReadChapterTitle = chapterList[it].title
            }
        }

        getCurrentChapterPage()
        getChapterPage(chapterPos + 1)
        getChapterPage(chapterPos - 1)
    }

    fun adjustCurPageList() {
        curPageList = getChapterPage(chapterPos) ?: throw IllegalStateException("错误的状态")
        if (position == Int.MIN_VALUE) {
            positionChangeEvent.value = curPageList.size - 1
        }
        if (position == Int.MAX_VALUE) {
            positionChangeEvent.value = 0
        }
        pageDrawer.status = PageViewDrawer.STATUS_FINISH
    }

    private fun getCurrentChapterPage() {
        scope.launch {
            requestChapter(chapterPos).collect {
                curPageList = pageDrawer.loadPage(it)
                cache.put(chapterPos, curPageList.toList())
                pageDrawer.status = PageViewDrawer.STATUS_FINISH
                _curChapterLoadedEvent.emit(chapterPos)
            }
        }
    }


    private fun getChapterPage(chapterPosition: Int): List<Page>? {
        if (chapterPosition < 0 || chapterPosition >= chapterList.size) {
            return null
        }
        if (cache[chapterPosition] != null) {
            return cache[chapterPosition]
        }
        scope.launch {
            requestChapter(chapterPosition).collect {
                val pages = pageDrawer.loadPage(it)
                cache.put(chapterPosition, pages)
                if (chapterPosition == chapterPos) {
                    adjustCurPageList()
                }
                _curChapterLoadedEvent.emit(chapterPosition)
            }
        }
        return null
    }

    fun getCurPage(): Page {
        return curPageList[position]
    }

    fun getCurChapter(): Chapter {
        Log.d("mengchen", "chapter pos = $chapterPos")
        return chapterList[chapterPos]
    }

    fun getCurPageCount(): Int {
        return curPageList.size
    }

    fun changeToNextPage(): Boolean {
        if (position + 1 < curPageList.size && position != Int.MAX_VALUE && position != Int.MIN_VALUE) {
            positionChangeEvent.value += 1
            return true
        }
        if (chapterPos + 1 >= chapterList.size) {
            return false
        }
        chapterPosChangeEvent.value += 1
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
            positionChangeEvent.value -= 1
            return true
        }
        if (chapterPos - 1 < 0) {
            return false
        }
        chapterPosChangeEvent.value -= 1
        curPageList = getChapterPage(chapterPos) ?: emptyList()
        // 提前加载一章
        getChapterPage(chapterPos - 1)
        if (curPageList.isEmpty()) {
            Log.d("mengchen", "status_loading")
            pageDrawer.status = PageViewDrawer.STATUS_LOADING
            positionChangeEvent.value = Int.MIN_VALUE
            return true
        }
        positionChangeEvent.value = curPageList.size - 1
        return true
    }

    fun openChapter(chapterIndex: Int) {
        val chapterPages = getChapterPage(chapterIndex)
        chapterPosChangeEvent.value = chapterIndex
        positionChangeEvent.value = 0
        // 没有立即获取到数据需要加载
        if (chapterPages == null) {
            pageDrawer.status = PageViewDrawer.STATUS_LOADING
            return
        }
        curPageList = chapterPages

    }

}
