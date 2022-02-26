package top.mcwebsite.novel.ui.search

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.serizlizer.SearchHistories
import top.mcwebsite.novel.data.local.serizlizer.SearchHistory
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.data.remote.repository.exception.BookSourceException
import top.mcwebsite.novel.model.BookModel

class SearchViewModel(private val bookDataSource: IBookDatasource) : ViewModel(), KoinComponent {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    val searchContent = MutableLiveData<String>()

    private val searchHistoryDataStore by inject<DataStore<SearchHistories>>()

    private val _searchHistoryEvent = MutableLiveData<List<String>>()
    val searchHistoryData: LiveData<List<String>> = _searchHistoryEvent

    private val _backEvent = MutableLiveData<Unit>()
    val backEvent: LiveData<Unit> = _backEvent

    private val _searchEvent = MutableSharedFlow<String>()
    val searchEvent: Flow<String> = _searchEvent

    private val _clickSearchResultEvent = MutableSharedFlow<BookModel>()
    val clickSearchResultEvent: Flow<BookModel> = _clickSearchResultEvent

    // TODO 考虑分页
    private val _searchResult: MutableLiveData<List<BookModel>> = MutableLiveData()

    val searchResult: LiveData<List<BookModel>> = _searchResult

    private var searchHistories: MutableList<SearchHistory> = mutableListOf()

    private val bookRepositoryManager by inject<BookRepositoryManager>()

    private val _searchItemUpdateEvent = MutableSharedFlow<Pair<BookModel, Int>>()
    val searchItemUpdateEvent = _searchItemUpdateEvent.asSharedFlow()

    private val _searchFailedEvent = MutableSharedFlow<BookSourceException>()
    val searchFailedEvent = _searchFailedEvent.asSharedFlow()

    private var bookshelf = listOf<BookEntity>()

    init {
        viewModelScope.launch {
            searchHistories = ArrayList(searchHistoryDataStore.data.first().historiesList)
            _searchHistoryEvent.value = searchHistories
                .sortedByDescending { it.time }
                .map { it.text }
        }
        viewModelScope.launch {
            bookDataSource.getBookAll().collect {
                bookshelf = it
            }
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
        viewModelScope.launch {
            searchContent.value.toString().takeIf { it.isNotBlank() }?.let {
                _searchEvent.emit(it)
                realSearch(it)
                addOrReplaceHistory(it)
            }
        }
    }

    private fun realSearch(key: String) {
        viewModelScope.launch {
            bookRepositoryManager.searchBook(key, 0, 10)
                .catch {
                    _searchFailedEvent.emit(it as BookSourceException)
                    Log.e(TAG, "search error, exception: ${it.message}")
                    it.printStackTrace()
                }
                .collect {
                    updateSearchResult(it)
                    _searchResult.value = it
                }
        }
    }

    private fun updateSearchResult(books: List<BookModel>) {
        books.forEach { book ->
            book.atBookShelf = bookshelf.find { it.url == book.url }  != null
        }
    }

    private fun addOrReplaceHistory(text: String) {
        remove(text)
        addHistory(text)
        update()
    }

    fun clickSearchItem(book: BookModel) {
        viewModelScope.launch {
            _clickSearchResultEvent.emit(book)
        }
    }

    fun cancelSearch() {
        // no op
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

    fun addToBookShelf(bookModel: BookModel, position: Int) {
        viewModelScope.launch {
            bookRepositoryManager.getBookInfo(bookModel).collect {
                if (!bookDataSource.isExistByUrl(bookModel.url)) {
                    bookDataSource.insert(bookModel)
                    bookModel.atBookShelf = true
                    _searchItemUpdateEvent.emit(bookModel to position)
                }
            }
        }
    }

    fun removeSearchBookRepository(source: String) {
        bookRepositoryManager.removeSearchBookRepositoryBySource(source)
    }
}
