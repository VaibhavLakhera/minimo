package com.minimo.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimo.launcher.R
import com.minimo.launcher.ui.theme.Dimens

@Composable
fun ToggleAppItem(
    modifier: Modifier,
    appName: String,
    isChecked: Boolean,
    isWorkProfile: Boolean,
    onToggleClick: () -> Unit
) {
    val paddingValues = remember(isWorkProfile) {
        if (isWorkProfile) {
            PaddingValues(
                start = 0.dp,
                end = Dimens.APP_HORIZONTAL_SPACING,
                top = 4.dp,
                bottom = 4.dp
            )
        } else {
            PaddingValues(horizontal = Dimens.APP_HORIZONTAL_SPACING, vertical = 4.dp)
        }
    }

    Row(
        modifier = modifier
            .clickable(onClick = onToggleClick)
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isWorkProfile) {
            Icon(
                painter = painterResource(id = R.drawable.ic_work_profile),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }
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