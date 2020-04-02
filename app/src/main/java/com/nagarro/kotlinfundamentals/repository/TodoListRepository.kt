package com.nagarro.kotlinfundamentals.repository

import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.api.model.TodoData
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoListRepository @Inject constructor(
    private val apiInterface: ApiInterface
) {
    internal fun getDataFromApi(): Observable<List<TodoData>> {
        return apiInterface.getJsonResponse()
    }
}