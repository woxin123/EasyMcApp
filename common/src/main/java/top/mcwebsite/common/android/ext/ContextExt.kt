package top.mcwebsite.common.android.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showShortToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showShortToast(@StringRes msgId: Int) {
    showShortToast(this.getString(msgId))
}

fun Context.showLongToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.showLongToast(@StringRes msgId: Int) {
    showLongToast(this.getString(msgId))
}