package com.example.android.codelabs.paging

import com.example.android.codelabs.paging.data.CoroutinesDispatcherProvider
import kotlinx.coroutines.Dispatchers.Unconfined

fun provideFakeCoroutinesDispatcherProvider(): CoroutinesDispatcherProvider =
    CoroutinesDispatcherProvider(Unconfined, Unconfined, Unconfined)