package top.mcwebsite.easymcapp.todoApp.appinitializers

import android.app.Application
import android.util.Log
import top.mcwebsite.common.android.appinitializer.AppInitializer

class AppInitializerTest : AppInitializer {
    override fun init(application: Application) {
        Log.d("mengchen", "app init")
    }
}
