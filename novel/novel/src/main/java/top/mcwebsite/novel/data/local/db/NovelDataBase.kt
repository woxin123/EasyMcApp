package top.mcwebsite.novel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import top.mcwebsite.novel.data.local.db.dao.BookDao
import top.mcwebsite.novel.data.local.db.dao.ChapterDao
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

@Database(entities = [BookEntity::class, ChapterEntity::class], version = 2, exportSchema = false)
abstract class NovelDataBase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun chapterDao(): ChapterDao
}
