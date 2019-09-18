package com.example.android.codelabs.paging.di

import androidx.lifecycle.ViewModel
import com.example.android.codelabs.paging.ui.SearchRepositoriesFragment
import com.example.android.codelabs.paging.ui.SearchRepositoriesViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger module for the repo list feature.
 */
@Module
abstract class RepoModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun searchRepositoriesFragment(): SearchRepositoriesFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchRepositoriesViewModel::class)
    abstract fun bindViewModel(viewmodel: SearchRepositoriesViewModel): ViewModel
}
