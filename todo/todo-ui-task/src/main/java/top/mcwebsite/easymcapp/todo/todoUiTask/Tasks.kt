package top.mcwebsite.easymcapp.todo.todoUiTask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.androidx.compose.getViewModel
import top.mcwebsite.easymcapp.todo.todoComposeComponents.TodoTopBar
import top.mcwebsite.easymcapp.todo.todoComposeComponents.rememberFlowWithLifecycle
import top.mcwebsite.easymcapp.todo.todoData.entity.TaskEntity
import java.time.format.DateTimeFormatter

@Composable
fun Tasks(
    openAddTask: () -> Unit,
    openEditTask: (Long) -> Unit,
) {
    Tasks(
        tasksViewModel = getViewModel(),
        openAddTask = openAddTask,
        openEditTask = openEditTask,
    )
}

@Composable
internal fun Tasks(
    tasksViewModel: TasksViewModel,
    openAddTask: () -> Unit,
    openEditTask: (Long) -> Unit,
) {
    val state by rememberFlowWithLifecycle(tasksViewModel.state).collectAsState(TasksViewState.Empty)
    Tasks(
        state = state,
        openAddTask = openAddTask,
        openEditTask = openEditTask,
        completeTask = { tasksViewModel.completeTask(it) },
        activeTask = { tasksViewModel.activeTask(it) },
    )
}

@Composable
internal fun Tasks(
    state: TasksViewState,
    openAddTask: () -> Unit,
    openEditTask: (Long) -> Unit,
    completeTask: (TaskEntity) -> Unit,
    activeTask: (TaskEntity) -> Unit,
) {
    val isShowSelectSortTypeDialog = remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            TaskTopBar(
                showSelectSortTypeDialog = {
                    isShowSelectSortTypeDialog.value = true
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openAddTask()
                },
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Outlined.Add),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                )
            }
        },
        isFloatingActionButtonDocked = false,
        modifier = Modifier.fillMaxWidth()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(color = MaterialTheme.colors.primary)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(state.taskGroups.size) {
                TaskGroup(
                    taskGroup = state.taskGroups[it],
                    openEditTask = openEditTask,
                    completeTask = completeTask,
                    activeTask = activeTask,
                )
            }
        }
        if (isShowSelectSortTypeDialog.value) {
            TasksSortDialog(
                onDismissDialog = {
                    isShowSelectSortTypeDialog.value = false
                }
            )
        }
    }
}

@Composable
internal fun TaskTopBar(
    modifier: Modifier = Modifier,
    showSelectSortTypeDialog: () -> Unit,
) {
    TodoTopBar(
        modifier = modifier,
        title = {
            Text(text = "InBox")
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = rememberVectorPainter(Icons.Outlined.Menu),
                    contentDescription = null,
                )
            }
        },
        actions = {
            var expendedState by remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { expendedState = true }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.MoreVert),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = expendedState,
                onDismissRequest = { expendedState = false },
                offset = DpOffset(0.dp, (-56).dp)
            ) {
                DropdownMenuItem(onClick = {
                    showSelectSortTypeDialog()
                    expendedState = false
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Sort")
                    }
                }
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Select")
                    }
                }
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Show Complete")
                    }
                }
            }
        }
    )
}

@Composable
internal fun TaskGroup(
    taskGroup: TaskGroup,
    openEditTask: (Long) -> Unit,
    completeTask: (TaskEntity) -> Unit,
    activeTask: (TaskEntity) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = taskGroup.groupName)
            Spacer(modifier = Modifier.height(5.dp))
            if (taskGroup.expended) {
                taskGroup.tasks.forEach {
                    Task(
                        taskEntity = it,
                        openEditTask = openEditTask,
                        completeTask = completeTask,
                        activeTask = activeTask,
                    )
                }
            }
        }
    }
}

@Composable
internal fun Task(
    taskEntity: TaskEntity,
    openEditTask: (Long) -> Unit,
    completeTask: (TaskEntity) -> Unit,
    activeTask: (TaskEntity) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { openEditTask(taskEntity.id) }
    ) {
        Checkbox(
            checked = taskEntity.isComplete,
            modifier = Modifier
                .width(30.dp)
                .height(30.dp),
            onCheckedChange = {
                if (it) {
                    completeTask(taskEntity)
                } else {
                    activeTask(taskEntity)
                }
            },
        )
        Text(text = taskEntity.title ?: "")
        Spacer(modifier = Modifier.weight(1F))
        if (taskEntity.startDate != null) {
            Text(
                text = taskEntity.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                color = Color.Red,
                fontSize = 12.sp,
            )
        }
    }
}

internal val tasksSortTypes = listOf(
    TasksSortType.TasksCustomSortType,
    TasksSortType.TasksTitleSortType,
    TasksSortType.TasksTimeSortType,
    TasksSortType.TasksTagSortType,
)

@Composable
fun TasksSortDialog(
    onDismissDialog: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissDialog() }) {
        val selectedSortType = remember {
            mutableStateOf<TasksSortType>(TasksSortType.TasksCustomSortType)
        }
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "sort",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )
            tasksSortTypes.forEach { sort ->
                Row(

                ) {
                    Text(text = sort.name)
                    Spacer(modifier = Modifier.weight(1F))
                    RadioButton(
                        selected = sort == selectedSortType.value,
                        onClick = {
                            selectedSortType.value = sort
                        }
                    )
                }
            }
        }
    }
}
