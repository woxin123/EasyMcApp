package top.mcwebsite.novel.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.BookModel

class BookDetailViewModel(private val bookRepository: BookRepositoryManager) : ViewModel() {

    lateinit var book: BookModel

    private val _updateBookModel = MutableSharedFlow<Unit>()
    val updateBookModel = _updateBookModel.asSharedFlow()

    fun setBookModel(book: BookModel) {
        this.book = book
        viewModelScope.launch {
            // TODO 添加缓存层
            if (book.introduce.isBlank()) {
                bookRepository.getBookInfo(book).collect {
                    _updateBookModel.emit(Unit)
                }
            }
        }
    }
}
