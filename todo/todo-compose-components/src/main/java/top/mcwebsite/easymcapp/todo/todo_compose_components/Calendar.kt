package top.mcwebsite.easymcapp.todo.todo_compose_components

import android.icu.util.LocaleData
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import top.mcwebsite.easymcapp.todo.todo_compose_components.utils.commonDays
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*

private val weekDaysName = arrayOf("日", "一", "二", "三", "四", "五", "六")

internal val DEFAULT_MIN_DATE = LocalDate.of(1970, 1, 1)
internal val DEFAULT_MAX_DATE = LocalDate.of(2050, 1, 1)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Calendar(
    currentDate: LocalDate = LocalDate.now(),
    holiday: Map<LocalDate, String> = commonDays(currentDate.year),
    minDate: LocalDate = DEFAULT_MIN_DATE,
    maxDate: LocalDate = DEFAULT_MAX_DATE,
    currentDayColor: Color = Color.Blue,
    chooseDayColor: Color = Color.Red,
    modifier: Modifier = Modifier,
    onChoose: (LocalDate) -> Unit = {},
) {
    val pageState = rememberPagerState()
    var rememberChooseDate by remember {
        mutableStateOf<LocalDate?>(null)
    }
    val calendarUtils = CalendarUtils(minDate, maxDate)
    val count = calculateCount(minDate, maxDate)
    LaunchedEffect(null) {
        pageState.scrollToPage(getPositionForDay(currentDate, minDate, maxDate))
    }
    HorizontalPager(
        count = count,
        state = pageState,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) { index ->
        Month(
            year = calendarUtils.getYearForPosition(index),
            month = Month.of(calendarUtils.getMonthForPosition(index) + 1),
            holiday,
            rememberChooseDate,
            currentDayColor,
            chooseDayColor,
        ) { month, day ->
            val chooseDate = LocalDate.of(currentDate.year, month.value, day)
            rememberChooseDate = chooseDate
            onChoose(chooseDate)
        }
    }
}

internal fun calculateCount(minDate: LocalDate, maxDate: LocalDate): Int {
    val diffYear = maxDate.year - minDate.year
    val diffMonth = maxDate.month.value - minDate.month.value
    return diffMonth + diffYear * 12 + 1
}

internal fun getPositionForDay(date: LocalDate, minDate: LocalDate, maxDate: LocalDate): Int {
    val yearOffset = date.year - minDate.year
    val monthOffset = date.month.value - minDate.month.value
    return yearOffset * 12 + monthOffset
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Month(
    year: Int = LocalDate.now().year,
    month: Month,
    holiday: Map<LocalDate, String>,
    chooseDate: LocalDate?,
    currentDayColor: Color = Color.Blue,
    chooseDayColor: Color = Color.Red,
    onChoose: (Month, Int) -> Unit,
) {
    val calendar = Calendar.getInstance().apply {
        set(year, month.ordinal, 1)
    }
    val firstOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = LocalDate.now().month == month
    val currentDay = LocalDate.now().dayOfMonth
    val isChooseMonth = chooseDate?.month == month
    val chooseDay = chooseDate?.dayOfMonth
    BoxWithConstraints {
        val height = maxWidth / 7
        Column(
            modifier = Modifier
        ) {
            Log.d("mengchen", month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
            Text(
                text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                color = Color.Gray,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 16.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(weekDaysName.size) {
                    Text(
                        text = weekDaysName[it],
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            LazyVerticalGrid(
                cells = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(firstOfWeek + maxDaysOfMonth - 1) {
                    if (it < firstOfWeek - 1) {
                        Spacer(
                            modifier = Modifier
                                .height(height)
                        )
                    } else {
                        val day = it - firstOfWeek + 2
                        val isCurrentDay = isCurrentMonth && day == currentDay
                        val isChooseDay = isChooseMonth && day == chooseDay
                        val holidayName = holiday[LocalDate.of(year, month.value, day)]
                        Day(
                            day = day,
                            holidayName = holidayName,
                            isCurrentDay = isCurrentDay,
                            isChooseDay = isChooseDay,
                            currentDayColor = currentDayColor,
                            chooseDayColor = chooseDayColor,
                            modifier = Modifier.height(height)
                        ) {
                            onChoose(month, it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Day(
    day: Int,
    holidayName: String?,
    isCurrentDay: Boolean = false,
    isChooseDay: Boolean = false,
    currentDayColor: Color = Color.Blue,
    chooseDayColor: Color = Color.Red,
    modifier: Modifier,
    onDayClick: (Int) -> Unit,
) {
    val color = when {
        isChooseDay -> Color.White
        isCurrentDay -> Color.Blue
        else -> Color.Black
    }
    ConstraintLayout(
        modifier = modifier
            .clickable {
                onDayClick.invoke(day)
            }
    ) {
        val (chooseBack, dayText, holidayText) = createRefs()
        createVerticalChain(dayText, holidayText, chainStyle = ChainStyle.Packed)
        if (isChooseDay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .fillMaxHeight(0.8F)
                    .background(color = chooseDayColor, shape = CircleShape)
                    .constrainAs(chooseBack) {
                        centerTo(parent)
                    }
            )
        }
        Text(
            text = day.toString(),
            color = color,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(dayText) {
                    if (!holidayName.isNullOrBlank() && !isChooseDay) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                        bottom.linkTo(holidayText.top)

                    } else {
                        centerTo(parent)
                    }
                }

        )
        if (!holidayName.isNullOrBlank() && !isChooseDay) {
            Text(
                text = holidayName,
                textAlign = TextAlign.Center,
                color = Color.Green,
                fontSize = 10.sp,
                modifier = Modifier
                    .constrainAs(holidayText) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(dayText.bottom)
                        centerHorizontallyTo(parent)
                    }
            )
        }
    }
}
