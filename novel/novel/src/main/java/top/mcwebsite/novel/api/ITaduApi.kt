package top.mcwebsite.novel.api

import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ITaduApi {

    @FormUrlEncoded
    @POST("search")
    suspend fun searchBook(@FieldMap requestBodyMap: Map<String, String>): String

    @GET
    suspend fun getBookInfo(@Url url: String): String

    @GET
    suspend fun getChapterList(@Url url: String): String

    @GET
    suspend fun getChapterInfo(@Url url: String): String
}
