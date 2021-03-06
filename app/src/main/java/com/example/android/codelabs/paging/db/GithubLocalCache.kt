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

package com.example.android.codelabs.paging.db

import android.util.Log
import androidx.paging.DataSource
import com.example.android.codelabs.paging.data.CoroutinesDispatcherProvider
import com.example.android.codelabs.paging.model.Repo
import kotlinx.coroutines.withContext

/**
 * Class that handles the DAO local data source.
 */
class GithubLocalCache internal constructor (
    private val repoDao: RepoDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    /**
     * Insert a list of repos in the database, on a background thread.
     */
    suspend fun insert(repos: List<Repo>) = withContext(dispatcherProvider.ioDispatcher) {
        Log.d("GithubLocalCache", "inserting ${repos.size} repos")
        repoDao.insert(repos)
    }

    /**
     * Request a LiveData<List<Repo>> from the Dao, based on a repo name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     * @param name repository name
     */
    fun reposByName(name: String): DataSource.Factory<Int, Repo> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        return repoDao.reposByName(query)
    }
}
