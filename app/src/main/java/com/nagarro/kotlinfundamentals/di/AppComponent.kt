package com.nagarro.kotlinfundamentals.di

import com.nagarro.kotlinfundamentals.TodoApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidInjectionModule::class, NetworkModule::class, ActivityBuilder::class])
interface AppComponent {

    fun inject(app: TodoApp)
}