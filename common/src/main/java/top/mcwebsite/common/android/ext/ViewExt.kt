package top.mcwebsite.common.android.ext

import android.view.View


fun View.setVisible(isShow: Boolean, useInvisible: Boolean = false) {
    val viewVisible = if (isShow) {
        View.VISIBLE
    } else {
        if (useInvisible) {
            View.INVISIBLE
        } else {
            View.GONE
        }
    }
    visibility = viewVisible
}