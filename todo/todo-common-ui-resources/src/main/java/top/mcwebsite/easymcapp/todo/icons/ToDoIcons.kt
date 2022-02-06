package top.mcwebsite.easymcapp.todo.icons

import androidx.compose.ui.graphics.vector.ImageVector
import top.mcwebsite.easymcapp.todo.icons.priotity.HighPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.LowPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.MediumPriority
import top.mcwebsite.easymcapp.todo.icons.priotity.NonePriority

object ToDoIcons

private var __AllAssets: List<ImageVector>? = null

val ToDoIcons.AllAssets: List<ImageVector>
    get() {
        if (__AllAssets != null) {
            return __AllAssets!!
        }
        __AllAssets= listOf(MediumPriority, LowPriority, NonePriority, HighPriority)
        return __AllAssets!!
    }
