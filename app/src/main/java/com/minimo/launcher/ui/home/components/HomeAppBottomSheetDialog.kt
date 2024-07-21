package com.minimo.launcher.ui.home.components

import androidx.compose.runtime.Composable
import com.minimo.launcher.ui.components.AppBottomSheetDialog
import com.minimo.launcher.ui.components.AppBottomSheetText

@Composable
fun HomeAppBottomSheetDialog(
    appName: String,
    onDismiss: () -> Unit,
    onRemoveFavouriteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onAppInfoClick: () -> Unit
) {
    AppBottomSheetDialog(appName = appName, onDismiss = onDismiss) {
        AppBottomSheetText(
            text = "Remove Favourite",
            onClick = onRemoveFavouriteClick
        )
        AppBottomSheetText(
            text = "Rename",
            onClick = onRenameClick
        )
        AppBottomSheetText(
            text = "App Info",
            onClick = onAppInfoClick
        )
    }
}