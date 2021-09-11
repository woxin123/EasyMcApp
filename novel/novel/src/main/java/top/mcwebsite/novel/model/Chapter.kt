package top.mcwebsite.novel.model

data class Chapter(
    val index: Int,
    val title: String,
    val url: String,
    // TODO 这个属性得去掉，占内存
    var content: String = "",

    val bookId: Int = 0,

)