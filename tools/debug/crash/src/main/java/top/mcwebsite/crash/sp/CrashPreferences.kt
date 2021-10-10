package top.mcwebsite.crash.sp

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object CrashPreferences {


    private const val CRASH_SHARED_PREFERENCE = "crash_shared_preference"
    private const val LAST_CRASH_TIME = "last_crash_time"

    fun getLastCrashTime(context: Context): Long {
        return context.getSharedPreferences(CRASH_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .getLong(LAST_CRASH_TIME, 0L)
    }


    fun saveLastCrashTime(context: Context, timeStamp: Long) {
        context.getSharedPreferences(CRASH_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
            .putLong(CRASH_SHARED_PREFERENCE, timeStamp).apply()
    }

}