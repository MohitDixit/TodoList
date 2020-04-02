package com.nagarro.kotlinfundamentals.views.todo


import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import javax.inject.Inject

class TodoListActivityViewModel @Inject constructor(
    private val orderListRepository: TodoListRepository
) :
    ViewModel() {
    lateinit var title: String
    lateinit var isComplete: String
    var color = Color.BLACK
    val status = arrayListOf("Completed", "Not Completed")
    private var todoListResult: MutableLiveData<List<TodoData>> = MutableLiveData()
    private var todoListError: MutableLiveData<Throwable> = MutableLiveData()
    private var todoListLoader: MutableLiveData<Boolean> = MutableLiveData()

    fun todoListResult(): LiveData<List<TodoData>> {
        return todoListResult
    }

    fun todoListError(): LiveData<Throwable> {
        return todoListError
    }

    fun todoListLoader(): LiveData<Boolean> {
        return todoListLoader
    }

    fun setOrderValue(todoData: TodoData) {
        this.title = todoData.title
        this.isComplete = if (todoData.completed) {
            status[0]
        } else status[1]
        setStatusColor()
    }

    fun setStatusColor() {
        color = if (isComplete == status[0]) {
            Color.GREEN
        } else Color.RED
    }

    suspend fun loadTodoList() {
        try {
            val orders: List<TodoData> = orderListRepository.getDataFromApi()
            todoListResult.postValue(orders)
            todoListLoader.postValue(false)
        } catch (e: Throwable) {
            todoListError.postValue(e)
            todoListLoader.postValue(false)
        }
    }
}