package top.mcwebsite.novel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import top.mcwebsite.novel.data.local.db.entity.BookEntity

@Parcelize
data class BookModel(
    val name: String,
    val author: String,
    var coverUrl: String? = null,
    var introduce: String = "",
    var bookType: String? = null,
    var lastChapter: String? = null,
    val url: String,
    var chapterUrl: String? = null,
    val source: String,
    var update: String? = null,
    var atBookShelf: Boolean = false,
) : Parcelable {

    fun transformToEntity() = BookEntity(
        name = this.name,
        author = this.author,
        coverUrl = this.coverUrl,
        introduce = this.introduce,
        bookType = this.bookType,
        lastChapter = this.lastChapter,
        url = this.url,
        chapterUrl = this.chapterUrl,
        source = this.source,
        update = this.update,
        addToBookShelfTime = System.currentTimeMillis(),
        lastReadTime = System.currentTimeMillis(),
        readDuration = 0L,
    )
}
