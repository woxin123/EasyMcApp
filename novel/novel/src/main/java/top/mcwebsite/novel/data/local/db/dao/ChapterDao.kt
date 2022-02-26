package top.mcwebsite.novel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter")
    suspend fun getAll(): List<ChapterEntity>

    @Query("SELECT * FROM chapter WHERE bid = :bid")
    suspend fun getChaptersByBId(bid: Int): List<ChapterEntity>

    @Query("SELECT * FROM chapter WHERE bid = :bid")
    fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>>

    @Insert
    suspend fun insertAll(vararg chapter: ChapterEntity)

    @Update
    suspend fun update(vararg chapter: ChapterEntity)

    @Delete
    suspend fun deleteByBid(chapters: List<ChapterEntity>)
}
