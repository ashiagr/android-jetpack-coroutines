package com.example.android.codelabs.paging.di

import dagger.Module
import dagger.Provides
import io.mockk.mockk

@Module
object TestAppViewModelFactoryModule {

    val viewModelFactory: AppViewModelFactory = mockk()

    @JvmStatic
    @Provides
    fun provideAppViewModelFactory(): AppViewModelFactory {
        return viewModelFactory
    }
}