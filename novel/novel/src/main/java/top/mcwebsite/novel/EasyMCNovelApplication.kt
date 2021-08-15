package top.mcwebsite.novel

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import top.mcwebsite.novel.di.appModule

class EasyMCNovelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyMCNovelApplication)
            modules(appModule)
        }
    }

}