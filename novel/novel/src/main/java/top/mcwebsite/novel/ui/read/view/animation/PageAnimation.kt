package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.annotation.FloatRange

abstract class PageAnimation(
    protected val screenWidth: Int,
    protected val screenHeight: Int,
    marginWidth: Int = 0,
    marginHeight: Int = 0,
    protected var view: View?,
    protected val listener: OnPageChangeListener,
) {

    protected val viewWidth = screenWidth - marginWidth * 2
    protected val viewHeight = screenHeight - marginHeight * 2

    protected val scroller: Scroller = Scroller(view?.context, LinearInterpolator())

    // 起点
    protected var startX: Float = 0F
    protected var startY: Float = 0F

    // 触碰点
    protected var touchX: Float = 0F
    protected var touchY: Float = 0F

    // 上一个触碰点
    private var lastX: Float = 0F
    private var lastY: Float = 0F

    protected var isRunning: Boolean = false

    open var direction: Direction = Direction.NONE

    open fun setStartPoint(x: Float, y: Float) {
        startX = x
        startY = y
        lastX = startX
        lastY = startY
    }

    open fun setTouchPoint(x: Float, y: Float) {
        lastX = touchX
        lastY = touchY
        touchX = x
        touchY = y
    }

    open fun startAnim() {
        if (isRunning) {
            return
        }
        isRunning = true
    }

    fun clear() {
        view = null
    }

    /**
     * 点击事件的处理
     */
    abstract fun onTouchEvent(event: MotionEvent): Boolean

    /**
     * 绘制图形
     */
    abstract fun draw(canvas: Canvas)

    /**
     * 滚动动画
     */
    abstract fun scrollAnim()

    /**
     * 取消动画
     */
    abstract fun abortAnim()

    /**
     * 获取背景板
     */
    abstract fun getBgBitmap(): Bitmap

    /**
     * 获取内容显示版面
     */
    abstract fun getNextBitmap(): Bitmap

    enum class Direction(isHorizontal: Boolean) {
        NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false)

    }

    interface OnPageChangeListener {
        fun hasPrev(): Boolean
        fun hasNext(): Boolean
        fun pageCancel()
    }

}