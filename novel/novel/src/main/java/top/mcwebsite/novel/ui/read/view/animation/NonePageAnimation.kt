package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.Scroller

class NonePageAnimation(
    width: Int,
    height: Int,
    view: View?,
    listener: OnPageChangeListener
) : HorizontalPageAnim(width, height, 0, 0, view, listener) {
    override fun drawMove(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }


}