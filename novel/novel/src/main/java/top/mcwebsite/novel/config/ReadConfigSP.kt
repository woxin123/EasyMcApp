package top.mcwebsite.novel.config

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// 改为抽象类，要不然无法复用
class ReadConfigSP<T>(
    val name: String,
    private val default: T
) : KoinComponent, ReadWriteProperty<Any?, T> {

    private val context by inject<Context>()

    private val prefs: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences("read_config", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <T> findPreference(name: String, default: T): T = with(prefs) {
        when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can not be saved into preferences")
        } as T
    }

    private fun <T> putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can not be saved into preferences")
        }.apply()
    }
}
