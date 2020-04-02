package com.nagarro.kotlinfundamentals.di

import com.nagarro.kotlinfundamentals.views.todo.TodoListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindTodoListActivity(): TodoListActivity
}