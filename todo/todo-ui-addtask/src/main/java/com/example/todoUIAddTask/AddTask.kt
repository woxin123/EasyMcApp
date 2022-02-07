package com.example.todoUIAddTask

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import top.mcwebsite.easymcapp.todo.icons.ToDoIcons
import top.mcwebsite.easymcapp.todo.icons.priotity.HighPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.LowPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.MediumPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.NonePriority
import top.mcwebsite.easymcapp.todo.todoComposeComponents.ToDoTextField
import top.mcwebsite.easymcapp.todo.todoComposeComponents.TodoTopBar
import top.mcwebsite.easymcapp.todo.todoComposeComponents.rememberFlowWithLifecycle
import java.time.LocalDate

@Composable
fun AddTask(
    date: LocalDate?,
    navigateUp: () -> Unit,
    openChooseDateTime: () -> Unit,
) {
    AddTask(
        date = date,
        addTaskViewModel = getViewModel(),
        navigateUp = navigateUp,
        openChooseDateTime = openChooseDateTime,
    )
}

@Composable
fun AddTask(
    date: LocalDate?,
    addTaskViewModel: AddTaskViewModel,
    navigateUp: () -> Unit,
    openChooseDateTime: () -> Unit,
) {
    addTaskViewModel.updateDate(date)
    val state by rememberFlowWithLifecycle(addTaskViewModel.state)
        .collectAsState(initial = AddTaskViewState.Empty)
    AddTask(
        state = state,
        navigateUp = navigateUp,
        openChooseDateTime = openChooseDateTime,
        onTitleChanged = { addTaskViewModel.onTitleChanged(it) },
        onContentChanged = { addTaskViewModel.onContentChanged(it) },
        onChooseDateAndRepeatedChanged = { addTaskViewModel.onChooseDateAndRepeatStateChanged(it) },
        onPriorityChanged = { addTaskViewModel.onPriorityChanged(it) },
    )
}

@Composable
fun AddTask(
    state: AddTaskViewState,
    navigateUp: () -> Unit,
    openChooseDateTime: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onChooseDateAndRepeatedChanged: (Boolean) -> Unit,
    onPriorityChanged: (Priority) -> Unit,
) {
    Scaffold(
        topBar = {
            AddTaskTopBar {
                navigateUp()
            }
        }
    ) { padding ->
//        val title = remember {
//            mutableStateOf("")
//        }
//        val content = remember {
//            mutableStateOf("")
//        }
        val expanded = remember {
            mutableStateOf(false)
        }
//        val isChooseDateAndRepeat = remember {
//            mutableStateOf(false)
//        }
//        val priorityIcon = remember {
//            mutableStateOf(priority.last())
//        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isChooseDateAndRepeat,
                    onCheckedChange = {
                        onChooseDateAndRepeatedChanged(it)
                    },
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = state.dateAndRepeat,
                    color = if (state.dateAndRepeat == "日期&重复") {
                        Color.Black
                    } else {
                        Color.Blue
                    },
                    modifier = Modifier
                        .alpha(if (state.isChooseDateAndRepeat) 1.0F else 0.34F)
                        .clickable {
                            if (state.isChooseDateAndRepeat) {
                                openChooseDateTime()
                            }
                        }
                )
                Spacer(modifier = Modifier.weight(1F))
                Box {
                    Icon(
                        painter = rememberVectorPainter(state.priority.icon),
                        contentDescription = null,
                        tint = state.priority.iconColor,
                        modifier = Modifier
                            .width(26.dp)
                            .height(26.dp)
                            .clickable { expanded.value = true }
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        offset = DpOffset(0.dp, (-26).dp)
                    ) {
                        priority.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    expanded.value = false
                                    onPriorityChanged(it)
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = rememberVectorPainter(it.icon),
                                        contentDescription = null,
                                        tint = it.iconColor,
                                        modifier = Modifier
                                            .width(26.dp)
                                            .height(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = it.name)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ToDoTextField(
                value = state.title,
                onValueChange = { change ->
                    onTitleChanged(change)
                },
                textStyle = MaterialTheme.typography.h6,
                placeholder = {
                    Text(
                        text = "What will you do?",
                        style = MaterialTheme.typography.h6,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            )
            ToDoTextField(
                value = state.content,
                onValueChange = { change: String -> onContentChanged(change) },
                textStyle = MaterialTheme.typography.body1,
                placeholder = {
                    Text(text = "Describe")
                },
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Color.White,
            )
        }
    }
}


@Composable
fun AddTaskTopBar(
    onBackClick: () -> Unit,
) {
    TodoTopBar(
        title = {
            Text("InBox")
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = rememberVectorPainter(Icons.Outlined.ArrowBack),
                    contentDescription = null
                )
            }
        }
    )
}

data class Priority(
    val name: String,
    val icon: ImageVector,
    val iconColor: Color,
)

internal val highPriority = Priority("High Priority", ToDoIcons.HighPriority, Color.Red)
internal val mediumPriority =
    Priority("Medium Priority", ToDoIcons.MediumPriority, Color(0xFFFFBB44))
internal val lowPriority = Priority("Low Priority", ToDoIcons.LowPriority, Color(0xFF0888FF))
internal val noPriority = Priority("No Priority", ToDoIcons.NonePriority, Color(0xFF333333))

internal val priority = buildList {
    add(highPriority)
    add(mediumPriority)
    add(lowPriority)
    add(noPriority)
}