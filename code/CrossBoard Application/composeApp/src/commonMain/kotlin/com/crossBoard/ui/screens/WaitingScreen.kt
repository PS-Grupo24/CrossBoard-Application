package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.utils.CustomColor

/**
 * A simple screen displayed while waiting for an operation,
 * like finding a match or waiting for an opponent.
 *
 * @param message The message to display while waiting (optional).
 * @param onCancelClick Callback invoked when the user clicks the Cancel button.
 */
@Composable
fun WaitingScreen(
    errorMessage: String? = null,
    message: String = "Waiting...",
    onCancelClick: () -> Unit,
    cancelEnabled: Boolean = true
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CircularProgressIndicator(color = CustomColor.LightBrown.value)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            color = CustomColor.DarkBrown.value
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCancelClick,
            enabled = cancelEnabled,
            colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
        ) {
            Text("Cancel", color = Color.White)
        }
    }
}
