package top.mcwebsite.easymcapp.todo.todoData.repository.task

import kotlinx.coroutines.flow.Flow
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

interface TasksDateSource {
    suspend fun saveTask(taskEntity: TaskEntity)
    suspend fun updateTask(taskEntity: TaskEntity)
    suspend fun saveOrUpdateTask(taskEntity: TaskEntity)
    suspend fun getTask(taskId: Long): TaskEntity
    suspend fun getTasks(): List<TaskEntity>
    suspend fun getTasksUseFlow(): Flow<List<TaskEntity>>
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteAllTasks()
    fun refreshTasks()
}
