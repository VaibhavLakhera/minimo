package com.minimo.launcher.ui.settings.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.R
import com.minimo.launcher.ui.components.DropdownView
import com.minimo.launcher.ui.theme.Dimens
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.AndroidUtils
import com.minimo.launcher.utils.Constants
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import com.minimo.launcher.utils.HomeClockMode
import com.minimo.launcher.utils.StringUtils
import kotlin.math.roundToInt

@Composable
fun AppearanceScreen(
    viewModel: AppearanceViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 64.dp)
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            ThemeDropdown(
                selectedOption = StringUtils.themeModeText(
                    context = context,
                    mode = state.themeMode
                ),
                options = listOf(
                    ThemeMode.System to StringUtils.themeModeText(
                        context,
                        ThemeMode.System
                    ),
                    ThemeMode.Dark to StringUtils.themeModeText(
                        context,
                        ThemeMode.Dark
                    ),
                    ThemeMode.Light to StringUtils.themeModeText(
                        context,
                        ThemeMode.Light
                    ),
                    ThemeMode.Black to StringUtils.themeModeText(
                        context,
                        ThemeMode.Black
                    ),
                ),
                onOptionSelected = { selected ->
                    viewModel.onThemeModeChanged(selected)
                }
            )
            if (AndroidUtils.isDynamicThemeSupported()) {
                Spacer(modifier = Modifier.height(4.dp))

                ToggleItem(
                    title = "Dynamic Colours",
                    subtitle = "Adapt theme colours based on system settings",
                    isChecked = state.dynamicTheme,
                    onToggleClick = viewModel::onToggleDynamicTheme
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(
                    horizontal = Dimens.APP_HORIZONTAL_SPACING,
                )
            ) {
                Text(
                    text = "Home App Size:",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = state.homeTextSize.roundToInt().toString(),
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                modifier = Modifier
                    .semantics { contentDescription = "Text size slider" }
                    .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING),
                value = state.homeTextSize,
                onValueChange = {
                    viewModel.onHomeTextSizeChanged(it.roundToInt())
                },
                valueRange = Constants.HOME_TEXT_SIZE_RANGE,
                steps = 16,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Sample App",
                fontSize = state.homeTextSize.sp,
                modifier = Modifier.padding(horizontal = Dimens.APP_HORIZONTAL_SPACING)
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            AppsAlignmentDropdown(
                selectedOption = StringUtils.homeAppsAlignmentText(
                    context = context,
                    alignment = state.homeAppsAlignment
                ),
                options = listOf(
                    HomeAppsAlignment.Start to StringUtils.homeAppsAlignmentText(
                        context,
                        HomeAppsAlignment.Start
                    ),
                    HomeAppsAlignment.Center to StringUtils.homeAppsAlignmentText(
                        context,
                        HomeAppsAlignment.Center
                    ),
                    HomeAppsAlignment.End to StringUtils.homeAppsAlignmentText(
                        context,
                        HomeAppsAlignment.End
                    ),
                ),
                onOptionSelected = { selected ->
                    viewModel.onHomeAppsAlignmentChanged(selected)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            ToggleItem(
                title = "Show Home Clock",
                isChecked = state.showHomeClock,
                onToggleClick = viewModel::onToggleShowHomeClock
            )
            if (state.showHomeClock) {
                Spacer(modifier = Modifier.height(4.dp))

                ClockAlignmentDropdown(
                    selectedOption = StringUtils.homeClockAlignmentText(
                        context = context,
                        alignment = state.homeClockAlignment
                    ),
                    options = listOf(
                        HomeClockAlignment.Start to StringUtils.homeClockAlignmentText(
                            context,
                            HomeClockAlignment.Start
                        ),
                        HomeClockAlignment.Center to StringUtils.homeClockAlignmentText(
                            context,
                            HomeClockAlignment.Center
                        ),
                        HomeClockAlignment.End to StringUtils.homeClockAlignmentText(
                            context,
                            HomeClockAlignment.End
                        ),
                    ),
                    onOptionSelected = { selected ->
                        viewModel.onHomeClockAlignmentChanged(selected)
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                ClockModeDropdown(
                    selectedOption = StringUtils.homeClockModeText(
                        context = context,
                        mode = state.homeClockMode
                    ),
                    options = listOf(
                        HomeClockMode.Full to StringUtils.homeClockModeText(
                            context,
                            HomeClockMode.Full
                        ),
                        HomeClockMode.TimeOnly to StringUtils.homeClockModeText(
                            context,
                            HomeClockMode.TimeOnly
                        ),
                        HomeClockMode.DateOnly to StringUtils.homeClockModeText(
                            context,
                            HomeClockMode.DateOnly
                        ),
                    ),
                    onOptionSelected = { selected ->
                        viewModel.onHomeClockModeChanged(selected)
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            ToggleItem(
                title = "Show Status Bar",
                isChecked = state.showStatusBar,
                onToggleClick = viewModel::onToggleShowStatusBar
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            ToggleItem(
                title = "Show Keyboard",
                subtitle = "Show keyboard when the drawer is opened",
                isChecked = state.autoOpenKeyboardAllApps,
                onToggleClick = viewModel::onToggleAutoOpenKeyboardAllApps
            )
        }
    }
}

@Composable
private fun ThemeDropdown(
    selectedOption: String,
    options: List<Pair<ThemeMode, String>>,
    onOptionSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.APP_HORIZONTAL_SPACING,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.theme),
            modifier = Modifier.weight(1f),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownView(
            selectedOption = selectedOption,
            options = options.map { it.second },
            onOptionSelected = { selected ->
                onOptionSelected(options.first { it.second == selected }.first)
            }
        )
    }
}

@Composable
private fun AppsAlignmentDropdown(
    selectedOption: String,
    options: List<Pair<HomeAppsAlignment, String>>,
    onOptionSelected: (HomeAppsAlignment) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.APP_HORIZONTAL_SPACING,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.home_apps_alignment),
            modifier = Modifier.weight(1f),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownView(
            selectedOption = selectedOption,
            options = options.map { it.second },
            onOptionSelected = { selected ->
                onOptionSelected(options.first { it.second == selected }.first)
            }
        )
    }
}

@Composable
private fun ClockAlignmentDropdown(
    selectedOption: String,
    options: List<Pair<HomeClockAlignment, String>>,
    onOptionSelected: (HomeClockAlignment) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.APP_HORIZONTAL_SPACING,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.clock_alignment),
            modifier = Modifier.weight(1f),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownView(
            selectedOption = selectedOption,
            options = options.map { it.second },
            onOptionSelected = { selected ->
                onOptionSelected(options.first { it.second == selected }.first)
            }
        )
    }
}

@Composable
private fun ToggleItem(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onToggleClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onToggleClick)
            .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = {
                onToggleClick()
            }
        )
    }
}

@Composable
private fun ClockModeDropdown(
    selectedOption: String,
    options: List<Pair<HomeClockMode, String>>,
    onOptionSelected: (HomeClockMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.APP_HORIZONTAL_SPACING,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.clock_mode),
            modifier = Modifier.weight(1f),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownView(
            selectedOption = selectedOption,
            options = options.map { it.second },
            onOptionSelected = { selected ->
                onOptionSelected(options.first { it.second == selected }.first)
            }
        )
    }
}