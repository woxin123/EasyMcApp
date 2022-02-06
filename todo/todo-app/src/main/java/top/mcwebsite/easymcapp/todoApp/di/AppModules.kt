package top.mcwebsite.easymcapp.todoApp.di

import com.example.todoUIAddTask.di.addTasksModule
import org.koin.dsl.bind
import org.koin.dsl.module
import top.mcwebsite.common.android.appinitializer.AppInitializer
import top.mcwebsite.easymcapp.todo.todo_ui_task.di.tasksModule
import top.mcwebsite.easymcapp.todoApp.appinitializers.AppInitializerTest
import top.mcwebsite.easymcapp.todoApp.appinitializers.AppInitializers

val appModule = module {

    factory { AppInitializerTest() } bind AppInitializer::class

    factory {
        // issue https://github.com/InsertKoinIO/koin/issues/1146
        AppInitializers(getAll<AppInitializer>().distinctBy { it.javaClass }.toSet())
    }
} + tasksModule + addTasksModule
