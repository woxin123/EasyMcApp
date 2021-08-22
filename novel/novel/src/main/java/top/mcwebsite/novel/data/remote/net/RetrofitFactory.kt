package top.mcwebsite.novel.data.remote.net

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitFactory : KoinComponent {

    private val context: Context by inject()

    private val okHttpClientBuild: OkHttpClient.Builder =
        OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .cache(getCache())

    fun factory(baseUrl: String): Retrofit {
        val oKHttpClient = okHttpClientBuild.build()
        return Retrofit.Builder()
            .client(oKHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }


    private fun getCache(): Cache {
        return Cache(File(context.cacheDir, "cache"), 1024 * 1024 * 100)
    }
}