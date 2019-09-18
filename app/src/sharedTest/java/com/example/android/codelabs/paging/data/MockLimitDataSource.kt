package com.example.android.codelabs.paging.data

import android.database.Cursor
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.paging.LimitOffsetDataSource
import com.example.android.codelabs.paging.getOrAwaitValue
import io.mockk.every
import io.mockk.mockk

/**
 * References
 * https://github.com/googlesamples/android-architecture-components/issues/287#issuecomment-527698261
 */
class MockLimitDataSource<T>(private val itemList: List<T>) : LimitOffsetDataSource<T>(
        mockDb,
        mockQuery,
        false,
        null) {
        override fun convertRows(cursor: Cursor?): MutableList<T> = itemList.toMutableList()
        override fun countItems(): Int = itemList.count()
        override fun isInvalid(): Boolean = false
        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
            /* Not implemented */
        }

        override fun loadRange(startPosition: Int, loadCount: Int) =
                itemList.subList(startPosition, startPosition + loadCount).toMutableList()

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
            callback.onResult(itemList, 0)
        }
    }

fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
        object : DataSource.Factory<Int, T>() {
            override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
        }

private val mockQuery = mockk<RoomSQLiteQuery> {
    every { sql } returns ""
}

private val mockDb = mockk<RoomDatabase> {
    every { invalidationTracker } returns mockk(relaxUnitFun = true)
}

fun <T> List<T>.asPagedList(config: PagedList.Config? = null): PagedList<T>? {
    val defaultConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(size)
            .setMaxSize(size + 2)
            .setPrefetchDistance(1)
            .build()
    return LivePagedListBuilder<Int, T>(
            createMockDataSourceFactory(this),
            config ?: defaultConfig
    ).build().getOrAwaitValue()
}