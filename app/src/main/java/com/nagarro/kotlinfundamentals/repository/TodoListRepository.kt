package com.nagarro.kotlinfundamentals.repository

import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.api.model.TodoData
import javax.inject.Singleton

@Singleton
class TodoListRepository(private val apiInterface: ApiInterface) {
    suspend fun getDataFromApi(): List<TodoData> {
        return apiInterface.getJsonResponse()
    }
}