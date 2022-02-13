package top.mcwebsite.easymcapp.todo.todoData

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import top.mcwebsite.easymcapp.todo.todoData.dao.TasksDao
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class
    ],
    version = 1,
)
@TypeConverters(ToDoTypeConverters::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}
