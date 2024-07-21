@file:OptIn(ExperimentalFoundationApi::class)

package com.minimo.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.minimo.launcher.ui.components.RenameAppDialog
import com.minimo.launcher.ui.components.SheetDragHandle
import com.minimo.launcher.ui.home.components.AppNameItem
import com.minimo.launcher.ui.home.components.HomeAppNameItem
import com.minimo.launcher.utils.launchApp
import com.minimo.launcher.utils.launchAppInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val favouriteApps by viewModel.favouriteAppsFlow.collectAsStateWithLifecycle(emptyList())
    val allApps by viewModel.allAppsFlow.collectAsStateWithLifecycle(emptyList())
    val isBottomSheetExpanded by viewModel.isBottomSheetExpanded.collectAsStateWithLifecycle()
    val renameAppDialog by viewModel.renameAppDialog.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    BackHandler {
        if (bottomSheetScaffoldState.bottomSheetState.targetValue != SheetValue.PartiallyExpanded) {
            coroutineScope.launch {
                bottomSheetScaffoldState.bottomSheetState.partialExpand()
            }
        }
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.launchApp.collect(context::launchApp)
        }
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.launchSettings.collect {
                onSettingsClick()
            }
        }
    }
    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.targetValue) {
        when (bottomSheetScaffoldState.bottomSheetState.targetValue) {
            SheetValue.Expanded -> {
                viewModel.setBottomSheetExpanded(true)
            }

            else -> {
                viewModel.setBottomSheetExpanded(false)
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetDragHandle = {
            SheetDragHandle(isExpanded = isBottomSheetExpanded)
        },
        sheetContent = {
            LazyColumn(verticalArrangement = Arrangement.Center) {
                items(items = allApps, key = { it.packageName }) { appInfo ->
                    AppNameItem(
                        modifier = Modifier.animateItemPlacement(),
                        appName = appInfo.name,
                        isFavourite = appInfo.isFavourite,
                        onClick = { viewModel.onLaunchAppClick(appInfo) },
                        longClickDisabled = appInfo.isLauncherSettings,
                        onToggleFavouriteClick = { viewModel.onToggleFavouriteAppClick(appInfo) },
                        onRenameClick = { viewModel.onRenameAppClick(appInfo) },
                        onHideAppClick = { viewModel.onHideAppClick(appInfo.packageName) },
                        onAppInfoClick = { context.launchAppInfo(appInfo.packageName) },
                    )
                }
            }
        },
        sheetPeekHeight = 56.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            items(items = favouriteApps, key = { it.packageName }) { appInfo ->
                HomeAppNameItem(
                    modifier = Modifier.animateItemPlacement(),
                    appName = appInfo.name,
                    onClick = { viewModel.onLaunchAppClick(appInfo) },
                    longClickDisabled = appInfo.isLauncherSettings,
                    onRemoveFavouriteClick = { viewModel.onRemoveAppFromFavouriteClicked(appInfo.packageName) },
                    onRenameClick = { viewModel.onRenameAppClick(appInfo) },
                    onAppInfoClick = { context.launchAppInfo(appInfo.packageName) }
                )
            }
        }
    }

    if (renameAppDialog != null) {
        val app = renameAppDialog!!
        RenameAppDialog(
            originalName = app.appName,
            currentName = app.name,
            onRenameClick = viewModel::onRenameApp,
            onCancelClick = viewModel::onDismissRenameAppDialog
        )
    }
}