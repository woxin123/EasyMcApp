package top.mcwebsite.novel.ui.search

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.local.datastore.SearchHistories
import top.mcwebsite.novel.data.local.datastore.SearchHistory
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel : ViewModel(), KoinComponent {

    val searchContent = MutableLiveData<String>()

    private val searchHistoryDataStore by inject<DataStore<SearchHistories>>()

    private val _searchHistoryEvent = MutableLiveData<List<String>>()
    val searchHistoryData: LiveData<List<String>> = _searchHistoryEvent


    private val _backEvent = MutableLiveData<Unit>()
    val backEvent: LiveData<Unit> = _backEvent

    private var searchHistories: MutableList<SearchHistory> = mutableListOf()

    init {
        viewModelScope.launch {
            searchHistories = ArrayList(searchHistoryDataStore.data.first().historiesList)
            _searchHistoryEvent.value = searchHistories
                .sortedByDescending { it.time }
                .map { it.text }
        }
    }

    fun clearAll() {
        searchHistories.clear()
        update()
    }

    private fun update() {
        _searchHistoryEvent.value = searchHistories.sortedByDescending { it.time }.map { it.text }
        viewModelScope.launch {
            searchHistoryDataStore.updateData {
                SearchHistories.newBuilder().addAllHistories(
                    searchHistories
                ).build()
            }
        }
    }

    fun search() {
        searchContent.value.toString().takeIf { it.isNotBlank() }?.let {
            remove(it)
            addHistory(it)
            update()
        }
    }

    fun setSearchText(text: String) {
        searchContent.value = text
    }



    private fun addHistory(text: String) {
        searchHistories.add(
            SearchHistory.newBuilder().setText(text).setTime(System.currentTimeMillis()).build()
        )
    }

    fun remove(text: String) {
        searchHistories.remove(searchHistories.find { it.text == text })
    }

    fun back() {
        _backEvent.value = Unit
    }

}