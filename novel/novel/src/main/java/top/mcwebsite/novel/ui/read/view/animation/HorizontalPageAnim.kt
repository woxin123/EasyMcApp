package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

abstract class HorizontalPageAnim (
    screenWidth: Int,
    screenHeight: Int,
    marginWidth: Int = 0,
    marginHeight: Int = 0,
    view: View?,
    listener: OnPageChangeListener,
) : PageAnimation(screenWidth, screenHeight, marginWidth, marginHeight, view, listener) {

    protected var curBitmap: Bitmap

    protected var mNextBitmap: Bitmap

    protected var isCancel: Boolean = false

    private var moveX: Int = 0
    private var moveY: Int = 0

    private var isMove: Boolean = false

    private var isNext: Boolean = false

    private var noNext: Boolean = false

    init {
        curBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
        mNextBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
    }

    fun changePage() {
        val bitmap = curBitmap
        curBitmap = mNextBitmap
        mNextBitmap = bitmap
    }

    abstract fun drawStatic(canvas: Canvas)

    abstract fun drawMove(canvas: Canvas)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        setTouchPoint(x.toFloat(), y.toFloat())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveX = 0
                moveY = 0

                isMove = false
                noNext = false
                isRunning = false
                isCancel = false
                setStartPoint(x.toFloat(), y.toFloat())
                abortAnim()
            }
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(view?.context).scaledTouchSlop
                if (!isMove) {
                    isMove = abs(startX - x) > slop || abs(startY - y) > slop
                }

                if (isMove) {

                    if (moveX == 0 && moveY == 0) {

                        if (x - startX > 0) {
                            isNext = false
                            val hasPrev = listener.hasPrev()
                            direction = Direction.PRE
                            if (!hasPrev) {
                                noNext = true
                                return true
                            }
                        } else {
                            isNext = true
                            val hasNext = listener.hasNext()
                            direction = Direction.NEXT
                            if (!hasNext) {
                                noNext = true
                                return true
                            }
                        }
                    } else {
                        isCancel = if (isNext) {
                            x - moveX > 0
                        } else {
                            x - moveX < 0
                        }
                    }
                    moveX = x
                    moveY = y
                    isRunning = true
                    view?.invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isMove) {
                    isNext = x >= screenWidth / 2
                    if (isNext) {
                        val hasNext = listener.hasNext()
                        direction = Direction.NEXT
                        if (hasNext) {
                            return true
                        }
                    } else {
                        val hasPrev = listener.hasPrev()
                        direction = Direction.PRE
                        if (!hasPrev) {
                            return true
                        }
                    }
                }
                if (isCancel) {
                    listener.pageCancel()
                }
                if (!noNext) {
                    startAnim()
                    view?.invalidate()
                }
            }
        }
        return true
    }

    override fun draw(canvas: Canvas) {
        if (isRunning) {
            drawMove(canvas)
        } else {
            if (isCancel) {
                mNextBitmap = curBitmap.copy(Bitmap.Config.RGB_565, true)
            }
            drawStatic(canvas)
        }
    }

    override fun scrollAnim() {
        if (scroller.computeScrollOffset()) {
            val x = scroller.currX
            val y = scroller.currY
            setTouchPoint(x.toFloat(), y.toFloat())
            if (scroller.finalX == x && scroller.finalY == y) {
                isRunning = false
            }
            view?.postInvalidate()
        }
    }

    override fun abortAnim() {
        if (!scroller.isFinished) {
            scroller.abortAnimation()
            isRunning = false
            setTouchPoint(scroller.finalX.toFloat(), scroller.finalY.toFloat())
            view?.postInvalidate()
        }
    }

    override fun getBgBitmap(): Bitmap {
        return mNextBitmap
    }

    override fun getNextBitmap(): Bitmap {
        return mNextBitmap
    }
}