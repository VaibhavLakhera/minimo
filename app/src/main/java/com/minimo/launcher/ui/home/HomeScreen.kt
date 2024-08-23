@file:OptIn(ExperimentalFoundationApi::class)

package com.minimo.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.minimo.launcher.ui.components.EmptyScreenView
import com.minimo.launcher.ui.components.RenameAppDialog
import com.minimo.launcher.ui.components.SheetDragHandle
import com.minimo.launcher.ui.home.components.AppNameItem
import com.minimo.launcher.ui.home.components.HomeAppNameItem
import com.minimo.launcher.ui.home.components.SearchItem
import com.minimo.launcher.utils.launchApp
import com.minimo.launcher.utils.launchAppInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    BackHandler {
        coroutineScope.launch {
            if (bottomSheetScaffoldState.bottomSheetState.currentValue != SheetValue.PartiallyExpanded) {
                bottomSheetScaffoldState.bottomSheetState.partialExpand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.expand()
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
                focusManager.clearFocus()
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetDragHandle = {
            SheetDragHandle(isExpanded = state.isBottomSheetExpanded)
        },
        sheetContent = {
            SearchItem(
                searchText = state.searchText,
                onSearchTextChange = viewModel::onSearchTextChange
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(items = state.filteredAllApps, key = { it.packageName }) { appInfo ->
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
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        if (state.initialLoaded && state.favouriteApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreenView(
                    title = "No favourite apps",
                    subTitle = "Long press an app to add it to this list and access it quickly"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                items(items = state.favouriteApps, key = { it.packageName }) { appInfo ->
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
    }

    if (state.renameAppDialog != null) {
        val app = state.renameAppDialog!!
        RenameAppDialog(
            originalName = app.appName,
            currentName = app.name,
            onRenameClick = viewModel::onRenameApp,
            onCancelClick = viewModel::onDismissRenameAppDialog
        )
    }
}