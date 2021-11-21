package top.mcwebsite.novel.ui.read.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.transition.Slide
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import top.mcwebsite.novel.ui.read.page.PageMode
import top.mcwebsite.novel.ui.read.page.PageViewDrawer
import top.mcwebsite.novel.ui.read.view.animation.*
import kotlin.math.abs

class PageWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = -1
) : View(context, attrs, defStyle) {

    private var viewWidth = 0
    private var viewHeight = 0

    private var isMove = false

    private lateinit var pageAnim: PageAnimation

    lateinit var pageDrawer: PageViewDrawer

    lateinit var touchListener: TouchListener

    private val slop = ViewConfiguration.get(context).scaledTouchSlop

    private var pageMode: PageMode = PageMode.SIMULATION


    private val pageAnimListener = object : PageAnimation.OnPageChangeListener {
        override fun hasPrev(): Boolean {
            return this@PageWidget.hasPrePage()
        }

        override fun hasNext(): Boolean {
            return this@PageWidget.hasNextPage()
        }

        override fun pageCancel() {
            this@PageWidget.pageCancel()
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        centerRect = Rect(viewWidth / 5, viewHeight / 3, viewWidth * 4 / 5, viewHeight * 2 / 3)
        pageDrawer.initDrawer(w.toFloat(), h.toFloat())

        setPageMode(pageMode)
        pageDrawer.drawPage(getNextBitmap(), false)
    }

    fun setPageMode(pageMode: PageMode) {
        this.pageMode = pageMode
        pageAnim = when (pageMode) {
            PageMode.SIMULATION -> SimulationPageAnim(viewWidth, viewHeight, this, pageAnimListener)
            PageMode.COVER -> CoverPageAnim(viewWidth, viewHeight, this, pageAnimListener)
            PageMode.SLIDE -> SlidePageAnim(viewWidth, viewHeight, this, pageAnimListener)
            PageMode.NONE -> NonePageAnimation(viewWidth, viewHeight, this, pageAnimListener)
            else -> {
                NonePageAnimation(viewWidth, viewHeight, this, pageAnimListener)
            }
        }
        drawCurPage(false)
    }

    fun drawNextPage() {
        if (pageAnim is HorizontalPageAnim) {
            (pageAnim as HorizontalPageAnim).changePage()
        }
        pageDrawer.drawPage(getNextBitmap(), false)
    }

    fun drawCurPage(isUpdate: Boolean) {
        pageDrawer.drawPage(getNextBitmap(), isUpdate)
    }

    private fun getNextBitmap(): Bitmap {
        return pageAnim.getNextBitmap()
    }

    fun updateColor() {
        pageDrawer.updateColor()
        drawCurPage(false)
        invalidate()
    }

    fun updateTextSize() {
        pageDrawer.updateTextSize()
        drawCurPage(false)
        invalidate()
    }

    private var startX: Int = 0
    private var startY: Int = 0

    private lateinit var centerRect: Rect

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = x
                startY = y
                isMove = false
                pageAnim.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isMove) {
                    isMove = abs(startX - event.x) > slop || abs(startY - event.y) > slop
                }
                if (isMove) {
                    pageAnim.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isMove) {
                    if (centerRect.contains(x, y)) {
                        touchListener.center()
                        return true
                    }
                }
                pageAnim.onTouchEvent(event)
            }
        }
        return true
    }

    override fun computeScroll() {
        pageAnim.scrollAnim()
        super.computeScroll()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pageAnim.draw(canvas)
    }

    private fun hasPrePage(): Boolean {
        touchListener.prePage()
        return pageDrawer.prePage()
    }

    private fun hasNextPage(): Boolean {
        touchListener.nextPage()
        return pageDrawer.nextPage()
    }

    private fun pageCancel() {
        touchListener.cancel()
        return pageDrawer.cancelPage()
    }

    interface TouchListener {
        fun center()
        fun prePage(): Boolean
        fun nextPage(): Boolean
        fun cancel()
    }

    interface OnPageChangeListener {
        fun hasPrev(): Boolean
        fun hasNext(): Boolean
        fun pageCancel()
    }

}