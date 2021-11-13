package top.mcwebsite.novel.data.local.datasource.impl

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.db.dao.ChapterDao
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.model.Chapter

class ChapterDatasourceImpl(
    private val chapterDao: ChapterDao
) : IChapterDatasource {
    override suspend fun getChaptersByBid(bid: Int): List<ChapterEntity> {
        return chapterDao.getAll()
    }

    override suspend fun getChaptersByBidFlow(bid: Int): Flow<List<ChapterEntity>> {
        return chapterDao.getChaptersByBidFlow(bid)
    }

    override suspend fun update(chapter: ChapterEntity) {
        chapterDao.update(chapter)
    }

    override suspend fun insert(vararg chapterEntity: ChapterEntity) {
        chapterDao.insertAll(*chapterEntity)
    }
}