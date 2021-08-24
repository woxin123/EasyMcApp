package top.mcwebsite.novel.ui.read.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

class PagerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = -1
) : View(context, attrs, defStyle) {

    private var viewWidth = 0
    private var viewHeight = 0

    private var startX = 0
    private var startY = 0

    private var bgColor = 0xFFCE29C

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h

    }

}