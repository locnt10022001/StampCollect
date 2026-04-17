package com.stampcollect.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stampcollect.ui.screens.CollectionDetailScreen
import com.stampcollect.ui.screens.DayStampsScreen
import com.stampcollect.ui.screens.MainScreen
import com.stampcollect.ui.screens.StampDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = "main",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable("main") {
            MainScreen(
                onCollectionClick = { id, name ->
                    navController.navigate("detail/$id/$name")
                },
                onDayClick = { timestamp ->
                    navController.navigate("day_details/$timestamp")
                },
                onStampClick = { id ->
                    navController.navigate("stamp_detail/$id")
                }
            )
        }
        composable(
            "detail/{collectionId}/{collectionName}",
            arguments = listOf(
                navArgument("collectionId") { type = NavType.IntType },
                navArgument("collectionName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("collectionId") ?: 0
            val name = backStackEntry.arguments?.getString("collectionName") ?: ""
            CollectionDetailScreen(
                collectionId = id,
                collectionName = name,
                onBackClick = { navController.popBackStack() },
                onAddStampClick = { navController.popBackStack() },
                onStampClick = { id -> navController.navigate("stamp_detail/$id") }
            )
        }
        composable(
            "day_details/{timestamp}",
            arguments = listOf(navArgument("timestamp") { type = NavType.LongType })
        ) { backStackEntry ->
            val ts = backStackEntry.arguments?.getLong("timestamp") ?: 0L
            DayStampsScreen(
                timestamp = ts,
                onBackClick = { navController.popBackStack() },
                onStampClick = { id -> navController.navigate("stamp_detail/$id") }
            )
        }
        composable(
            "stamp_detail/{stampId}",
            arguments = listOf(navArgument("stampId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("stampId") ?: 0
            StampDetailScreen(
                stampId = id,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
