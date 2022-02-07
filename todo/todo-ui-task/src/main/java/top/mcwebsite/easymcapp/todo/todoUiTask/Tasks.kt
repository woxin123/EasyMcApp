package top.mcwebsite.easymcapp.todo.todoUiTask

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import top.mcwebsite.easymcapp.todo.todoComposeComponents.TodoTopBar

@Composable
fun Tasks(
    openAddTask: () -> Unit,
) {
    Tasks(
        tasksViewModel = getViewModel(),
        openAddTask = openAddTask,
    )
}

@Composable
internal fun Tasks(
    tasksViewModel: TasksViewModel,
    openAddTask: () -> Unit
) {
    Scaffold(
        topBar = {
            TaskTopBar()
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
    ) {
        LazyColumn() {}
    }
}

@Composable
internal fun TaskTopBar(
    modifier: Modifier = Modifier,
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
        }
    )
}
