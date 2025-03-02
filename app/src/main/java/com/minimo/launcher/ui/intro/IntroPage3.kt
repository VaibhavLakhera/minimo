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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minimo.launcher.R
import com.minimo.launcher.ui.components.EmptyScreenView
import com.minimo.launcher.utils.openHomeSettings

@Composable
internal fun IntroPage3(
    onSkipClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyScreenView(
            title = stringResource(R.string.app_tagline),
            subTitle = stringResource(R.string.choose_minimo_as_your_default_launcher),
            horizontalPadding = 20.dp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = context::openHomeSettings) {
            Text(text = stringResource(R.string.set_default_launcher))
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = {
            onSkipClick()
        }) {
            Text(text = stringResource(R.string.skip))
        }
    }
}