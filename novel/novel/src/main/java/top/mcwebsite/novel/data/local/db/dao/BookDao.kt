package top.mcwebsite.novel.data.local.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM book order by last_read_time desc")
    fun getAll(): Flow<List<BookEntity>>

    @Query("SELECT count(*) FROM book WHERE url = :url")
    suspend fun isExistsByUrl(url: String): Boolean

    @Insert
    suspend fun insert(bookEntity: BookEntity)

    @Update
    suspend fun update(bookEntity: BookEntity)

    @Delete
    suspend fun delete(bookEntity: BookEntity)
}