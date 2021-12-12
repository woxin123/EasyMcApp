package top.mcwebsite.common.ui.utils

import java.text.SimpleDateFormat
import java.util.*

// TODO remove
fun Date.convertString(pattern: String): String {
    val simpleDataFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDataFormat.format(this)
}