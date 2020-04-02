package com.nagarro.kotlinfundamentals.views.todo

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
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
import com.nagarro.kotlinfundamentals.api.ApiInterface
import com.nagarro.kotlinfundamentals.databinding.ActivityTodoListBinding
import com.nagarro.kotlinfundamentals.util.Utils
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoListActivity : AppCompatActivity() {
    private lateinit var todoListActivityViewModel: TodoListActivityViewModel
    private var todoListAdapter = TodoListAdapter(ArrayList())

    @Inject
    lateinit var apiInterface: ApiInterface
    private val compositeDisposable by lazy { CompositeDisposable() }
    private var isLoading: Boolean = false
    @Inject
    lateinit var todoListActivityViewModelFactory: TodoListActivityViewModelFactory
    @Inject
    lateinit var utils: Utils

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
        activityTodoListBinding.todoListActivityViewModel = todoListActivityViewModel
        setUpViews(activityTodoListBinding)
    }

    private fun setUpViews(activityTodoListBinding: ActivityTodoListBinding) {
        val recyclerView = activityTodoListBinding.todoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        todoListAdapter.setViewModel(todoListActivityViewModel)
        todoListActivityViewModel.todoListResult().observe(this,
            Observer {
                isLoading = false
                if (it != null && it.isNotEmpty()) {
                    todoListAdapter.addTodos(it)
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
                isLoading = false
                shimmerFrameLayout.stopShimmer()
                utils.showViews(recyclerView)
                utils.hideViews(shimmerFrameLayout)
            } else
                Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT)
                    .show()
        })

        todoListActivityViewModel.todoListLoader().observe(this, Observer {
            if (it == false) {
                isLoading = false
                shimmerFrameLayout.stopShimmer()
                utils.showViews(recyclerView)
                utils.hideViews(shimmerFrameLayout)
            }
        })
    }

    private fun loadData() {
        if (!isLoading) {
            isLoading = true
            shimmerFrameLayout.startShimmer()
            utils.showViews(shimmerFrameLayout)
            utils.hideViews(todoRecyclerView)
            val job = Job()
            val coRoutineScope = CoroutineScope(job + Dispatchers.Main)
            Handler().postDelayed({
                coRoutineScope.launch {
                    todoListActivityViewModel.loadTodoList()
                }
            }, 1000)
        }
    }


    private fun showErrorAlert() {
        utils.showViews(errorTextView)
        utils.hideViews(todoRecyclerView)
        val spannableString = SpannableString(getString(R.string.error_retry_string))
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0, 21,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                loadData()
                utils.showViews(todoRecyclerView)
                utils.hideViews(errorTextView)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@TodoListActivity, R.color.reload_color)
            }
        }
        spannableString.setSpan(
            clickableSpan,
            spannableString.indexOf(getString(R.string.reload)),
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        errorTextView.movementMethod = LinkMovementMethod.getInstance()
        errorTextView.text = spannableString
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
