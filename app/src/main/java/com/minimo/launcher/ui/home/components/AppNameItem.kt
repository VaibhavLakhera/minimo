package com.minimo.launcher.ui.home.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimo.launcher.ui.theme.Dimens

@Composable
fun AppNameItem(
    modifier: Modifier,
    appName: String,
    isFavourite: Boolean,
    isHidden: Boolean,
    textAlign: TextAlign,
    textSize: TextUnit = 20.sp,
    onClick: () -> Unit,
    onToggleFavouriteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onToggleHideClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onLongClick: () -> Unit = { },
    onUninstallClick: () -> Unit
) {
    var appBottomSheetVisible by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(
            text = appName,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = textSize,
            textAlign = textAlign,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        onLongClick()
                        appBottomSheetVisible = true
                    }
                )
                .padding(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (appBottomSheetVisible) {
            AppListBottomSheetDialog(
                appName = appName,
                isFavourite = isFavourite,
                isHidden = isHidden,
                onDismiss = { appBottomSheetVisible = false },
                onToggleFavouriteClick = {
                    appBottomSheetVisible = false
                    onToggleFavouriteClick()
                },
                onRenameClick = {
                    appBottomSheetVisible = false
                    onRenameClick()
                },
                onToggleHideClick = {
                    appBottomSheetVisible = false
                    onToggleHideClick()
                },
                onAppInfoClick = {
                    appBottomSheetVisible = false
                    onAppInfoClick()
                },
                onUninstallClick = {
                    appBottomSheetVisible = false
                    onUninstallClick()
                }
            )
        }
    }
}