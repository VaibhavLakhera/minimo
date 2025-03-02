package com.minimo.launcher.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.minimo.launcher.ui.favourite_apps.FavouriteAppsScreen
import com.minimo.launcher.ui.hidden_apps.HiddenAppsScreen
import com.minimo.launcher.ui.home.HomeScreen
import com.minimo.launcher.ui.intro.IntroScreen
import com.minimo.launcher.ui.launch.LaunchScreen
import com.minimo.launcher.ui.settings.SettingsScreen
import com.minimo.launcher.ui.settings.appearance.AppearanceScreen

object Routes {
    const val LAUNCH = "launch"
    const val INTRO = "intro"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val SETTINGS_APPEARANCE = "settings_appearance"
    const val HIDDEN_APPS = "hidden_apps"
    const val FAVOURITE_APPS = "favourite_apps"
}

@Composable
fun AppNavGraph(onBackPressed: () -> Unit) {
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
                },
                onAddFavouriteAppsClick = {
                    navController.navigate(Routes.FAVOURITE_APPS)
                }
            )
        }
        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = onBackPressed,
                onHiddenAppsClick = {
                    navController.navigate(Routes.HIDDEN_APPS)
                },
                onAppearanceClick = {
                    navController.navigate(Routes.SETTINGS_APPEARANCE)
                },
                onFavouriteAppsClick = {
                    navController.navigate(Routes.FAVOURITE_APPS)
                }
            )
        }
        composable(route = Routes.HIDDEN_APPS) {
            HiddenAppsScreen(
                viewModel = hiltViewModel(it),
                onBackClick = onBackPressed
            )
        }
        composable(route = Routes.SETTINGS_APPEARANCE) {
            AppearanceScreen(
                viewModel = hiltViewModel(it),
                onBackClick = onBackPressed
            )
        }
        composable(route = Routes.FAVOURITE_APPS) {
            FavouriteAppsScreen(
                viewModel = hiltViewModel(it),
                onBackClick = onBackPressed
            )
        }
    }
}