package com.example.todoUIAddTask.di

import com.example.todoUIAddTask.AddTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addTasksModule = module {
    viewModel {
        AddTaskViewModel()
    }
}