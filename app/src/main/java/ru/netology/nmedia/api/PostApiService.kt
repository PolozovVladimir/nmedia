package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.PostDto
import java.util.concurrent.TimeUnit

interface ApiServiceInterface {
    @GET("posts")
    suspend fun getAll(): Response<List<PostDto>>

    @POST("posts")
    suspend fun save(@Body post: PostDto): Response<PostDto>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}

object ApiService {
    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    val service: ApiServiceInterface by lazy {
        retrofit.create(ApiServiceInterface::class.java)
    }
}