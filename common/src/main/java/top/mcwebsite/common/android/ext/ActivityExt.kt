package top.mcwebsite.common.android.ext

import android.app.Activity
import android.content.Intent


inline fun <reified A> Activity.startActivity(intent: Intent) where A : Activity {
    this.startActivity(intent.apply { setClass(this@startActivity, A::class.java) })
}