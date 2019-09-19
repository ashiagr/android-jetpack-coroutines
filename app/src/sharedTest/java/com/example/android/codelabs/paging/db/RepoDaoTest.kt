package com.example.android.codelabs.paging.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.codelabs.paging.data.flutterRepo
import com.example.android.codelabs.paging.getOrAwaitValue
import com.example.android.codelabs.paging.model.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RepoDaoTest {

    private lateinit var database: RepoDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RepoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun insertReposRetrieveReposFromDaoTest() = runBlocking {
        // Given - insert a task
        val repoList = listOf<Repo>(flutterRepo)
        database.reposDao().insert(repoList)

        // When - Get tasks from the database
        val dataSourceFactory = database.reposDao().reposByName(flutterRepo.name)
        // Get the paged list
        val defaultConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(repoList.size)
                .build()
        val data = LivePagedListBuilder(dataSourceFactory, defaultConfig)
                .build()
        val result = data.getOrAwaitValue()

        // Then - There is only 1 task in the database, and contains the expected values
        assertThat(result.size, `is`(1))
        Assert.assertThat((result[0] as Repo).name, `is`(flutterRepo.name))
    }
}
