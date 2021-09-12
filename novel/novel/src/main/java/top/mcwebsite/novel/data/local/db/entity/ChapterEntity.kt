package top.mcwebsite.novel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import top.mcwebsite.novel.model.Chapter

@Entity(tableName = "chapter")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val cid: Int = 0,
    val bid: Int = 0,
    val index: Int = 0,
    val title: String,
    val url: String,
) {
    fun transformToModel(): Chapter {
        return Chapter(
            index, title, url
        )
    }
}