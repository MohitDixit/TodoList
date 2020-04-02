package com.nagarro.kotlinfundamentals.views.todo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.kotlinfundamentals.R
import com.nagarro.kotlinfundamentals.api.model.TodoData
import com.nagarro.kotlinfundamentals.databinding.TodoListItemBinding
import java.util.*

class TodoListAdapter(private val todoList: List<TodoData>) :
    RecyclerView.Adapter<TodoListAdapter.ItemViewHolder>() {

    lateinit var todoListActivityViewModel: TodoListActivityViewModel

    private var todoListItems = ArrayList<TodoData>()

    init {
        this.todoListItems = todoList as ArrayList<TodoData>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<TodoListItemBinding>(
                layoutInflater,
                R.layout.todo_list_item,
                parent,
                false
            )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(todoList[position], todoListActivityViewModel)

    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun addToDos(orders: List<TodoData>) {
        val initPosition = todoList.size
        todoListItems.addAll(orders)
        Log.e("list size", todoListItems.size.toString())
        notifyItemRangeInserted(initPosition, todoListItems.size)
    }

    fun setViewModel(todoListActivityViewModel: TodoListActivityViewModel) {
        this.todoListActivityViewModel = todoListActivityViewModel
    }


    class ItemViewHolder(private val binding: TodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(
            model: TodoData,
            todoListActivityViewModel: TodoListActivityViewModel
        ) {
            todoListActivityViewModel.setOrderValue(model)
            binding.todoListActivityViewModel = todoListActivityViewModel
            binding.executePendingBindings()
        }
    }
}