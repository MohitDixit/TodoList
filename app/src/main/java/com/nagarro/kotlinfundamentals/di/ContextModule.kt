package com.nagarro.kotlinfundamentals.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.nagarro.kotlinfundamentals.util.Utils
import com.nagarro.kotlinfundamentals.views.todo.TodoListActivityViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideTodoListActivityViewModelFactory(
        factory: TodoListActivityViewModelFactory
    ): ViewModelProvider.Factory = factory

    @Provides
    @Singleton
    fun provideUtils(): Utils = Utils(context)
}