package top.mcwebsite.easymcapp.todo.todoData

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ToDoTypeConverters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?) = date?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?) = value?.let { LocalDate.parse(value) }

    @TypeConverter
    @JvmStatic
    fun fromLocalTime(time: LocalTime?) = time?.format(DateTimeFormatter.ISO_LOCAL_TIME)

    @TypeConverter
    @JvmStatic
    fun toLocalTime(value: String?) = value?.let { LocalTime.parse(value) }
}
