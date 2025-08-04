package ru.netology.nmedia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(
        postDao: PostDao,
        @AuthApiService apiService: ApiService,
        appAuth: AppAuth,
        appDb: AppDb
    ): PostRepository {
        return PostRepositoryImpl(postDao, apiService, appAuth, appDb)
    }
}