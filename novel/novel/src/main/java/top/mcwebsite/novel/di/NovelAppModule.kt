package top.mcwebsite.novel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.data.local.datasource.IBookDatasource
import top.mcwebsite.novel.data.local.datasource.IChapterDatasource
import top.mcwebsite.novel.data.local.datasource.impl.BookDataSourceImpl
import top.mcwebsite.novel.data.local.datasource.impl.ChapterDatasourceImpl
import top.mcwebsite.novel.data.local.db.NovelDataBase
import top.mcwebsite.novel.data.local.serizlizer.SearchHistories
import top.mcwebsite.novel.data.local.serizlizer.SearchHistoriesSerializer
import top.mcwebsite.novel.data.remote.repository.BookRepositoryManager
import top.mcwebsite.novel.data.remote.repository.impl.IdejianBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository
import top.mcwebsite.novel.ui.bookdetail.BookDetailViewModel
import top.mcwebsite.novel.ui.bookshelf.BookshelfViewModel
import top.mcwebsite.novel.ui.search.SearchViewModel
import top.mcwebsite.novel.ui.discovery.DiscoveryViewModel
import top.mcwebsite.novel.ui.home.MainViewModel
import top.mcwebsite.novel.ui.rank.RankViewModel
import top.mcwebsite.novel.ui.read.ReadViewModel
import top.mcwebsite.novel.ui.read.page.PageViewDrawer

private val Context.searchHistoryDataStore : DataStore<SearchHistories> by dataStore(
    fileName = "search_history.pb",
    serializer = SearchHistoriesSerializer,
)

val appModule = module {

    single {
        androidApplication().searchHistoryDataStore
    }

    single { TaduBookRepository() }

    single { Yb3BookRepository() }

    single { IdejianBookRepository() }

    single { BookRepositoryManager() }

    viewModel {
        MainViewModel()
    }

    viewModel {
        SearchViewModel(get())
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            NovelDataBase::class.java,
            "novel.db"
        ).build()
    }

    single<IBookDatasource> {
        BookDataSourceImpl(get<NovelDataBase>().bookDao())
    }

    single<IChapterDatasource> {
        ChapterDatasourceImpl(get<NovelDataBase>().chapterDao())
    }

    single {
        ReadConfig()
    }

    viewModel {
        DiscoveryViewModel()
    }

    viewModel {
        BookDetailViewModel(get())
    }

    viewModel {
        BookshelfViewModel(get(), get(), get())
    }

    viewModel {
        ReadViewModel(get(), get(), get())
    }

    viewModel {
        RankViewModel(get())
    }
}