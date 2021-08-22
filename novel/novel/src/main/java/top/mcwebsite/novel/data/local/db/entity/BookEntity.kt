package top.mcwebsite.novel.data.local.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bid: Int = 0,
    val name: String,
    val author: String,
    var coverUrl: String? = null,
    var introduce: String = "",
    @ColumnInfo(name = "book_type")
    var bookType: String? = null,
    @ColumnInfo(name = "last_chapter")
    var lastChapter: String? = null,
    val url: String,
    @ColumnInfo(name = "chapter_url")
    var chapterUrl: String? = null,
    val source: String,
    var update: String? = null,
    @ColumnInfo(name = "add_to_bookshelf_time")
    val addToBookShelfTime: Long,
    @ColumnInfo(name = "last_read_time")
    var lastReadTime: Long,
    @ColumnInfo(name = "read_duration")
    var readDuration: Long,
    @ColumnInfo(name = "last_read_index")
    var lastReadIndex: Int = -1,
    @ColumnInfo(name = "last_read_chapter_title")
    var lastReadChapterTitle: String? = null,
) : Parcelable