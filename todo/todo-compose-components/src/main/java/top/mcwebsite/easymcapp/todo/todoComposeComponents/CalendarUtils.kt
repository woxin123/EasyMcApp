package top.mcwebsite.easymcapp.todo.todoComposeComponents

import java.time.LocalDate

class CalendarUtils(val minDate: LocalDate, val maxDate: LocalDate) {
    fun getMonthForPosition(position: Int): Int {
        return (position + minDate.month.ordinal) % 12
    }

    fun getYearForPosition(position: Int): Int {
        val yearOffset = (position + minDate.month.ordinal) / 12
        return yearOffset + minDate.year
    }
}
