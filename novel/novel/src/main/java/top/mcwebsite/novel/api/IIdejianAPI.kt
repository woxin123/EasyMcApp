package top.mcwebsite.novel.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface IIdejianAPI {

    @GET
    suspend fun getBookInfo(@Url url: String): String

    @GET("/search")
    suspend fun searchBook(@Query("keyword") keyword: String): String

    @GET("/catelog/{id}")
    suspend fun getChapterLists(@Path(value = "id") id: String, @Query("page") page: Int): DeJianChapterPage

    @GET
    suspend fun getChapterInfo(@Url url: String): String
}

data class DeJianChapterPage(
    val code: Int,
    val html: String,
)