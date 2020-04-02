package com.nagarro.kotlinfundamentals.api.model

import java.io.Serializable

data class TodoData(val id:Int = 1, val userId:Int = 1, var title:String ="Sample", var completed:Boolean = false) : Serializable

