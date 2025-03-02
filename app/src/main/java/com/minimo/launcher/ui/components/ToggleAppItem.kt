package com.minimo.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimo.launcher.ui.theme.Dimens

@Composable
fun ToggleAppItem(
    modifier: Modifier,
    appName: String,
    isChecked: Boolean,
    onToggleClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onToggleClick)
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
            checked = isChecked,
            onCheckedChange = {
                onToggleClick()
            }
        )
    }
}