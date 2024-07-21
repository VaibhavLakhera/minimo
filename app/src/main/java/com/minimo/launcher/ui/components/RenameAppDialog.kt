package com.minimo.launcher.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun RenameAppDialog(
    originalName: String,
    currentName: String,
    onRenameClick: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onCancelClick,
        title = { Text("Rename App") },
        text = {
            LimitedOutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                placeholder = { Text(originalName) },
                label = { Text("App Name") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                maxLength = 30
            )
        },
        confirmButton = {
            Button(onClick = { onRenameClick(newName.trim()) }) {
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