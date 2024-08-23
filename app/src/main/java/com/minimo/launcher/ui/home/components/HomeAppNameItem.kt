package com.minimo.launcher.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimo.launcher.ui.theme.Dimens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeAppNameItem(
    modifier: Modifier,
    appName: String,
    onClick: () -> Unit,
    longClickDisabled: Boolean,
    onRemoveFavouriteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onAppInfoClick: () -> Unit
) {
    var appBottomSheetVisible by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(
            text = appName,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = if (longClickDisabled) {
                        null
                    } else {
                        { appBottomSheetVisible = true }
                    }
                )
                .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 16.dp),
        )
        if (appBottomSheetVisible) {
            HomeAppBottomSheetDialog(
                appName = appName,
                onDismiss = { appBottomSheetVisible = false },
                onRemoveFavouriteClick = {
                    appBottomSheetVisible = false
                    onRemoveFavouriteClick()
                },
                onRenameClick = {
                    appBottomSheetVisible = false
                    onRenameClick()
                },
                onAppInfoClick = {
                    appBottomSheetVisible = false
                    onAppInfoClick()
                }
            )
        }
    }
}