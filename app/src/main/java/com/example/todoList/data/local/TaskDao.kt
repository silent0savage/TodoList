package com.example.todoList.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// интерфейс для работы с таблицами/сущностями базы данных
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY position ASC")
    fun getAll(): Flow<List<TaskEntity>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Int, completed: Boolean)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("Update tasks SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Int, position: Int)
}