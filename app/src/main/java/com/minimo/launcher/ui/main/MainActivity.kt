package com.minimo.launcher.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.minimo.launcher.ui.navigation.AppNavGraph
import com.minimo.launcher.ui.navigation.Routes
import com.minimo.launcher.ui.theme.AppTheme
import com.minimo.launcher.utils.HomePressedNotifier
import com.minimo.launcher.utils.PackageUpdatedReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var packageUpdatedListener: PackageUpdatedReceiver? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setupPackageUpdatedListener()
        setContent {
            val navController = rememberNavController()
            val state by viewModel.state.collectAsState()
            val safeDrawingTop =
                WindowInsets.statusBars.union(WindowInsets.ime).union(WindowInsets.displayCutout)

            ObserveNavigationEvents(
                navController = navController,
                homePressedNotifier = viewModel.getHomePressedNotifier()
            )

            Box(modifier = Modifier.windowInsetsPadding(safeDrawingTop)) {
                AppTheme(
                    themeMode = state.themeMode,
                    statusBarVisible = state.statusBarVisible,
                    useDynamicTheme = state.useDynamicTheme,
                    blackTheme = state.blackTheme
                ) {
                    AppNavGraph(
                        navController = navController,
                        onBackPressed = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            }
        }
    }

    private fun setupPackageUpdatedListener() {
        packageUpdatedListener = PackageUpdatedReceiver().also { receiver ->
            ContextCompat.registerReceiver(
                this, receiver, receiver.getFilter(), ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.onHomeButtonPressed()
        }
    }

    override fun onDestroy() {
        if (packageUpdatedListener != null) {
            unregisterReceiver(packageUpdatedListener)
            packageUpdatedListener = null
        }
        super.onDestroy()
    }
}

@Composable
private fun ObserveNavigationEvents(
    navController: NavHostController,
    homePressedNotifier: HomePressedNotifier
) {
    LaunchedEffect(
        key1 = navController,
        key2 = homePressedNotifier
    ) {
        homePressedNotifier.homePressedEvent.collect {
            if (navController.currentDestination?.route != Routes.HOME) {
                navController.popBackStack(Routes.HOME, inclusive = false)
            }
        }
    }
}