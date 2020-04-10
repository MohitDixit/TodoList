package com.nagarro.kotlinfundamentals.views.todo


import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoListActivityViewModel @Inject constructor(
    private val orderListRepository: TodoListRepository
) : ViewModel() {
    lateinit var title: String
    lateinit var isComplete: String
    private var isLoading = false
    var color = Color.BLACK
    val isListShow by lazy { MutableLiveData<Boolean>() }
    val isErrorShow by lazy { MutableLiveData<Boolean>() }
    val isShimmerShow by lazy { MutableLiveData<Boolean>() }
    val status by lazy { arrayListOf("Completed", "Not Completed") }
    private val todoListResult by lazy { MutableLiveData<List<TodoData>>() }
    private val todoListError by lazy { MutableLiveData<Throwable>() }

    fun todoListResult(): LiveData<List<TodoData>> {
        return todoListResult
    }

    fun todoListError(): LiveData<Throwable> {
        return todoListError
    }

    fun setOrderValue(todoData: TodoData) {
        title = todoData.title
        isComplete = if (todoData.completed) {
            status[0]
        } else status[1]
        setStatusColor()
    }

    fun setStatusColor() {
        color = when (isComplete) {
            status[0] -> Color.GREEN
            else -> Color.RED
        }
    }

    fun loadTodoList() {
        if (!isLoading) {
            setLoadingIndicators(isLoad = true)
            viewModelScope.launch {
                delay(1000)
                try {
                    val orders: List<TodoData> = orderListRepository.getDataFromApi()
                    todoListResult.postValue(orders)
                    setViewIndicators(isOK = true)
                } catch (e: Throwable) {
                    todoListError.postValue(e)
                    setViewIndicators(isOK = false)
                } finally {
                    setLoadingIndicators(isLoad = false)
                }
            }
        }
    }

    private fun setViewIndicators(isOK: Boolean) {
        isListShow.postValue(isOK)
        isErrorShow.postValue(!isOK)
    }

    private fun setLoadingIndicators(isLoad: Boolean) {
        isLoading = isLoad
        isShimmerShow.postValue(isLoad)
        when (isLoad) {
            true -> isErrorShow.postValue(!isLoad)
        }
    }
}