package com.example.todoUIAddTask

data class AddTaskViewState(
    val isChooseDateAndRepeat: Boolean = false,
    val dateAndRepeat: String = "",
    val priority: Priority = noPriority,
    val title: String = "",
    val content: String = "",
) {
    companion object {
        val Empty = AddTaskViewState()
    }
}