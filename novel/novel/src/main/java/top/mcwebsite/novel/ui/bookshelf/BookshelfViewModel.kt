package top.mcwebsite.novel.ui.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity

class BookshelfViewModel(private val bookDataSource: IBookDatasource): ViewModel() {

    private val _bookshelfEvent = MutableSharedFlow<List<BookEntity>>(1)
    val bookshelfEvent = _bookshelfEvent.asSharedFlow()

    private val _clickItemEvent = MutableSharedFlow<BookEntity>(0)

    val clickItemEvent = _clickItemEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            bookDataSource.getBookAll().collect {
                _bookshelfEvent.emit(it)
            }
        }
    }

    fun clickItem(bookEntity: BookEntity) {
        viewModelScope.launch {
            _clickItemEvent.emit(bookEntity)
        }
    }

}