package com.minimo.launcher.ui.intro

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    viewModel: IntroViewModel,
    onIntroCompleted: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    fun navigateToNextPage() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        HorizontalPager(
            modifier = Modifier.padding(it),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> IntroPage1(onContinueClick = ::navigateToNextPage)

                // Set the preference flag of intro completed true here, because in page 3 we won't be able to change it after selecting home app.
                1 -> IntroPage2(
                    viewModel = viewModel,
                    onContinueClick = {
                        viewModel.setIntroCompleted()
                        navigateToNextPage()
                    }
                )

                2 -> IntroPage3(onSkipClick = onIntroCompleted)
            }
        }
    }
}