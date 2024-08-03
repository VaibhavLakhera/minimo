package com.minimo.launcher.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun LimitedOutlinedTextField(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    maxLength: Int = Int.MAX_VALUE,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false
) {
    OutlinedTextField(
        modifier = Modifier.focusRequester(focusRequester),
        value = TextFieldValue(
            text = value,
            selection = TextRange(value.length)
        ),
        onValueChange = { newValue ->
            if (newValue.text.length <= maxLength) {
                onValueChange(newValue.text)
            }
        },
        singleLine = singleLine,
        label = label,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions
    )
}