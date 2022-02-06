package top.mcwebsite.easymcapp.todo.todo_ui_task.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.easymcapp.todo.todo_ui_task.TasksViewModel

val tasksModule = module {
    viewModel {
        TasksViewModel()
    }
}