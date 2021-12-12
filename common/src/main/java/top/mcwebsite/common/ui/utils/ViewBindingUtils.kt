package top.mcwebsite.common.ui.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.viewbinding.ViewBinding

fun ViewBinding.context(): Context {
    return this.root.context
}

fun ViewBinding.getColor(@ColorRes id: Int): Int {
    return this.root.context.getColor(id)
}