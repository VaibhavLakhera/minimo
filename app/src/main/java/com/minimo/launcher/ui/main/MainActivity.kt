package com.minimo.launcher.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.minimo.launcher.ui.navigation.AppNavGraph
import com.minimo.launcher.ui.theme.AppTheme
import com.minimo.launcher.utils.PackageUpdatedReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var packageUpdatedListener: PackageUpdatedReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPackageUpdatedListener()
        setContent {
            AppTheme {
                AppNavGraph()
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