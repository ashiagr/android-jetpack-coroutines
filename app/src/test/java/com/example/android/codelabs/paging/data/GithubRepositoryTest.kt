package com.example.android.codelabs.paging.data

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.RepoSearchResponse
import com.example.android.codelabs.paging.getOrAwaitValue
import com.example.android.codelabs.paging.db.GithubLocalCache
import com.example.android.codelabs.paging.model.RepoSearchResult
import com.example.android.codelabs.paging.provideFakeCoroutinesDispatcherProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import retrofit2.Response

class GithubRepositoryTest {

    // Class under test
    private lateinit var githubRepository: GithubRepository

    // Executes tasks in the Architecture Components in the same thread
    // We don't want anything happening on background thread,
    // We want things to happen synchronously
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repoList = listOf(flutterRepo, materialDesignIconsRepo)

    private var githubService: GithubService = mockk()
    private var githubLocalCache: GithubLocalCache = mockk()

    private val dispatcherProvider = provideFakeCoroutinesDispatcherProvider()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Before
    fun createRepository() {
        // Get a reference to the class under test
        githubRepository = GithubRepository(
                githubService, githubLocalCache, dispatcherProvider
        )
    }

    @Test
    fun searchZeroItemsLoadedBoundaryTest() {
        // Given
        every {
            githubLocalCache.reposByName(any())
        } returns
        createMockDataSourceFactory(emptyList())
        coEvery() {
            githubService.searchRepos(any(), any(), any())
        } returns (Response.success(RepoSearchResponse(repoList.count(), repoList, 2)))

        coEvery() {
            githubLocalCache.insert(repoList)
        } answers { nothing }

        // When
        val result: RepoSearchResult = githubRepository.search("android")
        val pagedList = result.data.getOrAwaitValue()

        // Paged list size is zero
        assertThat(pagedList.size, `is`(0))
        // Verify network method searchRepos is called when zero items in the database
        coVerify {
            githubService.searchRepos(any(), any(), any())
        }
    }

    @After
    fun cleanUp() {
        githubRepository.cancelAllRequests()
    }
}
