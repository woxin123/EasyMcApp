package top.mcwebsite.easymcapp.todoApp

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.todoUIAddTask.AddTask
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import top.mcwebsite.easymcapp.todo.chooseDateTime.ChooseDateTime
import top.mcwebsite.easymcapp.todo.todoComposeComponents.Calendar
import top.mcwebsite.easymcapp.todo.todo_ui_task.Tasks
import java.time.LocalDate

internal sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")
    object Calender : Screen("calender")
    object Settings : Screen("Settings")
}

private sealed class LeafScreen(
    private val route: String,
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    object Tasks : LeafScreen("tasks")
    object Calendar : LeafScreen("calender")
    object AddTask : LeafScreen("addTask") {
        fun createRoute(root: Screen, date: LocalDate?): String {
            return "${root.route}/addTask".let {
                if (date != null) {
                    "$it?date=$date"
                } else {
                    it
                }
            }
        }
    }

    object ChooseDateTime : LeafScreen("choose_date_time")
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Tasks.route,
        enterTransition = { defaultEasyMcAppToDoEnterTransition(initialState, targetState) },
        exitTransition = { defaultEasyMcAppToDoExitTransition(initialState, targetState) },
        popEnterTransition = { defaultEasyMcAppToDoPopEnterTransition() },
        popExitTransition = { defaultEasyMcAppToDoPopExitTransition() },
        modifier = modifier,
    ) {
        addTasksTopLevel(navController, bottomNavigationState)
        addCalendarTopLevel(navController, bottomNavigationState)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addTasksTopLevel(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
) {
    navigation(
        route = Screen.Tasks.route,
        startDestination = LeafScreen.Tasks.createRoute(Screen.Tasks)
    ) {
        addTasks(navController, bottomNavigationState, Screen.Tasks)
        addAddTask(navController, bottomNavigationState, Screen.Tasks)
        addChooseDateTime(navController, bottomNavigationState, Screen.Tasks)
    }
}

private fun NavGraphBuilder.addCalendarTopLevel(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
) {
    navigation(
        route = Screen.Calender.route,
        startDestination = LeafScreen.Calendar.createRoute(Screen.Calender)
    ) {
       addCalendar(navController, bottomNavigationState, Screen.Calender)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addTasks(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
    root: Screen,
) {
    composable(
        route = LeafScreen.Tasks.createRoute(root),
    ) {
        bottomNavigationState.value = true
        Tasks(
            openAddTask = {
                navController.navigate(LeafScreen.AddTask.createRoute(root))
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addCalendar(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
    root: Screen,
) {
    composable(
        route = LeafScreen.Calendar.createRoute(root),
    ) {
        bottomNavigationState.value = true
        Calendar()
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addAddTask(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
    root: Screen,
) {
    composable(
        route = LeafScreen.AddTask.createRoute(root),
        arguments = listOf(
            navArgument("date") {
                type = NavType.SerializableType(LocalDate::class.java)
                nullable = true
            }
        )
    ) {
        bottomNavigationState.value = false
        val date = it.savedStateHandle.get<LocalDate>("date")
        AddTask(
            date = date,
            navigateUp = navController::navigateUp,
            openChooseDateTime = {
                navController.navigate(LeafScreen.ChooseDateTime.createRoute(root))
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addChooseDateTime(
    navController: NavHostController,
    bottomNavigationState: MutableState<Boolean>,
    root: Screen,
) {
    composable(
        route = LeafScreen.ChooseDateTime.createRoute(root)
    ) {
        bottomNavigationState.value = false
        ChooseDateTime(
            navigateUp = {
                navController.navigateUp()
            },
            navigateUpWithDate = {

                navController.previousBackStackEntry?.savedStateHandle?.set("date", it)
                navController.navigateUp()
            }
        )
    }
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultEasyMcAppToDoEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph

    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultEasyMcAppToDoExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph

    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.Start)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultEasyMcAppToDoPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End)
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultEasyMcAppToDoPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End)
}
