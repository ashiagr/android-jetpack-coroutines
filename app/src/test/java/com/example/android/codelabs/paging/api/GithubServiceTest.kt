package com.example.android.codelabs.paging.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.BufferedSource
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.After
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubServiceTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: GithubService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
    }

    @Test
    fun searchTest() {
        enqueueResponse("repo.json")
        val query = "android"
        val page = 1
        val perPage = 2
        runBlocking {
            val reposReponse: Response<RepoSearchResponse> = service.searchRepos(
                    query,
                    page,
                    perPage
            )
            val request = mockWebServer.takeRequest()
            assertThat(request.path,
                    `is`("/search/repositories?sort=stars&q=android&page=1&per_page=2")
            )
            assertThat<Response<RepoSearchResponse>>(reposReponse, IsNull.notNullValue())
        }
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
                .getResourceAsStream("api-response/$fileName")

        val source: BufferedSource = inputStream.source().buffer()

        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
                mockResponse
                        .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
