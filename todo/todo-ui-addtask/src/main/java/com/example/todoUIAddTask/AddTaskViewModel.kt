package com.example.todoUIAddTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun isEdited(): Boolean {
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

    fun saveTask() {
        viewModelScope.launch {
            tasksRepository.saveTask(
                TaskEntity(
                    title = title.value,
                    content = content.value,
                    startDate = dateState.value,
                    priority = priorityState.value.covertToPriorityType()
                )
            )
        }
    }

    private fun Priority.covertToPriorityType(): PriorityType {
        return when(this) {
            is Priority.HighPriority -> PriorityType.HIGH_PRIORITY
            is Priority.MediumPriority -> PriorityType.MEDIUM_PRIORITY
            is Priority.LowPriority -> PriorityType.LOW_PRIORITY
            is Priority.NoPriority -> PriorityType.NO_PRIORITY
        }
    }
}
