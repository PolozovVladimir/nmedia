package ru.netology.nmedia.service

import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GoogleApiModule {
    @Singleton
    @Provides
    fun provideGoogleApi():
            GoogleApiAvailability = GoogleApiAvailability.getInstance()
}