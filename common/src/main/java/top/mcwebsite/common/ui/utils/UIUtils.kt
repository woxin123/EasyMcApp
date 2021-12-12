package top.mcwebsite.common.ui.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

// TODO remove
fun dip2px(context: Context, dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5F).toInt()
}



val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )