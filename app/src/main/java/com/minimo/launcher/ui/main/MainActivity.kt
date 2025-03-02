package com.minimo.launcher.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.minimo.launcher.ui.navigation.AppNavGraph
import com.minimo.launcher.ui.theme.AppTheme
import com.minimo.launcher.utils.PackageUpdatedReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var packageUpdatedListener: PackageUpdatedReceiver? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPackageUpdatedListener()
        setContent {
            val state by mainViewModel.state.collectAsState()

            AppTheme(themeMode = state.themeMode) {
                AppNavGraph(
                    onBackPressed = {
                        onBackPressedDispatcher.onBackPressed()
                    }
                )
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

    override fun onDestroy() {
        if (packageUpdatedListener != null) {
            unregisterReceiver(packageUpdatedListener)
            packageUpdatedListener = null
        }
        super.onDestroy()
    }
}