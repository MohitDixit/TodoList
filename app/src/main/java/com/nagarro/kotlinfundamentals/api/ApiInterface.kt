package com.nagarro.kotlinfundamentals.api

import com.nagarro.kotlinfundamentals.api.model.TodoData
import retrofit2.http.GET

interface ApiInterface {
    @GET("/todos")
   suspend fun getJsonResponse(): List<TodoData>
}