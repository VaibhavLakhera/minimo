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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.ui.theme.Dimens
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeClockAlignment

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
            Text(
                text = "THEME",
                modifier = Modifier.padding(
                    horizontal = Dimens.APP_HORIZONTAL_SPACING,
                    vertical = 8.dp
                ),
                fontWeight = FontWeight.Bold
            )
            ThemeMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onThemeModeChanged(mode) }
                        .padding(
                            horizontal = Dimens.APP_HORIZONTAL_SPACING - 12.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.themeMode == mode,
                        onClick = { viewModel.onThemeModeChanged(mode) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(mode.name)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "HOME APPS ALIGNMENT",
                modifier = Modifier.padding(
                    horizontal = Dimens.APP_HORIZONTAL_SPACING,
                    vertical = 8.dp
                ),
                fontWeight = FontWeight.Bold
            )
            listOf(TextAlign.Start, TextAlign.Center, TextAlign.End).forEach { align ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onHomeAppsAlignmentChanged(align) }
                        .padding(
                            horizontal = Dimens.APP_HORIZONTAL_SPACING - 12.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.homeAppsAlign == align,
                        onClick = { viewModel.onHomeAppsAlignmentChanged(align) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(align.toString())
                }
            }
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
                HomeClockAlignment.entries.forEach { align ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onHomeClockAlignmentChanged(align) }
                            .padding(
                                horizontal = Dimens.APP_HORIZONTAL_SPACING - 12.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.homeClockAlignment == align,
                            onClick = { viewModel.onHomeClockAlignmentChanged(align) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(align.toString())
                    }
                }
            }
        }
    }
}