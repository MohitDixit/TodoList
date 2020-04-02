package com.nagarro.kotlinfundamentals.views.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.DisposableObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import javax.inject.Inject


class TodoListActivityViewModelTest {
    @Inject
    lateinit var todoListActivityViewModel: TodoListActivityViewModel

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    var orderListResult: MutableLiveData<List<TodoData>> = MutableLiveData()

    @Mock
    var orderListError: MutableLiveData<String> = MutableLiveData()

    @Mock
    var orderListLoader: MutableLiveData<Boolean> = MutableLiveData()

    @Mock
    private lateinit var disposableObserver: DisposableObserver<List<TodoData>>

    @Mock
    lateinit var apiInterface: ApiInterface

    @InjectMocks
    lateinit var todoListRepository: TodoListRepository




    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        todoListActivityViewModel = TodoListActivityViewModel(todoListRepository)
    }

    @Test
    fun setOrderListCompleteTest() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = true
        todoListActivityViewModel.setOrderValue(todoData)
    }

    @Test
    fun setOrderListNotCompleteTest() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = false
        todoListActivityViewModel.setOrderValue(todoData)
    }

    @Test
    fun loadTodoListTest() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = false
        val list = listOf(todoData)
        Mockito.`when`(todoListRepository.getDataFromApi()).thenReturn(Observable.just(list))
        Mockito.`when`(apiInterface.getJsonResponse()).thenReturn(Observable.just(list))
        todoListActivityViewModel.loadTodoList()
    }
}