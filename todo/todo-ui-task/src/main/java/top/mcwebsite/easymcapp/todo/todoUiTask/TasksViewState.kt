package top.mcwebsite.easymcapp.todo.todoUiTask

import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

data class TasksViewState(
    val tasks: List<TaskEntity> = emptyList(),
    val test: String? = null,
) {
    companion object {
        val Empty = TasksViewState()
    }
}
