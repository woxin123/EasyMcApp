package top.mcwebsite.easymcapp.todo.todoUiTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksRepository

class TasksViewModel(
    private val tasksRepository: TasksRepository,
) : ViewModel() {
    private val tasksState = MutableStateFlow<List<TaskEntity>>(emptyList())
    private val uiMessage = MutableStateFlow<String>("")

    val state = combine(tasksState, uiMessage) { tasks, message ->
        TasksViewState(tasks, message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksViewState.Empty
    )

    init {
        viewModelScope.launch {
            tasksState.emit(tasksRepository.getTasks())
        }
    }
}
