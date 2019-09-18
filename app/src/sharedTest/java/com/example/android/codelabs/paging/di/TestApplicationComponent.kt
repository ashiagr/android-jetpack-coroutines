package com.example.android.codelabs.paging.di

import android.content.Context
import com.example.android.codelabs.paging.TestMyApplication
import com.example.android.codelabs.paging.data.GithubRepository
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestApplicationModule::class,
    AndroidSupportInjectionModule::class,
    ViewModelBuilder::class,
    RepoModule::class])
interface TestApplicationComponent : AndroidInjector<TestMyApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestApplicationComponent
    }

    val githubRepository: GithubRepository
}
