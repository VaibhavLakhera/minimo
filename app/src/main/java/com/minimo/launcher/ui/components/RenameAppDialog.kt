package com.minimo.launcher.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun RenameAppDialog(
    originalName: String,
    currentName: String,
    onRenameClick: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var newName by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentName,
                selection = TextRange(currentName.length)
            )
        )
    }
    AlertDialog(
        onDismissRequest = onCancelClick,
        title = { Text("Rename App") },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = newName,
                onValueChange = { newValue ->
                    if (newValue.text.length <= 30) {
                        newName = newValue
                    }
                },
                singleLine = true,
                label = { Text("App Name") },
                placeholder = { Text(originalName) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        },
        confirmButton = {
            Button(onClick = { onRenameClick(newName.text.trim()) }) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelClick) {
                Text("Cancel")
            }
        }
    )
}