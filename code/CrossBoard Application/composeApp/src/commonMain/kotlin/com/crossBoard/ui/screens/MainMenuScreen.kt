package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.utils.CustomColor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainMenuScreen(
    onFindMatchClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Main Menu", style = MaterialTheme.typography.h4, color = CustomColor.DarkBrown.value)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onFindMatchClicked,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
        ) {
            Text("Find Match", color = Color.White)
        }
    }
}

@Preview
@Composable
fun previewMenuScreen(){
    MainMenuScreen({})
}