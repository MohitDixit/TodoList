package com.nagarro.kotlinfundamentals.api.model

data class TodoData(val id:Int = 1, val userId:Int = 1, var title:String ="Sample", var completed:Boolean = false)

