package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import kotlin.math.abs

class SlidePageAnim(
    screenWidth: Int,
    screenHeight: Int,
    view: View?,
    listener: OnPageChangeListener
) : HorizontalPageAnim(
    screenWidth, screenHeight, 0, 0, view, listener
) {

    private val srcRect = Rect(0, 0, viewWidth, viewHeight)
    private val destRect = Rect(0, 0, viewWidth, viewHeight)
    private val nextSrcRect = Rect(0, 0, viewWidth, viewHeight)
    private val nextDestRect = Rect(0, 0, viewWidth, viewHeight)

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }


    override fun drawMove(canvas: Canvas) {
        var dis = 0
        when (direction) {
            Direction.NEXT -> {
                //左半边的剩余区域
                dis = ((screenWidth - startX + touchX).toInt())
                if (dis > screenWidth) {
                    dis = screenWidth
                }
                //计算bitmap截取的区域
                srcRect.left = screenWidth - dis
                //计算bitmap在canvas显示的区域
                destRect.right = dis
                //计算下一页截取的区域
                nextSrcRect.right = screenWidth - dis
                //计算下一页在canvas显示的区域
                nextDestRect.left = dis
                canvas.drawBitmap(mNextBitmap, nextSrcRect, nextDestRect, null)
                canvas.drawBitmap(curBitmap, srcRect, destRect, null)
            }
            else -> {
                dis = ((touchX - startX).toInt())
                if (dis < 0) {
                    dis = 0
                    startX = touchX
                }
                srcRect.left = screenWidth - dis
                destRect.right = dis

                //计算下一页截取的区域
                nextSrcRect.right = screenWidth - dis
                //计算下一页在canvas显示的区域
                nextDestRect.left = dis
                canvas.drawBitmap(curBitmap, nextSrcRect, nextDestRect, null)
                canvas.drawBitmap(mNextBitmap, srcRect, destRect, null)
            }
        }
    }

    override fun startAnim() {
        super.startAnim()
        var dx = 0
        when (direction) {
            Direction.NEXT -> if (isCancel) {
                var dis = (screenWidth - startX + touchX).toInt()
                if (dis > screenWidth) {
                    dis = screenWidth
                }
                dx = screenWidth - dis
            } else {
                dx = (-(touchX + (screenWidth - startX))).toInt()
            }
            else -> dx = if (isCancel) {
                -abs(touchX - startX)
            } else {
                (screenWidth - (touchX - startX))
            }.toInt()
        }
        //滑动速度保持一致
        val duration: Int = 400 * Math.abs(dx) / screenWidth
        scroller.startScroll(touchX.toInt(), 0, dx, 0, duration)
    }

}