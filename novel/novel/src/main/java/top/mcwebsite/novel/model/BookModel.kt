package top.mcwebsite.novel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
) : Parcelable