package com.minimo.launcher.ui.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.minimo.launcher.ui.components.EmptyScreenView

@Composable
internal fun IntroPage1(
    onContinueClick: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            EmptyScreenView(
                title = "Welcome to Minimo",
                subTitle = "Let's get started by quickly setting up your favourite apps"
            )
        }
        IntroBottomButton(
            text = "Continue",
            onClick = onContinueClick
        )
    }
}