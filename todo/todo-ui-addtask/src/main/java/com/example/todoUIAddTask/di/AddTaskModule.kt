package com.example.todoUIAddTask.di

import com.example.todoUIAddTask.AddTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksRepository

val addTasksModule = module {
    viewModel {
        AddTaskViewModel(get())
    }
}
