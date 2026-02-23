package com.example.todoList.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoList.data.local.DatabaseProvider
import com.example.todoList.data.local.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// следит за состоянием UI, получает данные из repository
class TaskViewModel(application: Application): AndroidViewModel(application) {
    // хранение в бд
    private val db = DatabaseProvider.get(application)
    private val dao = db.taskDao()

    val tasks: StateFlow<List<TaskEntity>> =
        dao.getAll().stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000),
            emptyList()
        )

    var editingId by mutableStateOf<Int?>(null)
        private set

    var editingText by mutableStateOf("")
        private set

    fun addTask(text: String) {
        viewModelScope.launch {
            val nextPosition = tasks.value.size
            dao.insertTask(TaskEntity(name = text, isCompleted = false, position = nextPosition))
        }
    }

    // начало редактирования
    fun startEdit(task: TaskEntity){
        editingId = task.id
        editingText = task.name
    }

    // автосохранение задачи
    fun onEditTextChange(newText: String) {
        editingText = newText
        val id = editingId ?: return

        viewModelScope.launch {
            dao.updateName(id, newText.trim())
        }
    }

    // завершение редактирования
    fun finishEdit(){
        editingId = null
    }

    // переключить задачу
    fun toggleTask(task: TaskEntity){
        viewModelScope.launch {
            dao.updateCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity){
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun moveTask(from: Int, to: Int){
        val newList = tasks.value.toMutableList()
        val item = newList.removeAt(from)
        newList.add(to, item)

        viewModelScope.launch {
            newList.forEachIndexed { index, task ->
                dao.updatePosition(task.id, index)
            }
        }
    }
}