package com.minimo.launcher.ui.home.components

import androidx.compose.runtime.Composable
import com.minimo.launcher.ui.components.AppBottomSheetDialog
import com.minimo.launcher.ui.components.AppBottomSheetText

@Composable
fun AppListBottomSheetDialog(
    appName: String,
    isFavourite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavouriteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onHideAppClick: () -> Unit,
    onAppInfoClick: () -> Unit
) {
    AppBottomSheetDialog(appName = appName, onDismiss = onDismiss) {
        AppBottomSheetText(
            text = if (isFavourite) "Remove Favourite" else "Favourite",
            onClick = onToggleFavouriteClick
        )
        AppBottomSheetText(
            text = "Rename",
            onClick = onRenameClick
        )
        AppBottomSheetText(
            text = "Hide App",
            onClick = onHideAppClick
        )
        AppBottomSheetText(
            text = "App Info",
            onClick = onAppInfoClick
        )
    }
}