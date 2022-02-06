package top.mcwebsite.easymcapp.todo.todo_compose_components

import android.util.AttributeSet
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import top.mcwebsite.easymcapp.todo.todo_compose_components.databinding.LayoutTimePickBinding

@Composable
fun TimePicker(

) {
//    ConstraintLayout(
//        modifier = Modifier
//            .padding(start = 30.dp)
//            .width(300.dp)
//            .background(shape = RoundedCornerShape(2.dp), color = Color.White)
//    ) {
//        AndroidView(factory = { ctx ->
//            TimePicker(ctx, ).apply {
//            }
//        })
//    }
    AndroidViewBinding(LayoutTimePickBinding::inflate) {

    }
}