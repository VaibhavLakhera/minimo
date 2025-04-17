package com.minimo.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.minimo.launcher.utils.lockScreen
import com.minimo.launcher.utils.showNotificationDrawer
import com.minimo.launcher.utils.uninstallApp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val swipeUpThreshold = -70f
private const val swipeDownThreshold = 70f

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
    val keyboardController = LocalSoftwareKeyboardController.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val triggerHomePressed by viewModel.triggerHomePressed.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    val focusRequester = remember { FocusRequester() }

    BackHandler {
        coroutineScope.launch {
            if (bottomSheetScaffoldState.bottomSheetState.currentValue != SheetValue.PartiallyExpanded) {
                bottomSheetScaffoldState.bottomSheetState.partialExpand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.expand()
            }
        }
    }

    LaunchedEffect(triggerHomePressed) {
        if (triggerHomePressed) {
            if (bottomSheetScaffoldState.bottomSheetState.currentValue != SheetValue.PartiallyExpanded) {
                bottomSheetScaffoldState.bottomSheetState.partialExpand()
            }
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.launchApp.collect(context::launchApp)
        }
    }

    fun hideKeyboardWithClearFocus() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.currentValue) {
        when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
            SheetValue.Expanded -> {
                if (!state.isBottomSheetExpanded) {
                    viewModel.setBottomSheetExpanded(true)

                    if (state.autoOpenKeyboardAllApps) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            }

            else -> {
                viewModel.setBottomSheetExpanded(false)
                focusManager.clearFocus()
            }
        }
    }

    val homeLazyListState = rememberLazyListState()
    val allAppsLazyListState = rememberLazyListState()

    LaunchedEffect(allAppsLazyListState) {
        snapshotFlow { allAppsLazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (isScrolling && allAppsLazyListState.canScrollBackward) {
                    hideKeyboardWithClearFocus()
                }
            }
    }

    fun onSwipeUpAtEnd() {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    fun onSwipeDownAtEnd() {
        context.showNotificationDrawer()
    }

    var accumulatedSwipe by remember { mutableFloatStateOf(0f) }
    var swipeTriggered by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // Called as the user drags.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Add the new delta to the accumulatedSwipe.
                accumulatedSwipe += available.y
                // Only trigger if not already triggered.
                if (!swipeTriggered) {
                    when {
                        accumulatedSwipe >= swipeDownThreshold -> {
                            swipeTriggered = true
                        }

                        accumulatedSwipe <= swipeUpThreshold -> {
                            swipeTriggered = true
                        }
                    }
                }
                return Offset.Zero
            }

            // Called when the gesture is finishing.
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (swipeTriggered) {
                    if (accumulatedSwipe > 0) {
                        onSwipeDownAtEnd()
                    } else {
                        onSwipeUpAtEnd()
                    }
                }
                // Reset after gesture completes.
                swipeTriggered = false
                accumulatedSwipe = 0f
                return super.onPreFling(available)
            }

            // Reset the flag if fling completes.
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                swipeTriggered = false
                accumulatedSwipe = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    val systemNavigationHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    if (state.doubleTapToLock) {
                        context.lockScreen()
                    }
                })
            }
    ) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetDragHandle = {
                SheetDragHandle(isExpanded = state.isBottomSheetExpanded)
            },
            sheetShadowElevation = 0.dp,
            sheetContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SearchItem(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        searchText = state.searchText,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        endPadding = 0.dp
                    )
                    IconButton(
                        onClick = {
                            hideKeyboardWithClearFocus()
                            onSettingsClick()
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
                LazyColumn(
                    state = allAppsLazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = systemNavigationHeight)
                ) {
                    items(items = state.filteredAllApps, key = { it.packageName }) { appInfo ->
                        AppNameItem(
                            modifier = Modifier.animateItem(),
                            appName = appInfo.name,
                            isFavourite = appInfo.isFavourite,
                            onClick = {
                                hideKeyboardWithClearFocus()
                                viewModel.onLaunchAppClick(appInfo)
                            },
                            onToggleFavouriteClick = { viewModel.onToggleFavouriteAppClick(appInfo) },
                            onRenameClick = { viewModel.onRenameAppClick(appInfo) },
                            onHideAppClick = { viewModel.onHideAppClick(appInfo.packageName) },
                            onAppInfoClick = { context.launchAppInfo(appInfo.packageName) },
                            textAlign = state.appsTextAlign,
                            onLongClick = ::hideKeyboardWithClearFocus,
                            onUninstallClick = { context.uninstallApp(appInfo.packageName) }
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
                        TimeAndDateView(
                            horizontalAlignment = state.homeClockAlignment,
                            clockMode = state.homeClockMode,
                            twentyFourHourFormat = state.twentyFourHourFormat
                        )
                    }

                    LazyColumn(
                        state = homeLazyListState,
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