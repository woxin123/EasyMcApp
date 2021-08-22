package top.mcwebsite.novel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.novel.data.local.datastore.SearchHistories
import top.mcwebsite.novel.data.local.datastore.SearchHistoriesSerializer
import top.mcwebsite.novel.data.remote.repository.impl.BookRepositoryManager
import top.mcwebsite.novel.data.remote.repository.impl.TaduBookRepository
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository
import top.mcwebsite.novel.ui.bookdetail.BookDetailViewModel
import top.mcwebsite.novel.ui.search.SearchViewModel
import top.mcwebsite.novel.ui.discovery.DiscoveryViewModel

private val Context.searchHistoryDataStore : DataStore<SearchHistories> by dataStore(
    fileName = "search_history.pb",
    serializer = SearchHistoriesSerializer,
)

val appModule = module {

    viewModel {
        SearchViewModel()
    }

    viewModel {
        DiscoveryViewModel()
    }

    viewModel {
        BookDetailViewModel()
    }

    single {
        androidApplication().searchHistoryDataStore
    }

    single { TaduBookRepository() }

    single { Yb3BookRepository() }

    single { BookRepositoryManager() }
}