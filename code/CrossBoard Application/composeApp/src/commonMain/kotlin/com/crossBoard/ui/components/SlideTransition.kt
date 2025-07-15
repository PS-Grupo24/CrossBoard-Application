package com.crossBoard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> SlideTransition(
    targetState: T,
    modifier: Modifier = Modifier,
    direction: (from: T, to: T) -> Int = { _, _ -> 1 },
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            val dir = direction(initialState, targetState)
            if (initialState == targetState) {
                fadeIn() with fadeOut()
            } else {
                slideInHorizontally { fullWidth -> dir * fullWidth } + fadeIn() with
                        slideOutHorizontally { fullWidth -> -dir * fullWidth } + fadeOut()
            }.using(SizeTransform(clip = false))
        },
        label = "SlideTransition"
    ) { state ->
        content(state)
    }
}