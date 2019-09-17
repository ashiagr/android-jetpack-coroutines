package com.example.android.codelabs.paging.data

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutinesDispatcherProvider(
    val mainDispatcher: CoroutineDispatcher,
    val ioDispatcher: CoroutineDispatcher,
    val computationDispatcher: CoroutineDispatcher
)