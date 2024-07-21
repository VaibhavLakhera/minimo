package com.minimo.launcher.ui.hidden_apps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.utils.launchApp
import com.minimo.launcher.utils.launchAppInfo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HiddenAppsScreen(
    viewModel: HiddenAppsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val hiddenApps by viewModel.hiddenAppsFlow.collectAsStateWithLifecycle(emptyList())

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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues),
        ) {
            items(items = hiddenApps, key = { it.packageName }) { appInfo ->
                HiddenAppNameItem(
                    modifier = Modifier.animateItemPlacement(),
                    appName = appInfo.name,
                    onClick = { context.launchApp(appInfo.packageName) },
                    onRemoveHiddenClick = { viewModel.onRemoveAppFromHiddenClicked(appInfo.packageName) },
                    onAppInfoClick = { context.launchAppInfo(appInfo.packageName) }
                )
            }
        }
    }
}