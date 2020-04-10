package com.nagarro.kotlinfundamentals.views.todo

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.kotlinfundamentals.R
import com.nagarro.kotlinfundamentals.databinding.ActivityTodoListBinding
import com.nagarro.kotlinfundamentals.util.Utils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_todo_list.*
import javax.inject.Inject


class TodoListActivity : AppCompatActivity() {
    private lateinit var todoListActivityViewModel: TodoListActivityViewModel
    private val todoListAdapter by lazy { TodoListAdapter(ArrayList()) }
    @Inject
    lateinit var todoListActivityViewModelFactory: TodoListActivityViewModelFactory
    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var contextMain: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        initDataBinding()
        loadData()
    }

    private fun initDataBinding() {
        val activityTodoListBinding: ActivityTodoListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_todo_list)
        todoListActivityViewModel =
            ViewModelProviders.of(this, todoListActivityViewModelFactory)
                .get(TodoListActivityViewModel::class.java)
        with(activityTodoListBinding) {
            todoListViewModel = todoListActivityViewModel
            lifecycleOwner = this@TodoListActivity
        }
        setUpViews()
    }

    private fun setUpViews() {
        val recyclerView = todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(contextMain, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        todoListAdapter.setViewModel(todoListActivityViewModel)
        todoListActivityViewModel.todoListResult().observe(this,
            Observer { todoList ->
                if (todoList != null && todoList.isNotEmpty()) {
                    todoListAdapter.addToDos(todoList)
                    recyclerView.adapter = todoListAdapter
                } else if (utils.isConnectedToInternet()) {
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT)
                        .show()
                }
            })

        todoListActivityViewModel.todoListError().observe(this, Observer {
            if (it != null) {
                showErrorAlert()
            } else
                Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT)
                    .show()
        })
    }

    private fun loadData() {
        todoListActivityViewModel.loadTodoList()
    }

    private fun showErrorAlert() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                loadData()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                with(ds) {
                    isUnderlineText = false
                    color = ContextCompat.getColor(contextMain, R.color.reload_color)
                }
            }
        }
        val spannableString = SpannableString(getString(R.string.error_retry_string)).apply {
            setCustomSpan(clickableSpan)
        }
        with(errorTextView) {
            movementMethod = LinkMovementMethod.getInstance()
            text = spannableString
        }
    }

    private fun SpannableString.setCustomSpan(clickableSpan: ClickableSpan) {
        val spanType = arrayListOf(StyleSpan(Typeface.BOLD), clickableSpan)
        val spanStartIndex = arrayListOf(0, indexOf(getString(R.string.reload)))
        val spanEndIndex = arrayListOf(indexOf(getString(R.string.give)), length)
        for (i in 0..1) {
            setSpan(
                spanType[i],
                spanStartIndex[i],
                spanEndIndex[i],
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}


