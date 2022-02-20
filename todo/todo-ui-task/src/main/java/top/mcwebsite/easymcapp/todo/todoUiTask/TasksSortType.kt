package top.mcwebsite.easymcapp.todo.todoUiTask

sealed class TasksSortType(
    val name: String,
) {
    object TasksCustomSortType : TasksSortType("Custom")
    object TasksTimeSortType : TasksSortType("Time")
    object TasksTitleSortType : TasksSortType("Title")
    object TasksTagSortType : TasksSortType("Tag")
    object TasksPrioritySortType : TasksSortType("Tag")
}
