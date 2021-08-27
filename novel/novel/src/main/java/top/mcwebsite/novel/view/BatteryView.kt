package top.mcwebsite.novel.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BatteryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint by lazy { Paint() }

    private val rect by lazy { RectF() }


    var powerProgress = 50
        set(value) {
            if (value in 0..100) {
                field = value
            }
        }

    var borderColor: Int = Color.BLACK

    var fillColor: Int = Color.GREEN

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = borderColor
        val radius = 0F
        val outlineStrokeWidth = 3
        paint.strokeWidth = outlineStrokeWidth.toFloat()
        val height = (height - paddingTop - paddingBottom) / 3
        val width = width - paddingLeft - paddingRight - 2
        val startX = paddingLeft + outlineStrokeWidth
        val startY = paddingTop + (getHeight() - height) / 2
        val outlineBorderWidth = (width / 10) * 9 - 2 * outlineStrokeWidth
        rect.left = (startX).toFloat()
        rect.top = (startY).toFloat()
        rect.right = (startX + outlineBorderWidth).toFloat()
        rect.bottom = (startY + height).toFloat()

        canvas?.drawRoundRect(rect, radius, radius, paint)
        if (powerProgress > 0) {
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 0F
            paint.color = fillColor
            val powerProgressWidth =
                (outlineBorderWidth / 100F) * powerProgress
            rect.left = (startX + outlineStrokeWidth).toFloat()
            rect.top = (startY + outlineStrokeWidth).toFloat()
            rect.right = (startX + powerProgressWidth).toFloat()
            rect.bottom = (startY + height - outlineStrokeWidth).toFloat()
            canvas?.drawRoundRect(
                rect,  radius, radius, paint
            )
        }

        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = borderColor
        val batteryHeadHeight = height / 2
        val batteryHeadWidth = width / 10
        rect.left = (startX +  outlineBorderWidth).toFloat()
        rect.top = (startY + (height - batteryHeadHeight) / 2).toFloat()
        rect.right = (startX + outlineBorderWidth + batteryHeadWidth).toFloat()
        rect.bottom = rect.top + batteryHeadHeight
        canvas?.drawRect(
            rect, paint
        )
    }

}