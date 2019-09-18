package com.example.android.codelabs.paging.ui

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.data.asPagedList
import com.example.android.codelabs.paging.data.flutterRepo
import com.example.android.codelabs.paging.data.materialDesignIconsRepo
import com.example.android.codelabs.paging.di.DaggerTestApplicationRule
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import io.mockk.every
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
class SearchRepositioriesFragmentTest {

    private lateinit var repository: GithubRepository

    /**
     * Sets up Dagger components for testing.
     */
    @get:Rule
    val rule = DaggerTestApplicationRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    /**
     * Gets a reference to the [GithubRepository] exposed by the [DaggerTestApplicationRule].
     */
    @Before
    fun setupDaggerComponent() {
        repository = rule.component.githubRepository
    }

    @Test
    fun displaySearchRepoResultsWhenRepositoryHasData() {
        // GIVEN
        val repoList = listOf(flutterRepo, materialDesignIconsRepo)
        val networkErrors = MutableLiveData<String>()
        val data = MutableLiveData<PagedList<Repo>>().apply {
            value = repoList.asPagedList()
        }

        every {
            repository.search(any())
        } returns RepoSearchResult(data, networkErrors)

        every {
            repository.cancelAllRequests()
        } answers { nothing }


        // WHEN - On startup
        launchFragment()

        // THEN - Verify repo is displayed on screen
        onView(withText("flutter/flutter")).check(matches(isDisplayed()))
    }

    private fun launchFragment(): FragmentScenario<SearchRepositoriesFragment>? {
        val scenario = launchFragmentInContainer<SearchRepositoriesFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment {
            (it.activity?.findViewById(R.id.list) as? RecyclerView)?.itemAnimator = null
        }
        return scenario
    }
}
