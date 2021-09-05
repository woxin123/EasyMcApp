package top.mcwebsite.novel.ui.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.ui.read.view.PageViewDrawer

@ExperimentalCoroutinesApi
class ReadViewModel(private val bookRepository: BookRepositoryManager) : ViewModel(), KoinComponent {

     companion object {
          const val TAG = "ReadViewModel"
     }

     lateinit var bookEntity: BookEntity
          private set

     private lateinit var bookModel: BookModel

     private val pageViewDrawer: PageViewDrawer by inject()

     lateinit var pageProvider: PageProvider


     private var _chapterList = MutableSharedFlow<List<Chapter>>()
     val chapterList = _chapterList.asSharedFlow()
     private var chapters: List<Chapter> = emptyList()

     fun setBook(bookEntity: BookEntity) {
          this.bookEntity = bookEntity
          init()
     }

     private fun init() {
          bookModel = bookEntity.transform()

          viewModelScope.launch {
               bookRepository.getBookChapters(bookModel).collect {
                    Log.d(TAG, "init: $it")
                    initData(it)
                    _chapterList.emit(it)
               }
          }
     }

     private fun initData(chapterList: List<Chapter>) {
          chapters = chapterList
          pageProvider = PageProvider(bookEntity, chapters, requestChapter = {
               bookRepository.getChapterInfo(bookModel, chapters[it])
          }, viewModelScope, pageViewDrawer)

     }




}