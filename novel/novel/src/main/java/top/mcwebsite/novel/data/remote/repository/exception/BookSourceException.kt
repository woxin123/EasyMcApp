package top.mcwebsite.novel.data.remote.repository.exception

class BookSourceException(
    val bookSource: String,
    cause: Throwable,
) : Exception(cause)
