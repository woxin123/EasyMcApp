package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.Canvas
import android.view.View

class NonePageAnim(
    screenWidth: Int,
    screenHeight: Int,
    marginWidth: Int = 0,
    marginHeight: Int = 0,
    view: View?,
    listener: OnPageChangeListener,
) : HorizontalPageAnim(screenWidth, screenHeight, marginWidth, marginHeight, view, listener) {
    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }

    override fun startAnim() {

    }

}