package com.example.android.codelabs.paging.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import com.example.android.codelabs.paging.CustomTestRunner
import com.example.android.codelabs.paging.TestMyApplication

/**
 * JUnit rule that creates a [TestApplicationComponent] and injects the [TestMyApplication].
 *
 * Note that the `testInstrumentationRunner` property needs to point to [CustomTestRunner].
 */
class DaggerTestApplicationRule : TestWatcher() {

    lateinit var component: TestApplicationComponent
        private set

    override fun starting(description: Description?) {
        super.starting(description)

        val app = ApplicationProvider.getApplicationContext<Context>() as TestMyApplication
        component = DaggerTestApplicationComponent.factory().create(app)
        component.inject(app)
    }
}
