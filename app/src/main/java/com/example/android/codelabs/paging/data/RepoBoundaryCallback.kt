/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import android.util.Log
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.db.GithubLocalCache
import com.example.android.codelabs.paging.model.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * This boundary callback gets notified when user reaches to the edges of the list for example when
 * the database cannot provide any more data.
 **/
private const val IN_QUALIFIER = "in:name,description"

class RepoBoundaryCallback(
    private val query: String,
    private val service: GithubService,
    private val cache: GithubLocalCache,
    private val coroutineScope: CoroutineScope,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : PagedList.BoundaryCallback<Repo>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        Log.d("RepoBoundaryCallback", "onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    /**
     * When all items in the database were loaded, we need to query the backend for more items.
     */
    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        Log.d("RepoBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return
        fetchGitHubReposFromNetworkAndPersist(query)
    }

    private fun fetchGitHubReposFromNetworkAndPersist(query: String) {
        isRequestInProgress = true
        coroutineScope.launch(dispatcherProvider.ioDispatcher) {
            try {
                val apiQuery = query + IN_QUALIFIER
                val response = service.searchRepos(apiQuery, lastRequestedPage, NETWORK_PAGE_SIZE)

                if (response.isSuccessful) {
                    val repos = response.body()?.items ?: emptyList()
                    cache.insert(repos)
                    lastRequestedPage++
                    isRequestInProgress = false
                } else {
                    _networkErrors.postValue(response.toString())
                    isRequestInProgress = false
                }
            } catch (exception: IOException) {
                Log.e("RepoBoundaryCallback", exception.message)
                _networkErrors.postValue(exception.message)
                isRequestInProgress = false
            }
        }
    }
}