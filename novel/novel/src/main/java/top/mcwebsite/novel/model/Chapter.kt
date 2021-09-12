package top.mcwebsite.novel.model

import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

data class Chapter(
    val index: Int,
    val title: String,
    val url: String,

    val bookId: Int = 0,

) {
    fun transformToEntity(): ChapterEntity {
        return ChapterEntity(
            bid = bookId,
            index = this.index,
            title = this.title,
            url = this.url
        )
    }
}