package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.widget.Scroller

enum class Direction(isHorizontal: Boolean) {
    NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false)
}

enum class Animation {
    NONE, CURL, SLIDE, SHIFT
}

abstract class AnimationProvider(
    protected var curBitmap: Bitmap,
    protected var nextBitmap: Bitmap,
    protected var viewWidth: Int,
    protected var viewHeight: Int
) {

    protected var startX: Float = 0F
    protected var startY: Float = 0F

    protected var endX: Float = 0F
    protected var endY: Float = 0F

    protected var touch = PointF()
    var direction: Direction = Direction.NONE
    var isCancel: Boolean = false

    // 绘制滑动页面
    abstract fun drawMove(canvas: Canvas)

    // 绘制不滑动的页面
    abstract fun drawStatic(canvas: Canvas)

    abstract fun startAnimation(scroller: Scroller)

    fun setStartPoint(x: Float, y: Float) {
        startX = x
        startY = y
    }

    fun setTouchPoint(x: Float, y: Float) {
        touch.x = x
        touch.y = y
    }

}