package top.mcwebsite.novel.api

import retrofit2.http.*

interface ITaduApi {

    @FormUrlEncoded
    @POST("search")
    suspend fun searchBook(@FieldMap requestBodyMap: Map<String, String>): String

    @GET
    suspend fun getBookInfo(@Url url: String): String

    @GET
    suspend fun getChapterList(@Url url: String): String

    @GET
    suspend fun getChapterInfo(@Url url: String)
}