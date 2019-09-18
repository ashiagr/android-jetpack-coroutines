package com.example.android.codelabs.paging.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.data.asPagedList
import com.example.android.codelabs.paging.data.flutterRepo
import com.example.android.codelabs.paging.data.materialDesignIconsRepo
import com.example.android.codelabs.paging.getOrAwaitValue
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import com.example.android.codelabs.paging.utils.LiveDataTestUtil
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchRepositoriesViewModelTest {
    // Class under test
    private lateinit var searchRepositoriesViewModel: SearchRepositoriesViewModel
    private var githubRepository: GithubRepository = mockk()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        searchRepositoriesViewModel = SearchRepositoriesViewModel(githubRepository)
    }

    @Test
    fun searchReposFromRepositoryTest() {
        // Given
        val repoList = listOf(flutterRepo, materialDesignIconsRepo)

        val networkErrors = MutableLiveData<String>()
        val data = MutableLiveData<PagedList<Repo>>()

        every {
            githubRepository.search(any())
        } returns RepoSearchResult(data, networkErrors)

        // When
        searchRepositoriesViewModel.searchRepo("android")

        data.postValue(repoList.asPagedList())
        searchRepositoriesViewModel.repos.getOrAwaitValue()

        // Then
        assertThat(
            LiveDataTestUtil.getValue(searchRepositoriesViewModel.repos).size,
            `is`(repoList.size)
        )

        // When
        networkErrors.postValue("Unknown error")
        searchRepositoriesViewModel.networkErrors.getOrAwaitValue()

        // Then
        assertThat(
            LiveDataTestUtil.getValue(searchRepositoriesViewModel.networkErrors),
            `is`("Unknown error")
        )
    }
}