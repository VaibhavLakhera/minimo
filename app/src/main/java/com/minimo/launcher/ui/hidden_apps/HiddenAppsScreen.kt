package com.minimo.launcher.ui.hidden_apps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.ui.components.EmptyScreenView
import com.minimo.launcher.utils.launchApp
import com.minimo.launcher.utils.launchAppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenAppsScreen(
    viewModel: HiddenAppsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hidden Apps"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (state.initialLoaded && state.hiddenApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreenView(
                    title = "No hidden apps",
                    subTitle = "Apps in this list won't appear on your home screen"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(items = state.hiddenApps, key = { it.packageName }) { appInfo ->
                    HiddenAppNameItem(
                        modifier = Modifier.animateItem(),
                        appName = appInfo.name,
                        onClick = { context.launchApp(appInfo.packageName) },
                        onRemoveHiddenClick = { viewModel.onRemoveAppFromHiddenClicked(appInfo.packageName) },
                        onAppInfoClick = { context.launchAppInfo(appInfo.packageName) }
                    )
                }
            }
        }
    }
}