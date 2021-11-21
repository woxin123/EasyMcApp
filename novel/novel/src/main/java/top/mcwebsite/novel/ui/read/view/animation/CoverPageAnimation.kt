package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Scroller
import kotlin.math.abs

class CoverPageAnim(
    screenWidth: Int,
    screenHeight: Int,
    view: View?,
    listener: OnPageChangeListener
) : HorizontalPageAnim(screenWidth, screenHeight, 0, 0, view, listener) {

    private val mSrcRect: Rect = Rect(0, 0, viewWidth, viewHeight)
    private val mDestRect: Rect = Rect(0, 0, viewWidth, viewHeight)
    private val mBackShadowDrawableLR: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0x66000000, 0x00000000)
    )

    init {
        mBackShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            mNextBitmap = curBitmap.copy(Bitmap.Config.RGB_565, true)
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        when (direction) {
            Direction.NEXT -> {
                var dis = (viewWidth - startX + touchX).toInt()
                if (dis > viewWidth) {
                    dis = viewWidth
                }
                //计算bitmap截取的区域
                mSrcRect.left = viewWidth - dis
                //计算bitmap在canvas显示的区域
                mDestRect.right = dis
                canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
                canvas.drawBitmap(curBitmap, mSrcRect, mDestRect, null)
                addShadow(dis, canvas)
            }
            else -> {
                mSrcRect.left = (viewWidth - touchX).toInt()
                mDestRect.right = touchX.toInt()
                canvas.drawBitmap(curBitmap, 0F, 0F, null)
                canvas.drawBitmap(mNextBitmap, mSrcRect, mDestRect, null)
                addShadow(touchX.toInt(), canvas)
            }
        }
    }

    //添加阴影
    private fun addShadow(left: Int, canvas: Canvas?) {
        mBackShadowDrawableLR.setBounds(left, 0, left + 30, viewHeight)
        mBackShadowDrawableLR.draw(canvas!!)
    }

    override fun startAnim() {
        super.startAnim()
        var dx = 0
        when (direction) {
            Direction.NEXT -> if (isCancel) {
                var dis = (viewWidth - startX + touchX).toInt()
                if (dis > viewWidth) {
                    dis = viewWidth
                }
                dx = viewWidth - dis
            } else {
                dx = (-(touchX + (viewWidth - startX))).toInt()
            }
            else -> dx = if (isCancel) {
                -touchX.toInt()
            } else {
                (viewWidth - touchX).toInt()
            }
        }

        //滑动速度保持一致
        val duration: Int = 400 * abs(dx) / viewWidth
        scroller.startScroll(touchX.toInt(), 0, dx, 0, duration)
    }


}
