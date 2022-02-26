package top.mcwebsite.novel.data.local.datasource.impl

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.db.dao.ChapterDao
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity

class ChapterDatasourceImpl(
    private val chapterDao: ChapterDao
) : IChapterDatasource {
    override suspend fun getChaptersByBid(bid: Int): List<ChapterEntity> {
        return chapterDao.getChaptersByBId(bid)
    }

    override suspend fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>> {
        return chapterDao.getChaptersByBidFlow(bid)
    }

    override suspend fun updateChapterEntities(vararg chapterEntity: ChapterEntity) {
        chapterDao.update(*chapterEntity)
    }

    override suspend fun insert(vararg chapterEntity: ChapterEntity) {
        chapterDao.insertAll(*chapterEntity)
    }
}
