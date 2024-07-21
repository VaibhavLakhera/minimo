package com.minimo.launcher.ui.hidden_apps

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
import com.minimo.launcher.ui.components.AppBottomSheetDialog
import com.minimo.launcher.ui.components.AppBottomSheetText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HiddenAppNameItem(
    modifier: Modifier,
    appName: String,
    onClick: () -> Unit,
    onRemoveHiddenClick: () -> Unit,
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
                    onLongClick = { appBottomSheetVisible = true }
                )
                .padding(horizontal = 48.dp, vertical = 16.dp),
        )
        if (appBottomSheetVisible) {
            HiddenAppBottomSheetDialog(
                appName = appName,
                onDismiss = { appBottomSheetVisible = false },
                onRemoveHiddenClick = {
                    appBottomSheetVisible = false
                    onRemoveHiddenClick()
                },
                onAppInfoClick = {
                    appBottomSheetVisible = false
                    onAppInfoClick()
                }
            )
        }
    }
}

@Composable
fun HiddenAppBottomSheetDialog(
    appName: String,
    onDismiss: () -> Unit,
    onRemoveHiddenClick: () -> Unit,
    onAppInfoClick: () -> Unit
) {
    AppBottomSheetDialog(appName = appName, onDismiss = onDismiss) {
        AppBottomSheetText(
            text = "Remove Hidden",
            onClick = onRemoveHiddenClick
        )
        AppBottomSheetText(
            text = "App Info",
            onClick = onAppInfoClick
        )
    }
}