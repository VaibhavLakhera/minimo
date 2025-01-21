package com.minimo.launcher.ui.intro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minimo.launcher.R
import com.minimo.launcher.ui.components.EmptyScreenView

private const val MINIMUM_FAVOURITE_COUNT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IntroPage2(
    viewModel: IntroViewModel,
    onContinueClick: () -> Unit
) {
    val allApps by viewModel.allAppsFlow.collectAsStateWithLifecycle(emptyList())
    val favouriteApps by viewModel.favouriteAppsFlow.collectAsStateWithLifecycle(emptyList())
    val minimumFavouriteAdded = favouriteApps.size >= MINIMUM_FAVOURITE_COUNT

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(id = R.string.favourite_apps))
            }
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyScreenView(
                        title = stringResource(id = R.string.add_only_the_apps_you_use_most),
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
        IntroBottomButton(
            text = if (minimumFavouriteAdded) {
                stringResource(R.string.btn_continue)
            } else {
                stringResource(R.string.add_at_least_to_continue, MINIMUM_FAVOURITE_COUNT)
            },
            onClick = {
                if (minimumFavouriteAdded) {
                    onContinueClick()
                }
            }
        )
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
            .padding(horizontal = 24.dp, vertical = 4.dp),
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