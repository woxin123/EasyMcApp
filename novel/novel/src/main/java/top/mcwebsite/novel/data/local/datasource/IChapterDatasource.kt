package top.mcwebsite.novel.data.local.datasource

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.model.Chapter

interface IChapterDatasource {

    suspend fun getChaptersByBid(bid: Int): List<ChapterEntity>

    suspend fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>>

    suspend fun update(chapter: ChapterEntity)

    suspend fun insert(vararg chapter: Chapter)
}