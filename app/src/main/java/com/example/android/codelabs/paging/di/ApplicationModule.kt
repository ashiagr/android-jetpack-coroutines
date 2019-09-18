package com.example.android.codelabs.paging.di

import android.content.Context
import androidx.room.Room
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.data.CoroutinesDispatcherProvider
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.db.GithubLocalCache
import com.example.android.codelabs.paging.db.RepoDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object ApplicationModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideGithubService(): GithubService {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        return Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(OkHttpClient().newBuilder().addInterceptor(logger).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGithubRepository(
        githubService: GithubService,
        cache: GithubLocalCache,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): GithubRepository {
        return GithubRepository(githubService, cache, dispatcherProvider)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGithubLocalCache(
        database: RepoDatabase,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): GithubLocalCache {
        return GithubLocalCache(database.reposDao(), dispatcherProvider)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideCoroutinesDispatcherProvider(): CoroutinesDispatcherProvider {
        return CoroutinesDispatcherProvider(
                Dispatchers.Main,
                Dispatchers.IO,
                Dispatchers.Default
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): RepoDatabase {
        return Room.databaseBuilder(context.applicationContext,
                RepoDatabase::class.java, "Github.db")
                .build()
    }
}