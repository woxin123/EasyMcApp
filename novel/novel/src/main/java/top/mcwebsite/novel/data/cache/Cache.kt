package top.mcwebsite.novel.data.cache

import java.lang.ref.WeakReference

data class Cache(
    val size: Long,
    val data: WeakReference<CharArray>,
)