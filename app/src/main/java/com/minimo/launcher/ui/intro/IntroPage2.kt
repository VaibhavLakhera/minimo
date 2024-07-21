package com.minimo.launcher.ui.intro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.ui.components.EmptyScreenView

@Composable
internal fun IntroPage2(viewModel: IntroViewModel) {
    val allApps by viewModel.allAppsFlow.collectAsStateWithLifecycle(emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreenView(
                    title = "Your Everyday Favorites",
                    subTitle = "Keep only the apps you use most",
                    horizontalPadding = 20.dp
                )
            }
        }
        items(items = allApps, key = { it.packageName }) { appInfo ->
            AddFavouriteAppItem(
                appName = appInfo.name,
                isFavourite = appInfo.isFavourite,
                onToggleFavouriteClick = { viewModel.onToggleFavouriteAppClick(appInfo) }
            )
        }
    }
}

@Composable
private fun AddFavouriteAppItem(
    appName: String,
    isFavourite: Boolean,
    onToggleFavouriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onToggleFavouriteClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appName,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isFavourite, onCheckedChange = {
                onToggleFavouriteClick()
            })
    }
}