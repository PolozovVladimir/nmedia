package ru.netology.nmedia.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthApiService

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthRetrofit