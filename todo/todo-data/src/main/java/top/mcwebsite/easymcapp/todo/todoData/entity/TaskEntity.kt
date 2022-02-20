package top.mcwebsite.easymcapp.todo.todoData.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "content")
    val content: String? = null,
    @ColumnInfo(name = "is_complete")
    val isComplete: Boolean = false,
    @ColumnInfo(name = "priority")
    val priority: PriorityType = PriorityType.NO_PRIORITY,
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate? = null,
    @ColumnInfo(name = "start_time")
    val startTime: LocalTime? = null,
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate? = null,
    @ColumnInfo(name = "end_time")
    val endTime: LocalTime? = null,
) : ToDoEntity
