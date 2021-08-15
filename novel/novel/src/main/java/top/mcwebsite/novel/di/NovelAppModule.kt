package top.mcwebsite.novel.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.novel.ui.discovery.DiscoveryViewModel

val appModule = module {

    viewModel {
        DiscoveryViewModel()
    }
}