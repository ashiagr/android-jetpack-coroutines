package com.example.android.codelabs.paging.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.codelabs.paging.data.flutterRepo
import com.example.android.codelabs.paging.data.materialDesignIconsRepo
import com.example.android.codelabs.paging.getOrAwaitValue
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.provideFakeCoroutinesDispatcherProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the [GithubLocalCache].
 */
@RunWith(AndroidJUnit4::class)
class GithubLocalCacheTest {

    private lateinit var localDataSource: GithubLocalCache
    private lateinit var database: RepoDatabase

    private val dispatcherProvider = provideFakeCoroutinesDispatcherProvider()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
                RepoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = GithubLocalCache(database.reposDao(), dispatcherProvider)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertReposRetrieveReposFromDBTest() = runBlocking {
        // Given - repos inserted in the database
        val repos = listOf<Repo>(flutterRepo, materialDesignIconsRepo)

        localDataSource.insert(repos)

        // When
        val dataSourceFactory = localDataSource.reposByName("flutter")
        // Get the paged list
        val defaultConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(repos.size)
                .build()
        val data = LivePagedListBuilder(dataSourceFactory, defaultConfig)
                .build()
        val result = data.getOrAwaitValue()

        // Then - Same repo is returned
        assertThat(result.size, `is`(1))
        assertThat((result[0] as Repo).name, `is`("flutter"))
    }
}
