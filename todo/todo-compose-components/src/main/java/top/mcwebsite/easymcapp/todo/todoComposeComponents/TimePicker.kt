package top.mcwebsite.easymcapp.todo.todoComposeComponents

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidViewBinding
import top.mcwebsite.easymcapp.todo.todoComposeComponents.databinding.LayoutTimePickBinding

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
    AndroidViewBinding(LayoutTimePickBinding::inflate)
}
