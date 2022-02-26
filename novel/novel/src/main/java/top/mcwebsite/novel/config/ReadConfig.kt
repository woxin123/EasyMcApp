package top.mcwebsite.novel.config

import android.graphics.Color
import top.mcwebsite.common.ui.utils.dp

/**
 * 阅读的配置类
 * 尝试用 pb，但是对于默认值时一个麻烦事，所以先用 SP
 */
class ReadConfig {

    companion object {
        const val MIN_TEXT_SIZE = 40
        const val MAX_TEXT_SIZE = 80
    }

    // 缓存相关
    var lruCacheSize: Int by ReadConfigSP("lru_cache_size", 20)
    val enableFileCache: Boolean by ReadConfigSP("enable_file_cache", true)
    // 缓存相关

    // 字体相关
    var textSize: Float by ReadConfigSP("text_size", 20F.dp)
    var textColor: Int by ReadConfigSP("text_color", Color.BLACK)

    // 字体相关
    var backgroundColor: Int by ReadConfigSP("background_color", Color.parseColor("#CEC29D"))

    var brightness: Float by ReadConfigSP("brightness", -1F)

    var pageMode: Int by ReadConfigSP("page_mode", 0)
}
