package top.mcwebsite.common.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import top.mcwebsite.common.R

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var color: Int = Color.RED
    var fill: Boolean = true
    var strokeWidth: Float = 0F
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
        color = a.getColor(R.styleable.CircleView_circle_color, Color.RED)
        fill = a.getBoolean(R.styleable.CircleView_fill, true)
        strokeWidth = a.getDimension(R.styleable.CircleView_strokeWidth, 0F)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width - paddingStart - paddingEnd
        val height = height - paddingTop - paddingBottom
        val radius = width.coerceAtMost(height) / 2
        paint.color = color
        if (fill) {
            paint.style = Paint.Style.FILL
        } else {
            paint.style = Paint.Style.STROKE
        }
        paint.strokeWidth = strokeWidth
        canvas.drawCircle((paddingLeft + width / 2).toFloat(), (paddingTop + height / 2).toFloat(), radius.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 200)
        }
    }

}