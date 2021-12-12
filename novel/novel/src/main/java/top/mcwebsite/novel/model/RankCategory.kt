package top.mcwebsite.novel.model

data class RankCategory(
    val source: String,
    val name: String,
    val bookModels: List<BookModel>
)
