package top.mcwebsite.novel

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository

class Yb3BookRepositoryTest {

    private val yb3BookRepository: Yb3BookRepository = Yb3BookRepository()

    @Test
    fun getRankList() = runBlocking {
        yb3BookRepository.getRankList().collect {
            println(it)
        }
    }

}