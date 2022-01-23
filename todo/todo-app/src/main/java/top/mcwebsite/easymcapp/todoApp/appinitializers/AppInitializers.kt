package top.mcwebsite.easymcapp.todoApp.appinitializers

import android.app.Application
import top.mcwebsite.common.android.appinitializer.AppInitializer

class AppInitializers(
    private val initializers: Set<@JvmSuppressWildcards AppInitializer>
) {
    fun init(application: Application) {
        initializers.forEach {
            it.init(application)
        }
    }
}
