package com.example.todoUIAddTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.mcwebsite.easymcapp.todo.todoData.entity.PriorityType
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddTaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val title = MutableStateFlow("")
    private val content = MutableStateFlow("")
    private val isChooseDateAndRepeat = MutableStateFlow(false)
    private val priorityState = MutableStateFlow<Priority>(Priority.NoPriority)
    private val dateAndRepeat = MutableStateFlow("日期&重复")
    private val dateState = MutableStateFlow<LocalDate?>(null)
    private var isInitByTaskId: Boolean = false

    private var taskEntity: TaskEntity = TaskEntity()

    val state = combine(
        title,
        content,
        isChooseDateAndRepeat,
        priorityState,
        dateAndRepeat
    ) { title, content, isChooseDateAndRepeat, priority, dateAndRepeat ->
        AddTaskViewState(isChooseDateAndRepeat, dateAndRepeat, priority, title, content)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AddTaskViewState.Empty,
    )

    init {
        viewModelScope.launch {
            dateState.collect {
                if (it != null) {
                    dateAndRepeat.emit(it.format(DateTimeFormatter.ofPattern("MM-dd")))
                }
            }
        }
    }

    fun initTaskEntity(taskId: Long) {
        viewModelScope.launch {
            if (taskId != -1L && !isInitByTaskId) {
                isInitByTaskId = true
                taskEntity = tasksRepository.getTask(taskId)
                update(taskEntity)
            }
        }
    }

    private fun update(taskEntity: TaskEntity) {
        viewModelScope.launch {
            title.emit(taskEntity.title ?: "")
            content.emit(taskEntity.content ?: "")
            priorityState.emit(taskEntity.priority.convertToPriority())
            dateAndRepeat.emit(
                taskEntity.startDate?.format(DateTimeFormatter.ofPattern("MM-dd")) ?: "日期&重复"
            )
        }
    }

    fun onTitleChanged(change: String) {
        viewModelScope.launch {
            title.emit(change)
        }
    }

    fun onContentChanged(change: String) {
        viewModelScope.launch {
            content.emit(change)
        }
    }

    fun onChooseDateAndRepeatStateChanged(isCheck: Boolean) {
        viewModelScope.launch {
            isChooseDateAndRepeat.emit(isCheck)
        }
    }

    fun onPriorityChanged(priority: Priority) {
        viewModelScope.launch {
            priorityState.emit(priority)
        }
    }

    fun updateDate(date: LocalDate?) {
        viewModelScope.launch {
            dateState.emit(date)
        }
    }

    private fun isEdited(): Boolean {
        if (title.value.isNotEmpty()) {
            return true
        }
        if (content.value.isNotEmpty()) {
            return true
        }
        if (priorityState.value != Priority.NoPriority) {
            return true
        }
        if (dateState.value != null) {
            return true
        }
        return false
    }

    fun back() {
        if (isEdited()) {
            saveTask()
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            tasksRepository.saveOrUpdateTask(
                taskEntity = taskEntity.copy(
                    title = title.value,
                    content = content.value,
                    isComplete = false,
                    startDate = dateState.value,
                    priority = priorityState.value.covertToPriorityType()
                )
            )
        }
    }

    private fun Priority.covertToPriorityType(): PriorityType {
        return when (this) {
            is Priority.HighPriority -> PriorityType.HIGH_PRIORITY
            is Priority.MediumPriority -> PriorityType.MEDIUM_PRIORITY
            is Priority.LowPriority -> PriorityType.LOW_PRIORITY
            is Priority.NoPriority -> PriorityType.NO_PRIORITY
        }
    }

    private fun PriorityType.convertToPriority(): Priority {
        return when (this) {
            PriorityType.HIGH_PRIORITY -> Priority.HighPriority
            PriorityType.MEDIUM_PRIORITY -> Priority.MediumPriority
            PriorityType.LOW_PRIORITY -> Priority.LowPriority
            PriorityType.NO_PRIORITY -> Priority.NoPriority
        }
    }
}
