package top.mcwebsite.easymcapp.todo.todoUiTask

data class TasksViewState(
    val taskGroups: List<TaskGroup> = emptyList(),
    val test: String? = null,
) {
    companion object {
        val Empty = TasksViewState()
    }
}
