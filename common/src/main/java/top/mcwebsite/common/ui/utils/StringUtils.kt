package top.mcwebsite.common.ui.utils


// TODO remove
fun String.halfToFull(): String {
    val chars = this.toCharArray()
    for ((i, c) in chars.withIndex()) {
        if (c.code == 32) { // 半角空格
            chars[i] = Char(12288)
            continue
        }
        if (c.code in 33..126) {
            chars[i] = Char(chars[i].code + 65248)
        }
    }
    return String(chars)
}