package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.di.AuthApiService
import ru.netology.nmedia.di.AuthClient
import ru.netology.nmedia.di.AuthRetrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {

    @Provides
    @Singleton
    @AuthClient
    fun provideAuthOkHttpClient(
        baseClient: OkHttpClient,
        appAuth: AppAuth
    ): OkHttpClient = baseClient.newBuilder()
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                chain.proceed(newRequest)
            } ?: chain.proceed(chain.request())
        }
        .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthClient client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @AuthApiService
    fun provideAuthApiService(
        @AuthRetrofit retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)
}