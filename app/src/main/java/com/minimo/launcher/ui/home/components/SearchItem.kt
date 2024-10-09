package com.minimo.launcher.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.minimo.launcher.ui.theme.Dimens

@Composable
fun SearchItem(
    modifier: Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = { Text(text = "Search app") },
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Dimens.APP_HORIZONTAL_SPACING),
        singleLine = true
    )
}