package com.minimo.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.minimo.launcher.ui.components.EmptyScreenView
import com.minimo.launcher.ui.components.RenameAppDialog
import com.minimo.launcher.ui.components.SheetDragHandle
import com.minimo.launcher.ui.components.TimeAndDateView
import com.minimo.launcher.ui.home.components.AppNameItem
import com.minimo.launcher.ui.home.components.HomeAppNameItem
import com.minimo.launcher.ui.home.components.SearchItem
import com.minimo.launcher.utils.launchApp
import com.minimo.launcher.utils.launchAppInfo
import kotlinx.coroutines.launch

private const val swipeUpThreshold = 70f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit,
    onAddFavouriteAppsClick: () -> Unit,
) {
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

    val lazyListState = rememberLazyListState()

    fun onSwipeUpAtEnd() {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    var swipeTriggered by remember { mutableStateOf(false) }
    var accumulatedSwipeUp by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // Called as the user drags.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Check if the list is at the bottom and the user is dragging upward (available.y negative)
                if (!lazyListState.canScrollForward && available.y < 0) {
                    accumulatedSwipeUp += -available.y // Track upward swipe distance
                    if (accumulatedSwipeUp >= swipeUpThreshold && !swipeTriggered) {
                        swipeTriggered = true
                    }
                }
                return Offset.Zero
            }

            // Called when the gesture is finishing.
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (swipeTriggered) {
                    // Trigger the callback once when the user lifts the finger.
                    onSwipeUpAtEnd()
                    swipeTriggered = false
                    accumulatedSwipeUp = 0f
                }
                return super.onPreFling(available)
            }

            // Reset the flag if fling completes.
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                swipeTriggered = false
                accumulatedSwipeUp = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    val systemNavigationHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetDragHandle = {
                SheetDragHandle(isExpanded = state.isBottomSheetExpanded)
            },
            sheetShadowElevation = 0.dp,
            sheetContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SearchItem(
                        modifier = Modifier.weight(1f),
                        searchText = state.searchText,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        endPadding = 0.dp
                    )
                    IconButton(
                        onClick = onSettingsClick
                    ) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = systemNavigationHeight)
                ) {
                    items(items = state.filteredAllApps, key = { it.packageName }) { appInfo ->
                        AppNameItem(
                            modifier = Modifier.animateItem(),
                            appName = appInfo.name,
                            isFavourite = appInfo.isFavourite,
                            onClick = { viewModel.onLaunchAppClick(appInfo) },
                            onToggleFavouriteClick = { viewModel.onToggleFavouriteAppClick(appInfo) },
                            onRenameClick = { viewModel.onRenameAppClick(appInfo) },
                            onHideAppClick = { viewModel.onHideAppClick(appInfo.packageName) },
                            onAppInfoClick = { context.launchAppInfo(appInfo.packageName) },
                            textAlign = state.appsTextAlign
                        )
                    }
                }
            },
            sheetPeekHeight = 56.dp + systemNavigationHeight,
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            if (state.initialLoaded && state.favouriteApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyScreenView(
                        title = "No favourites added",
                        subTitle = "Add your favourite apps to access them easily",
                        button = {
                            Button(onClick = onAddFavouriteAppsClick) {
                                Text(text = "Add favourite apps")
                            }
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(paddingValues)
                ) {
                    if (state.showHomeClock) {
                        TimeAndDateView(state.homeClockAlignment)
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(nestedScrollConnection),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(items = state.favouriteApps, key = { it.packageName }) { appInfo ->
                            HomeAppNameItem(
                                modifier = Modifier.animateItem(),
                                appName = appInfo.name,
                                onClick = { viewModel.onLaunchAppClick(appInfo) },
                                onRemoveFavouriteClick = {
                                    viewModel.onRemoveAppFromFavouriteClicked(
                                        appInfo.packageName
                                    )
                                },
                                onRenameClick = { viewModel.onRenameAppClick(appInfo) },
                                onAppInfoClick = { context.launchAppInfo(appInfo.packageName) },
                                textAlign = state.appsTextAlign,
                                textSize = state.homeTextSize.sp
                            )
                        }
                    }
                }
            }
        }

        // To cover the navigation bars with surface color.
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(systemNavigationHeight)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        )
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