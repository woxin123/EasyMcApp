package top.mcwebsite.novel.config

import android.graphics.Color
import top.mcwebsite.common.ui.utils.dp

/**
 * 阅读的配置类
 * 尝试用 pb，但是对于默认值时一个麻烦事，所以先用 SP
 */
class ReadConfig{

    // 缓存相关
    var lruCacheSize: Int by ReadConfigSP("lru_cache_size", 20)
    // 缓存相关

    // 字体相关
    var textSize: Float by ReadConfigSP("text_size", 20F.dp)
    var textColor: Int by ReadConfigSP("text_color", Color.BLACK)
    var titleSize: Float by ReadConfigSP("title_text_size", 25F.dp)

    // 字体相关
    var backgroundColor: Int by ReadConfigSP("background_color", Color.parseColor("#CEC29D"))

}