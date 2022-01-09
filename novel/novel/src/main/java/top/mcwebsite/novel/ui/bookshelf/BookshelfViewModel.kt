package top.mcwebsite.novel.ui.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity

class BookshelfViewModel(private val bookDataSource: IBookDatasource): ViewModel() {

    private val _bookshelfEvent = MutableSharedFlow<List<BookEntity>>(1)
    val bookshelfEvent = _bookshelfEvent.asSharedFlow()

    private val _clickItemEvent = MutableSharedFlow<BookEntity>(0)

    val clickItemEvent = _clickItemEvent.asSharedFlow()

    private val _checkedBookNum = MutableSharedFlow<Int>()
    val checkBookNumFlow = _checkedBookNum.asSharedFlow()

    private val checkedBooks = mutableListOf<BookEntity>()

    private val _removeBookEvent = MutableSharedFlow<BookEntity>()
    val removeBookEvent = _removeBookEvent.asSharedFlow()

    private val _editStatus = MutableStateFlow(false)
    val editStatus = _editStatus.asStateFlow()

    private val _selectAllEvent = MutableSharedFlow<Unit>()
    val selectAllEvent = _selectAllEvent.asSharedFlow()

    private val _onFinishEvent = MutableSharedFlow<Unit>()
    val onFinishEvent = _onFinishEvent.asSharedFlow()

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

    fun onBookSelected(isChecked: Boolean, bookEntity: BookEntity) {
        if (isChecked) {
            checkedBooks.add(bookEntity)
        } else {
            checkedBooks.remove(bookEntity)
        }
        viewModelScope.launch {
            _checkedBookNum.emit(checkedBooks.size)
        }
    }

    fun deleteSelectedBooks() {
        viewModelScope.launch {
            bookDataSource.removeBooks(checkedBooks.toList())
            checkedBooks.forEach {
                _removeBookEvent.emit(it)
            }
        }

        checkedBooks.clear()
    }

    fun onSelectAllClick() {
        viewModelScope.launch {
            _selectAllEvent.emit(Unit)
        }
    }

    fun onFinishClick() {
        viewModelScope.launch {
            _onFinishEvent.emit(Unit)
        }
    }

    fun changeEditStus(edit: Boolean) {
        viewModelScope.launch {
            _editStatus.emit(edit)
            if (!edit) {
                checkedBooks.clear()
                _checkedBookNum.emit(0)
            }
        }
    }

}