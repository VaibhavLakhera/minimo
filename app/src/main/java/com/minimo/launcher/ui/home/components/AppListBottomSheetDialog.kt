package com.minimo.launcher.ui.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.minimo.launcher.R
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
    onAppInfoClick: () -> Unit,
    onUninstallClick: () -> Unit,
) {
    AppBottomSheetDialog(appName = appName, onDismiss = onDismiss) {
        AppBottomSheetText(
            text = if (isFavourite) stringResource(R.string.remove_favourite) else stringResource(R.string.add_favourite),
            onClick = onToggleFavouriteClick
        )
        AppBottomSheetText(
            text = stringResource(R.string.rename),
            onClick = onRenameClick
        )
        AppBottomSheetText(
            text = stringResource(R.string.hide_app),
            onClick = onHideAppClick
        )
        AppBottomSheetText(
            text = stringResource(R.string.app_info),
            onClick = onAppInfoClick
        )
        AppBottomSheetText(
            text = stringResource(R.string.uninstall),
            onClick = onUninstallClick
        )
    }
}