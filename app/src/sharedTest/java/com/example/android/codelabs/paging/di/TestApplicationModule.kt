package com.example.android.codelabs.paging.di

import com.example.android.codelabs.paging.data.GithubRepository
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

/**
 * A replacement for [ApplicationModule] to be used in tests.
 */
@Module
object TestApplicationModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideGithubRepository(): GithubRepository {
        val githubRepository: GithubRepository = mockk()
        return githubRepository
    }
}
