package top.mcwebsite.easymcapp.todo.todoUiTask.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.mcwebsite.easymcapp.todo.todoUiTask.TasksViewModel

val tasksModule = module {
    viewModel {
        TasksViewModel(get())
    }
}
