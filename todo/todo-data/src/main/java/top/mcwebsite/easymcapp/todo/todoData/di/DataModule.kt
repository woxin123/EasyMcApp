package top.mcwebsite.easymcapp.todo.todoData.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.bind
import org.koin.dsl.module
import top.mcwebsite.easymcapp.todo.todoData.ToDoDatabase
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksDateSource
import top.mcwebsite.easymcapp.todo.todoData.repository.task.TasksRepository
import top.mcwebsite.easymcapp.todo.todoData.repository.task.local.TasksLocalDataSource

val dataModule = module {
    single {
        Room.databaseBuilder(androidApplication(), ToDoDatabase::class.java, "todo.db")
            .build()
    } bind ToDoDatabase::class

    single {
        get<ToDoDatabase>().tasksDao()
    }

    single {
        TasksLocalDataSource(get())
    } bind TasksDateSource::class

    single {
        TasksRepository(get())
    } bind TasksRepository::class
}
