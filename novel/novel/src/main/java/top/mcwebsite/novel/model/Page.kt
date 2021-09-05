package top.mcwebsite.novel.model

data class Page(
    var position: Int = -1,
    var titleLines: Int = 0,
    var title: String = "",
    var lines: List<String> = emptyList(),
) {
    fun getLineToString(): String {
        val sb = StringBuilder()
        for (line in lines) {
            sb.append(line)
        }
        return sb.toString()
    }
}