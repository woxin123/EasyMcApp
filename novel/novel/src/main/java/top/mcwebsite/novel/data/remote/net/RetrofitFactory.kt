package top.mcwebsite.novel.data.remote.net

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitFactory : KoinComponent {

//    private val context: Context by inject()

    private val okHttpClientBuild: OkHttpClient.Builder =
        OkHttpClient.Builder()
            .addInterceptor(addUserAgentInterceptor())
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
//            .cache(getCache())

    fun factory(baseUrl: String): Retrofit {
        val oKHttpClient = okHttpClientBuild.build()
        return Retrofit.Builder()
            .client(oKHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    private fun addUserAgentInterceptor(): Interceptor {
        return Interceptor {  chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            requestBuilder.addHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3")
            chain.proceed(requestBuilder.build())
        }
    }

//    private fun getCache(): Cache {
//        return Cache(File(context.cacheDir, "cache"), 1024 * 1024 * 100)
//    }
}