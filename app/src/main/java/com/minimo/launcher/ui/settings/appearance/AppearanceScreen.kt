package com.minimo.launcher.ui.settings.appearance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.ui.entities.ThemeMode
import com.minimo.launcher.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    viewModel: AppearanceViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Appearance"
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
        Column(
            modifier = Modifier
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
        }
    }
}