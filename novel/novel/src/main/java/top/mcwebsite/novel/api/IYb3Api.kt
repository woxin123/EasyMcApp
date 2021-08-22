package top.mcwebsite.novel.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface IYb3Api {

    @GET
    suspend fun getBookInfo(@Url url: String): String

    @GET("web/search.php")
    suspend fun searchBook(@Query("q") key: String): String

    @GET
    suspend fun getBookContent(@Url url: String): String

    @GET
    suspend fun getChapterLists(@Url url: String): String

    @GET
    suspend fun getChapterInfo(@Url url: String): String

}