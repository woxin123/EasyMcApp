package top.mcwebsite.easymcapp.todo.chooseDateTime

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import top.mcwebsite.easymcapp.todo.todo_compose_components.Calendar
import java.time.LocalDate

@Composable
fun ChooseDateTime(
    navigateUp: () -> Unit,
    navigateUpWithDate: (LocalDate) -> Unit,
) {
    var chooseDate: LocalDate = LocalDate.now()
    Scaffold(
        topBar = {
            ChooseDateTimeTopBar(
                navigateUp = navigateUp,
                navigateUpChooseDate = {
                    navigateUpWithDate(chooseDate)
                },
            )
        }
    ) {
        Calendar(
            onChoose = {
                chooseDate = it
            }
        )
    }
}

@Composable
internal fun ChooseDateTimeTopBar(
    navigateUp: () -> Unit,
    navigateUpChooseDate: () -> Unit,
) {
    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigateUp() }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Close),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Date",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = { navigateUpChooseDate() }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Done),
                    contentDescription = null,
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.MoreVert),
                    contentDescription = null,
                )
            }
        }
    }
}