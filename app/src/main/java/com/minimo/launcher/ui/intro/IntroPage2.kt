package com.minimo.launcher.ui.intro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.minimo.launcher.ui.home.components.SearchItem
import com.minimo.launcher.ui.theme.Dimens
import com.minimo.launcher.utils.Constants.INTRO_MINIMUM_FAVOURITE_COUNT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IntroPage2(
    viewModel: IntroViewModel,
    onContinueClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(id = R.string.favourite_apps))
            }
        )
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
                AddFavouriteAppItem(
                    modifier = Modifier.animateItem(),
                    appName = appInfo.name,
                    isFavourite = appInfo.isFavourite,
                    onToggleFavouriteClick = { viewModel.onToggleFavouriteAppClick(appInfo) }
                )
            }
        }
        IntroBottomButton(
            text = if (state.minimumFavouriteAdded) {
                stringResource(R.string.btn_continue)
            } else {
                stringResource(R.string.add_at_least_to_continue, INTRO_MINIMUM_FAVOURITE_COUNT)
            },
            onClick = {
                if (state.minimumFavouriteAdded) {
                    onContinueClick()
                }
            }
        )
    }
}

@Composable
private fun AddFavouriteAppItem(
    modifier: Modifier,
    appName: String,
    isFavourite: Boolean,
    onToggleFavouriteClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onToggleFavouriteClick)
            .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = appName,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isFavourite, onCheckedChange = {
                onToggleFavouriteClick()
            })
    }
}