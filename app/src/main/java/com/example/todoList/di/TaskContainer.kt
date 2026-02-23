package com.example.todoList.di

import android.content.Context
import com.example.todoList.data.local.DatabaseProvider
import com.example.todoList.repository.TaskRepository

class TaskContainer(context: Context
) {
    private val db by lazy { DatabaseProvider.get(context) }
    val taskRepository: TaskRepository by lazy {
        TaskRepository(db.taskDao())
    }
}