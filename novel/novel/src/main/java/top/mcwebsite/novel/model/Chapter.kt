package top.mcwebsite.novel.model

data class Chapter(
    val index: Int,
    val title: String,
    val url: String,
    var content: String = "",

    val bookId: Int = 0,

)