package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.Admin
import com.crossBoard.domain.NormalUser
import com.crossBoard.domain.User
import com.crossBoard.utils.CustomColor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainMenuScreen(
    user: User,
    onSinglePlayerClicked: () -> Unit,
    onFindMatchClicked: () -> Unit,
    onCheckStatsClicked: () -> Unit
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
            onSinglePlayerClicked,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
        ){
            Text("Single Player", color = Color.White)
        }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onFindMatchClicked,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
        ) {
            Text("Multiplayer Match", color = Color.White)
        }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onCheckStatsClicked,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
        ){
            Text("Check Statistics", color = Color.White)
        }
        if (user is Admin){
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
            ){
                Text("Admin Panel", color = Color.White)
            }
        }
    }
}