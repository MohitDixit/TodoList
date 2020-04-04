package com.nagarro.kotlinfundamentals.views.todo

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.repository.TodoListRepository
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
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

    @ExperimentalCoroutinesApi
    @Test
    fun loadTodoListTest() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = false
        val list = listOf(todoData)
        runBlockingTest {
            Mockito.`when`(apiInterface.getJsonResponse()).thenReturn(list)
            todoListActivityViewModel.loadTodoList()
            assertEquals(list, todoListActivityViewModel.todoListResult().value)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadTodoListTestError() {
        val throwable = RuntimeException()
        runBlockingTest {
            Mockito.`when`(apiInterface.getJsonResponse()).thenThrow(throwable)
            todoListActivityViewModel.loadTodoList()
            assertEquals(throwable, todoListActivityViewModel.todoListError().value)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadTodoListTestLoader() {
        val todoData = TodoData()
        todoData.title = "Sam"
        todoData.completed = false
        val list = listOf(todoData)
        runBlockingTest {
            Mockito.`when`(apiInterface.getJsonResponse()).thenReturn(list)
            todoListActivityViewModel.loadTodoList()
            assertEquals(false, todoListActivityViewModel.todoListLoader().value)
        }
    }
}