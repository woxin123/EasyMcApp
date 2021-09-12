package top.mcwebsite.novel.data.local.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter WHERE bid = :bid")
    suspend fun getChaptersByBId(bid: Int): List<ChapterEntity>

    @Query("SELECT * FROM chapter WHERE bid = :bid")
    fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>>

    @Insert
    suspend fun insert(vararg chapter: ChapterEntity)


    @Update
    suspend fun update(chapter: ChapterEntity)

    @Delete
    suspend fun deleteByBid(chapters: List<ChapterEntity>)

}