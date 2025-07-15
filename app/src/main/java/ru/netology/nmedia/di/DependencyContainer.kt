package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.util.concurrent.TimeUnit

class DependencyContainer (
    private val context: Context
) {

    companion object{
        private const val BASE_URL = BuildConfig.BASE_URL
    }



    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

   private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
    .fallbackToDestructiveMigration()
    .build()

    private val apiService = retrofit.create<ApiService>()

    private val postDao = appDb.postDao()

    val repository: PostRepository = PostRepositoryImpl(
        postDao,
        apiService,
    )
}