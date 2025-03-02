package com.minimo.launcher.ui.favourite_apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.R
import com.minimo.launcher.ui.components.ToggleAppItem
import com.minimo.launcher.ui.home.components.SearchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteAppsScreen(
    viewModel: FavouriteAppsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.favourite_apps)
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
                .padding(paddingValues)
        ) {
            SearchItem(
                modifier = Modifier.fillMaxWidth(),
                searchText = state.searchText,
                onSearchTextChange = viewModel::onSearchTextChange
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(items = state.filteredAllApps, key = { it.packageName }) { appInfo ->
                    ToggleAppItem(
                        modifier = Modifier.animateItem(),
                        appName = appInfo.name,
                        isChecked = appInfo.isFavourite,
                        onToggleClick = { viewModel.onToggleFavouriteAppClick(appInfo) }
                    )
                }
            }
        }
    }
}