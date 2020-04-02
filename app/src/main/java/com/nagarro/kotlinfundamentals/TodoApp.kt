package com.nagarro.kotlinfundamentals

import android.app.Application
import com.nagarro.kotlinfundamentals.di.DaggerAppComponent
import com.nagarro.kotlinfundamentals.di.NetworkModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class TodoApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .networkModule(NetworkModule(this))
            .build()
            .inject(this)
    }
    override fun androidInjector(): AndroidInjector<Any> = activityDispatchingAndroidInjector
}