package droidkaigi.github.io.challenge2019.infra.api

import droidkaigi.github.io.challenge2019.infra.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.infra.api.response.UserResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {
    companion object {
        fun apiClient(): HackerNewsApi {
            return Retrofit.Builder()
                .client(
                    OkHttpClient()
                        .newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().also {
                            it.level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()
                )
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(HackerNewsApi::class.java)
        }
    }

    @GET("item/{id}.json")
    fun getItem(@Path("id") id: Long): Single<ItemResponse>

    @GET("user/{id}.json")
    fun getUser(@Path("id") id: String): Single<UserResponse>

    @GET("topstories.json")
    fun getTopStories(): Single<List<Long>>

    @GET("newstories.json")
    fun getNewStories(): Single<List<Long>>

    @GET("jobstories.json")
    fun getJobStories(): Single<List<Long>>
}
