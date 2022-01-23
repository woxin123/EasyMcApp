package top.mcwebsite.easymcapp.todoApp

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

internal sealed class Screen(val route: String) {
    object Tasks: Screen("tasks")
    object Calender: Screen("calender")
    object Settings: Screen("Settings")
}

private sealed class LeafScreen(
    private val route: String,
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    object Tasks : LeafScreen("tasks")
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
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
        addTasksTopLevel(navController = navController)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addTasksTopLevel(
    navController: NavHostController,
) {
    navigation(
        route = Screen.Tasks.route,
        startDestination = LeafScreen.Tasks.createRoute(Screen.Tasks)
    ) {
        addTasks(navController, Screen.Tasks)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addTasks(
    navController: NavHostController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Tasks.createRoute(root)
    ) {
        Text(text = "hello Tasks")
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
