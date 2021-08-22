package top.mcwebsite.novel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import top.mcwebsite.novel.data.local.db.dao.BookDao
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.model.BookModel

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class NovelDataBase : RoomDatabase() {

    abstract fun bookDao(): BookDao

}