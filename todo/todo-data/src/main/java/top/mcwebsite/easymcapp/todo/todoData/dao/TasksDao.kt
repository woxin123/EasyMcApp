package top.mcwebsite.easymcapp.todo.todoData.dao

import androidx.room.Dao
import androidx.room.Query
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

@Dao
abstract class TasksDao : EntityDao<TaskEntity>() {
    @Query("SELECT * FROM task")
    abstract suspend fun queryAll(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id = :taskId")
    abstract suspend fun queryById(taskId: Long): TaskEntity

    @Query("DELETE FROM task")
    abstract suspend fun deleteAll(): Int
}
