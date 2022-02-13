package top.mcwebsite.easymcapp.todo.todoData.repository.task.local

import top.mcwebsite.easymcapp.todo.todoData.dao.TasksDao
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksDateSource

class TasksLocalDataSource(
    private val tasksDao: TasksDao
) : TasksDateSource {
    override suspend fun saveTask(taskEntity: TaskEntity) {
        tasksDao.insert(taskEntity)
    }

    override suspend fun getTask(taskId: Long): TaskEntity {
        return tasksDao.queryById(taskId)
    }

    override suspend fun getTasks(): List<TaskEntity> {
        return tasksDao.queryAll()
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksDao.deleteEntity(task)
    }

    override suspend fun deleteAllTasks() {
        tasksDao.deleteAll()
    }

    override fun refreshTasks() {
        // no op
    }
}
