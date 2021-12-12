package top.mcwebsite.novel.ui.rank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.RankCategory

class RankViewModel(private val bookRepository: BookRepositoryManager) : ViewModel() {

    private val _rankCategoriesEvent: MutableSharedFlow<List<RankCategory>> = MutableSharedFlow()

    val rankCategoriesEvent = _rankCategoriesEvent.asSharedFlow()

    private val rankData = mutableMapOf<String, List<RankCategory>>()

    fun requireRankCategoriesBySource(source: String) {
        viewModelScope.launch {
            bookRepository.getRankList(source).collect {
                _rankCategoriesEvent.emit(it)
                rankData[source] = it
            }
        }
    }


}