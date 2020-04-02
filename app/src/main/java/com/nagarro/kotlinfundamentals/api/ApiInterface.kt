package com.nagarro.kotlinfundamentals.api

import com.nagarro.kotlinfundamentals.api.model.TodoData
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {

    @GET("/todos")
    fun getJsonResponse(): Observable<List<TodoData>>
}