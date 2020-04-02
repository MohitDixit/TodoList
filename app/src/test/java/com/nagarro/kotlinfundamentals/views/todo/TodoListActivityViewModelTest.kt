package com.nagarro.kotlinfundamentals.views.todo

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import javax.inject.Inject


class TodoListActivityViewModelTest {
    @Inject
    lateinit var todoListActivityViewModel: TodoListActivityViewModel

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiInterface: ApiInterface

    @InjectMocks
    lateinit var todoListRepository: TodoListRepository

    private inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

    @Mock
    lateinit var observer: androidx.lifecycle.Observer<List<TodoData>>

    @Mock
    lateinit var errorObserver: androidx.lifecycle.Observer<Throwable>

    @Mock
    lateinit var loaderObserver: androidx.lifecycle.Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        todoListActivityViewModel = TodoListActivityViewModel(todoListRepository)
        observer = mock()
        errorObserver = mock()
        loaderObserver = mock()
        todoListActivityViewModel.todoListResult().observeForever(observer)
        todoListActivityViewModel.todoListError().observeForever(errorObserver)
        todoListActivityViewModel.todoListLoader().observeForever(loaderObserver)
    }

    @Test
    fun setOrderListCompleteTest() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = true
        todoListActivityViewModel.setOrderValue(todoData)
        assertEquals(todoData.title, todoListActivityViewModel.title)
        assertEquals(todoListActivityViewModel.status[0], todoListActivityViewModel.isComplete)
    }

    @Test
    fun setOrderListNotCompleteTest() {
        val todoData = TodoData()
        todoData.title = "John"
        todoData.completed = false
        todoListActivityViewModel.setOrderValue(todoData)
        assertEquals(todoListActivityViewModel.status[1], todoListActivityViewModel.isComplete)
    }

    @Test
    fun setStatusColorTestComplete() {
        todoListActivityViewModel.isComplete = todoListActivityViewModel.status[0]
        todoListActivityViewModel.setStatusColor()
        assertEquals(Color.GREEN, todoListActivityViewModel.color)
    }

    @Test
    fun setStatusColorTestNotComplete() {
        todoListActivityViewModel.isComplete = todoListActivityViewModel.status[1]
        todoListActivityViewModel.setStatusColor()
        assertEquals(Color.RED, todoListActivityViewModel.color)
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
        val captor = ArgumentCaptor.forClass(TodoData::class.java)
        captor.run {
            verify(observer, times(1)).onChanged(listOf(capture()))
            assertEquals(list, value)
        }
    }

    @Test
    fun loadTodoListTestError() {
        val throwable = Throwable()
        Mockito.`when`(todoListRepository.getDataFromApi()).thenReturn(Observable.error(throwable))
        Mockito.`when`(apiInterface.getJsonResponse()).thenReturn(Observable.error(throwable))
        todoListActivityViewModel.loadTodoList()
        val captor = ArgumentCaptor.forClass(Throwable::class.java)
        captor.run {
            verify(errorObserver, times(1)).onChanged(capture())
            assertEquals(throwable, value)
        }
    }

    @Test
    fun loadTodoListTestLoader() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = false
        val list = listOf(todoData)
        Mockito.`when`(todoListRepository.getDataFromApi()).thenReturn(Observable.just(list))
        Mockito.`when`(apiInterface.getJsonResponse()).thenReturn(Observable.just(list))
        todoListActivityViewModel.loadTodoList()
        val captor = ArgumentCaptor.forClass(Boolean::class.java)
        captor.run {
            verify(loaderObserver, times(1)).onChanged(capture())
            assertEquals(false, value)
        }
    }
}