package top.mcwebsite.novel.data.local.datasource

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

interface IChapterDatasource {

    suspend fun getChaptersByBid(bid: Int): List<ChapterEntity>

    suspend fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>>

    suspend fun updateChapterEntities(vararg chapterEntity: ChapterEntity)

    suspend fun insert(vararg chapterEntity: ChapterEntity)
}
