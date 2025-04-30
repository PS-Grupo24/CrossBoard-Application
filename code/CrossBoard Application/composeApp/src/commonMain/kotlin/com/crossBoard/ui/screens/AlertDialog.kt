package com.crossBoard.ui.screens

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.crossBoard.utils.CustomColor

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
        title = { Text(title, color = CustomColor.DarkBrown.value) },
        text = { Text(text, color = CustomColor.LightBrown.value) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
            ){
                Text(confirmText, color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
            ){
                Text(dismissText, color = Color.White)
            }
        }
    )
}