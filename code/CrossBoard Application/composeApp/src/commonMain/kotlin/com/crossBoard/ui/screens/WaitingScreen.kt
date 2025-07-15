package com.crossBoard.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.utils.CustomColor

/**
 * A polished, animated screen displayed while waiting for an operation.
 *
 * @param message The main message to display while waiting.
 * @param errorMessage An optional error message to display.
 * @param onCancelClick Callback invoked when the user cancels the operation.
 * @param cancelEnabled Whether the cancel button is enabled.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WaitingScreen(
    message: String = "Waiting...",
    errorMessage: String? = null,
    onCancelClick: () -> Unit,
    cancelEnabled: Boolean = true
) {
    BackHandler(enabled = cancelEnabled) {
        onCancelClick()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PulsingHourglass()

        Spacer(Modifier.height(24.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            color = CustomColor.DarkBrown.value
        )

        Text(
            text = "This shouldn't take long.",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        AnimatedVisibility(visible = errorMessage != null) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onCancelClick,
            enabled = cancelEnabled,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, CustomColor.DarkBrown.value),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = CustomColor.DarkBrown.value
            )
        ) {
            Text("Cancel")
        }
    }
}

/**
 * A composable that displays an hourglass icon with a subtle
 * "breathing" or "pulsing" animation.
 */
@Composable
private fun PulsingHourglass() {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Icon(
        imageVector = Icons.Default.HourglassTop,
        contentDescription = "Waiting...",
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .alpha(0.8f),
        tint = CustomColor.LightBrown.value
    )
}