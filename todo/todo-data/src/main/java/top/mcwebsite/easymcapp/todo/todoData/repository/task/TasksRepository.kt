package top.mcwebsite.easymcapp.todo.todoData.repository.task

import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

class TasksRepository(
    private val tasksDateSource: TasksDateSource,
) : TasksDateSource {

    override suspend fun saveTask(taskEntity: TaskEntity) {
        tasksDateSource.saveTask(taskEntity)
    }

    override suspend fun getTask(taskId: Long): TaskEntity {
        return tasksDateSource.getTask(taskId)
    }

    override suspend fun getTasks(): List<TaskEntity> {
        return tasksDateSource.getTasks()
    }

    override suspend fun deleteTask(task: TaskEntity) {
        return tasksDateSource.deleteTask(task)
    }

    override suspend fun deleteAllTasks() {
        return tasksDateSource.deleteAllTasks()
    }

    override fun refreshTasks() {
        // no op
    }
}
