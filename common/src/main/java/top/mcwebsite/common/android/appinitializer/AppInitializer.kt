package top.mcwebsite.common.android.appinitializer

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}
