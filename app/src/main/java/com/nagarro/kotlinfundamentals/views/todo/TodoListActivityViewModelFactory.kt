package com.nagarro.kotlinfundamentals.views.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import javax.inject.Inject

class TodoListActivityViewModelFactory @Inject constructor(
    private val apiInterface: ApiInterface
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TodoListActivityViewModel::class.java)) {
            val orderListRepository = TodoListRepository(apiInterface)
            return TodoListActivityViewModel(orderListRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}