package top.mcwebsite.easymcapp.todoApp

import android.app.Application
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.INFO
import top.mcwebsite.easymcapp.todoApp.appinitializers.AppInitializers
import top.mcwebsite.easymcapp.todoApp.di.appModule

class ToDoApplication : Application() {

    private val appInitializers: AppInitializers by inject()

    override fun onCreate() {
        super.onCreate()
        // 第一步，初始化 koin
        startKoin {
//            androidLogger(INFO)
            androidContext(this@ToDoApplication)
            modules(appModule)
        }
        appInitializers.init(this)
    }
}
