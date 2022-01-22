package top.mcwebsite.novel.ui.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.Chapter

class BookshelfViewModel(
    private val bookDataSource: IBookDatasource,
    private val chapterSource: IChapterDatasource,
    private val bookRepositoryManager: BookRepositoryManager,
): ViewModel() {

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

    private val _updatedBookEvent = MutableSharedFlow<List<Pair<Int, BookEntity>>>()
    val updatedBookEvent = _updatedBookEvent.asSharedFlow()

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

    fun onRefreshBooks() {
        viewModelScope.launch {
            val updatedBook = mutableListOf<Pair<Int, BookEntity>>()
            _bookshelfEvent.first().forEachIndexed { index, book ->
                bookRepositoryManager.getBookChapters(book.transform()).collect {
                    val oldChapters = chapterSource.getChaptersByBid(book.bid)
                    if (isUpdate(oldChapters, it)) {
                        updatedBook.add(index to book)
                        book.isUpdate = true
                        bookDataSource.update(book)
                    }
                }
            }
            _updatedBookEvent.emit(updatedBook)
        }
    }

    private fun isUpdate(oldChapterList: List<ChapterEntity>, newChapterEntity: List<Chapter>): Boolean {
        return newChapterEntity.size > oldChapterList.size
    }

}