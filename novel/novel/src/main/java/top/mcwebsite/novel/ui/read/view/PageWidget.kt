package top.mcwebsite.novel.ui.read.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import top.mcwebsite.novel.ui.read.view.animation.*
import kotlin.math.abs

class PageWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = -1
) : View(context, attrs, defStyle) {

    private var viewWidth = 0
    private var viewHeight = 0

    private var downX = 0F
    private var downY = 0F

    private var moveX = 0F
    private var moveY = 0F

    private var isRunning = false

    private var cancelPage = false

    private var isLastOrFirstPage = false

    private var isMove = false

    private var isNext = false

    private var bgColor = 0xFFCE29C

    private lateinit var animationProvider: AnimationProvider

    private lateinit var curPageBitmap: Bitmap
    private lateinit var nextPageBitmap: Bitmap

    lateinit var pageDrawer: PageViewDrawer

    lateinit var touchListener: TouchListener

    private val scroller: Scroller = Scroller(context, LinearInterpolator())

    private val slop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metric)
        viewWidth = metric.widthPixels
        viewHeight = metric.heightPixels
        curPageBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
        nextPageBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)

        animationProvider = NoneAnimation(curPageBitmap, nextPageBitmap, viewWidth, viewHeight)
    }


    fun getCurPage(): Bitmap {
        return curPageBitmap
    }

    fun drawNextPage() {
        pageDrawer.drawPage(nextPageBitmap, false)
    }

    fun drawCurPage(isUpdate: Boolean) {
        pageDrawer.drawPage(nextPageBitmap, isUpdate)
    }

    fun setBgColor(color: Int) {
        bgColor = color
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
                moveX = 0F
                moveY = 0F
                isLastOrFirstPage = false
                isNext = false
                isMove = false
                isRunning = false
                animationProvider.setStartPoint(downX, downY)
                abortAnimation()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isMove) {
                    isMove = abs(downX - x) > slop || abs(downY - y) > slop
                }
                if (isMove) {
                    if (moveX == 0F && moveY == 0F) {
                        isNext = x - downX <= 0
                        cancelPage = false
                        if (isNext) {
                            val hasNextPage = pageChangeListener.hasNext()
                            animationProvider.direction = Direction.NEXT
                            if (!hasNextPage) {
                                isLastOrFirstPage = true
                                return true
                            }
                        } else {
                            val isPrePage = pageChangeListener.hasPrev()
                            animationProvider.direction = Direction.PRE
                            if (!isPrePage) {
                                isLastOrFirstPage = true
                                return true
                            }
                        }
                    }
                    moveX = x
                    moveY = y
                }


            }
            MotionEvent.ACTION_UP -> {
                if (!isMove) {
                    cancelPage = false
                    if (downX > viewWidth / 5 && downX < viewWidth * 4 / 5 && downY > viewHeight / 3 && downY < viewHeight * 2 / 3) {
                        touchListener.center()
                    }
                    return true
                }

//                isNext = x >= viewWidth / 2
//
//                if (isNext) {
//                    val hasNext = pageChangeListener.hasNext() ?: false
//                    animationProvider.direction = Direction.NEXT
//                    if (!hasNext) {
//                        return true
//                    }
//                } else {
//                    val hasPre = pageChangeListener?.hasPrev() ?: false
//                    animationProvider.direction = Direction.PRE
//                    if (!hasPre) {
//                        return true
//                    }
//                }
//                if (cancelPage) {
//                    touchListener.cancel()
//                }
//                if (!isLastOrFirstPage) {
//                    isRunning = true
//                    animationProvider.startAnimation(scroller)
//                    postInvalidate()
//                }
                if (!isLastOrFirstPage) {
                    animationProvider.startAnimation(scroller)
                    postInvalidate()
                }
            }
        }
        return true
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val x = scroller.currX
            val y = scroller.currY
            animationProvider.setTouchPoint(x.toFloat(), y.toFloat())
            if (scroller.finalX == x && scroller.finalY == y) {
                isRunning = false
            }
            postInvalidate()
        }
        super.computeScroll()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(bgColor)
        canvas?.let {
            if (isRunning) {
                animationProvider.drawMove(canvas)
            } else {
                animationProvider.drawStatic(canvas)
            }
        }
    }

    fun abortAnimation() {
        if (!scroller.isFinished) {
            scroller.abortAnimation()
            animationProvider.setTouchPoint(scroller.finalX.toFloat(), scroller.finalY.toFloat())
            postInvalidate()
        }
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



    var pageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
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