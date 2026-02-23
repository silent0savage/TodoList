package com.example.todoList.repository

import com.example.todoList.data.local.TaskDao
import com.example.todoList.data.local.TaskEntity

// посредник между источником данных и юзером. Логика получения данных
// по сути вызывает методы TaskDao
class TaskRepository(
    private val taskDao: TaskDao
) {
    fun getAll() = taskDao.getAll()
    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
}