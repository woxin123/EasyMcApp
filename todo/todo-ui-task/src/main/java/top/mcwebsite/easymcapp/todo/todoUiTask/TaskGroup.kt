package top.mcwebsite.easymcapp.todo.todoUiTask

import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

data class TaskGroup(
    val groupName: String,
    val expended: Boolean = true,
    val tasks: List<TaskEntity>,
)
