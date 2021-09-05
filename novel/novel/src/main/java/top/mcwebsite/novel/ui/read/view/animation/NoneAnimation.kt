package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Scroller

class NoneAnimation(
    curBitmap: Bitmap,
    nextBitmap: Bitmap,
    width: Int,
    height: Int
) : AnimationProvider(curBitmap, nextBitmap, width, height) {
    override fun drawMove(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(nextBitmap, 0F, 0F, null)
        }
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(nextBitmap, 0F, 0F, null)
        }
    }

    override fun startAnimation(scroller: Scroller) {
    }
}