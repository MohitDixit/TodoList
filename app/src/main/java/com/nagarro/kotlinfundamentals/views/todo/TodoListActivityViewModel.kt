package com.nagarro.kotlinfundamentals.views.todo


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nagarro.kotlinfundamentals.BuildConfig
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TodoListActivityViewModel @Inject constructor(
    private val orderListRepository: TodoListRepository) :
    ViewModel() {
    lateinit var title: String
    lateinit var isComplete: String
    private val status= arrayListOf("Completed","Not Completed")
    var orderListResult: MutableLiveData<List<TodoData>> = MutableLiveData()
    var orderListError: MutableLiveData<String> = MutableLiveData()
    var orderListLoader: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var disposableObserver: DisposableObserver<List<TodoData>>

    fun todoListResult(): LiveData<List<TodoData>> {
        return orderListResult
    }

    fun todoListError(): LiveData<String> {
        return orderListError
    }

    fun todoListLoader(): LiveData<Boolean> {
        return orderListLoader
    }

    fun setOrderValue(todoData: TodoData) {
         this.title = todoData.title
         this.isComplete = if(todoData.completed){
             status[0]
         }else status[1]
    }

    fun loadTodoList() {
        disposableObserver = object : DisposableObserver<List<TodoData>>() {
            override fun onComplete() {}
            override fun onNext(orders: List<TodoData>) {
                orderListResult.postValue(orders)
                orderListLoader.postValue(false)
            }
            override fun onError(e: Throwable) {
                orderListError.postValue(e.message)
                orderListLoader.postValue(false)
            }
        }
        orderListRepository.getDataFromApi()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(BuildConfig.timeout, TimeUnit.MILLISECONDS)
            .subscribe(disposableObserver)
    }
}