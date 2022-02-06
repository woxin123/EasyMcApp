package top.mcwebsite.easymcapp.todo.todo_compose_components.utils

import java.time.LocalDate

fun commonDays(year: Int): MutableMap<LocalDate, String> {
    return mutableMapOf<LocalDate, String>().apply {
        newYearFestival(year).apply { put(second, first) }
        lunarNewYearsEva(year).apply { put(second, first) }
        lunarNewYearFestival(year).apply { put(second, first) }
        valentineDay(year).apply { put(second, first) }
        womanDay(year).apply { put(second, first) }
        arborDay(year).apply { put(second, first) }
        consumersDay(year).apply { put(second, first) }
        aprilFoolsDay(year).apply { put(second, first) }
        qingmingFestival(year).apply { put(second, first) }
        workersDay(year).apply { put(second, first) }
        youthDay(year).apply { put(second, first) }
        childrenDay(year).apply { put(second, first) }
        dragonBoatFestival(year).apply { put(second, first) }
        cPCFoundingDay(year).apply { put(second, first) }
        armyDay(year).apply { put(second, first) }
        qiXiFestival(year).apply { put(second, first) }
        midAutumnFestival(year).apply { put(second, first) }
        teachersDay(year).apply { put(second, first) }
        nationalDay(year).apply { put(second, first) }
        doubleNinthDay(year).apply { put(second, first) }
        allSaintsDay(year).apply { put(second, first) }
        thanksgivingDay(year).apply { put(second, first) }
        christmasEve(year).apply { put(second, first) }
        christmasDay(year).apply { put(second, first) }
    }
}

fun newYearFestival(year: Int): Pair<String, LocalDate> {
    return "元旦" to LocalDate.of(year, 1, 1)
}

fun lunarNewYearFestival(year: Int): Pair<String, LocalDate> {
    val lunarNewYear = LunarCalendar.lunarToSolar(year, 1, 1, false)
    return "春节" to LocalDate.of(lunarNewYear[0], lunarNewYear[1], lunarNewYear[2])
}

fun lunarNewYearsEva(year: Int): Pair<String, LocalDate> {
    val lunarNewYear = LunarCalendar.lunarToSolar(year, 1, 1, false)
    val lunarNewYearDate = LocalDate.of(lunarNewYear[0], lunarNewYear[1], lunarNewYear[2])
    return "除夕" to lunarNewYearDate.minusDays(1)
}

fun valentineDay(year: Int): Pair<String, LocalDate> {
    return "情人节" to LocalDate.of(year, 2, 14)
}

fun womanDay(year: Int): Pair<String, LocalDate> {
    return "妇女节" to LocalDate.of(year, 3, 8)
}

fun arborDay(year: Int): Pair<String, LocalDate> {
    return "植树节" to LocalDate.of(year, 3, 12)
}

fun consumersDay(year: Int): Pair<String, LocalDate> {
    return "消费者日" to LocalDate.of(year, 3, 15)
}

fun aprilFoolsDay(year: Int): Pair<String, LocalDate> {
    return "愚人节" to LocalDate.of(year, 4, 1)
}

/**
 * 计算范围 1700~3100
 */
fun calculateQingming(year: Int): Int {
    if (year == 2232) {
        return 4;
    }
    val coefficient = doubleArrayOf(5.15, 5.37, 5.59, 4.82, 5.02, 5.26, 5.48, 4.70, 4.92, 5.135, 5.36, 4.60, 4.81, 5.04, 5.26)
    val mod = year % 100
    return (mod * 0.2422 + coefficient[year / 100 - 17] - mod / 4).toInt()
}

fun qingmingFestival(year: Int): Pair<String, LocalDate> {
    val day = calculateQingming(year)
    return "清明节" to LocalDate.of(year, 4, day)
}

fun workersDay(year: Int): Pair<String, LocalDate> {
    return "劳动节" to LocalDate.of(year, 5, 1)
}

fun youthDay(year: Int): Pair<String, LocalDate> {
    return "青年节" to LocalDate.of(year, 5, 4)
}

fun childrenDay(year: Int): Pair<String, LocalDate> {
    return "儿童节" to LocalDate.of(year, 6, 1)
}

fun dragonBoatFestival(year: Int): Pair<String, LocalDate> {
    val dragonBoatFestival = LunarCalendar.lunarToSolar(year, 5, 5, false)
    return "端午节" to LocalDate.of(dragonBoatFestival[0], dragonBoatFestival[1], dragonBoatFestival[2])
}

fun cPCFoundingDay(year: Int): Pair<String, LocalDate> {
    return  "建党节" to LocalDate.of(year, 7, 1)
}

fun armyDay(year: Int): Pair<String, LocalDate> {
    return "建军节" to LocalDate.of(year, 8, 1)
}

fun qiXiFestival(year: Int): Pair<String, LocalDate> {
    val qiXiFestival = LunarCalendar.lunarToSolar(year, 7, 7, false)
    return "七夕" to LocalDate.of(qiXiFestival[0], qiXiFestival[1], qiXiFestival[2])
}

fun midAutumnFestival(year: Int): Pair<String, LocalDate> {
    val midAutumnFestival = LunarCalendar.lunarToSolar(year, 8, 15, false)
    return "中秋节" to LocalDate.of(midAutumnFestival[0], midAutumnFestival[1], midAutumnFestival[2])
}

fun teachersDay(year: Int): Pair<String, LocalDate> {
    return "教师节" to LocalDate.of(year, 9, 10)
}

fun nationalDay(year: Int): Pair<String, LocalDate> {
    return "国庆节" to LocalDate.of(year, 10, 1)
}

fun doubleNinthDay(year: Int): Pair<String, LocalDate> {
    val doubleNinthDay = LunarCalendar.lunarToSolar(year, 9, 9, false)
    return "重阳节" to LocalDate.of(doubleNinthDay[0], doubleNinthDay[1], doubleNinthDay[2])
}

fun allSaintsDay(year: Int): Pair<String, LocalDate> {
    return "万圣节" to LocalDate.of(year, 11, 1)
}

fun thanksgivingDay(year: Int): Pair<String, LocalDate> {
    return "感恩节" to LocalDate.of(year, 11, 24)
}

fun christmasEve(year: Int): Pair<String, LocalDate> {
    return "平安夜" to LocalDate.of(year, 12, 24)
}

fun christmasDay(year: Int): Pair<String, LocalDate> {
    return "圣诞节" to LocalDate.of(year, 12, 25)
}