package top.mcwebsite.novel.model

data class Chapter(
    val index: Int,
    val title: String,
    val url: String,
    val content: String = "",
)