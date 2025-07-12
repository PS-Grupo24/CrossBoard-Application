package com.crossBoard.ui.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.crossBoard.utils.CustomColor

/**
 * Element responsible for the alert dialog.
 * @param onDismissRequest The action to perform on dismiss request.
 * @param title The alert title.
 * @param text The alert text description.
 * @param onConfirm The action to perform when the alert is confirmed.
 * @param confirmText The description of the action to perform on the confirmation.
 * @param onDismiss The action to perform when the alert is dismissed.
 * @param dismissText The description of the action to perform when dismissed.
 */
@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmText: String,
    onDismiss: () -> Unit,
    dismissText: String,
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title, color = CustomColor.DarkBrown.value, modifier = Modifier.testTag("Alert Dialog Title test"))},
        text = { Text(text, color = CustomColor.LightBrown.value, modifier = Modifier.testTag("Alert Dialog Text test")) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("Confirm Button Test"),
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
            ){
                Text(confirmText, color = Color.White, modifier = Modifier.testTag("Confirm Button Text Test"))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("Dismiss Button Test"),
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
            ){
                Text(dismissText, color = Color.White, modifier = Modifier.testTag("Dismiss Button Text Test"))
            }
        }
    )
}