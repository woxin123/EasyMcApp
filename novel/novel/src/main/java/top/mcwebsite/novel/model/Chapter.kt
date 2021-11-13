package top.mcwebsite.novel.model

import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

data class Chapter(
    val index: Int,
    val title: String,
    val url: String,

) {
    fun transformToEntity(bid: Int): ChapterEntity {
        return ChapterEntity(
            bid = bid,
            index = this.index,
            title = this.title,
            url = this.url
        )
    }
}