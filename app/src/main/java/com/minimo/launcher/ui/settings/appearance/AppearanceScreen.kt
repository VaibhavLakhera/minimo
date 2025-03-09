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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.ui.components.DropdownView
import com.minimo.launcher.ui.theme.Dimens
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.Constants
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import kotlin.math.roundToInt

@Composable
fun AppearanceScreen(
    viewModel: AppearanceViewModel,
    onBackClick: () -> Unit
) {
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
            DropdownWithName(
                name = "Theme",
                selectedOption = state.themeMode?.name.orEmpty(),
                options = ThemeMode.entries.map { it.name },
                onOptionSelected = { selected ->
                    viewModel.onThemeModeChanged(ThemeMode.valueOf(selected))
                }
            )

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

            DropdownWithName(
                name = "Home Apps Alignment",
                selectedOption = state.homeAppsAlignment?.name.orEmpty(),
                options = listOf(
                    TextAlign.Start,
                    TextAlign.Center,
                    TextAlign.End
                ).map { it.toString() },
                onOptionSelected = { selected ->
                    viewModel.onHomeAppsAlignmentChanged(HomeAppsAlignment.valueOf(selected))
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .clickable(onClick = viewModel::onToggleShowHomeClock)
                    .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Show Home Clock",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = state.showHomeClock,
                    onCheckedChange = {
                        viewModel.onToggleShowHomeClock()
                    }
                )
            }
            if (state.showHomeClock) {
                DropdownWithName(
                    name = "Home Clock Alignment",
                    selectedOption = state.homeClockAlignment?.name.orEmpty(),
                    options = HomeClockAlignment.entries.map { it.name },
                    onOptionSelected = { selected ->
                        viewModel.onHomeClockAlignmentChanged(HomeClockAlignment.valueOf(selected))
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .clickable(onClick = viewModel::onToggleShowStatusBar)
                    .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Show Status Bar",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = state.showStatusBar,
                    onCheckedChange = {
                        viewModel.onToggleShowStatusBar()
                    }
                )
            }
        }
    }
}

@Composable
private fun DropdownWithName(
    name: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
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
            name,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownView(
            selectedOption = selectedOption,
            options = options,
            onOptionSelected = onOptionSelected
        )
    }
}