package com.minimo.launcher.ui.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.minimo.launcher.ui.components.EmptyScreenView
import com.minimo.launcher.utils.chooseDefaultLauncher

@Composable
internal fun IntroPage3(
    onDoneClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyScreenView(
            title = "Distraction free home screen",
            subTitle = "Choose Minimo as your default launcher",
            horizontalPadding = 20.dp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { context.chooseDefaultLauncher() }) {
            Text(text = "Set Default Launcher")
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = onDoneClick) {
            Text(text = "Skip")
        }
    }
}