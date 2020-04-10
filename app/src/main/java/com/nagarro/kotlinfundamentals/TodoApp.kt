package com.nagarro.kotlinfundamentals

import androidx.multidex.MultiDexApplication
import com.nagarro.kotlinfundamentals.di.ContextModule
import com.nagarro.kotlinfundamentals.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class TodoApp : MultiDexApplication(), HasAndroidInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = activityDispatchingAndroidInjector
}