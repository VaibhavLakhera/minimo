package com.minimo.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.minimo.launcher.ui.theme.Dimens
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun TimeAndDateView(
    horizontalAlignment: Alignment.Horizontal
) {
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    val timeFormatter = remember { DateTimeFormatter.ofPattern("hh:mm a") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, dd MMMM") }

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            while (true) {
                currentDateTime = LocalDateTime.now()

                val nextMinute = currentDateTime.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)
                val nextMinuteDelay = ChronoUnit.MILLIS.between(currentDateTime, nextMinute) + 1000

                delay(nextMinuteDelay)
            }
        }
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.APP_HORIZONTAL_SPACING,
                vertical = 16.dp
            )
    ) {
        Text(
            text = currentDateTime.format(timeFormatter).uppercase(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentDateTime.format(dateFormatter),
            fontSize = 18.sp
        )
    }
}