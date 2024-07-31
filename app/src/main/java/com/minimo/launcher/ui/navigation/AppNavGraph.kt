package com.minimo.launcher.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.minimo.launcher.ui.hidden_apps.HiddenAppsScreen
import com.minimo.launcher.ui.home.HomeScreen
import com.minimo.launcher.ui.intro.IntroScreen
import com.minimo.launcher.ui.launch.LaunchScreen
import com.minimo.launcher.ui.settings.SettingsScreen

object Routes {
    const val LAUNCH = "launch"
    const val INTRO = "intro"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HIDDEN_APPS = "hidden_apps"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LAUNCH,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(route = Routes.LAUNCH) {
            LaunchScreen(
                viewModel = hiltViewModel(it),
                onNavigateToRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(route = Routes.INTRO) {
            IntroScreen(
                viewModel = hiltViewModel(it),
                onIntroCompleted = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Routes.HOME) {
            HomeScreen(
                viewModel = hiltViewModel(it),
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHiddenAppsClick = {
                    navController.navigate(Routes.HIDDEN_APPS)
                }
            )
        }
        composable(route = Routes.HIDDEN_APPS) {
            HiddenAppsScreen(
                viewModel = hiltViewModel(it),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}