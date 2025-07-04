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

/**
 * Responsible for the display of the main menu elements.
 * It displays a button to navigate to each of the functionalities.
 * @param user The current logged user.
 * @param onSinglePlayerClicked The action to perform when the single player button is clicked.
 * @param onFindMatchClicked The action to perform when the multiplayer button is clicked.
 * @param onCheckStatsClicked The action to perform when the check statistics button is clicked.
 * @param onAdminPanelClicked The action to perform when the admin panel button is clicked.
 */
@Composable
fun MainMenuScreen(
    user: User,
    onSinglePlayerClicked: () -> Unit,
    onFindMatchClicked: () -> Unit,
    onCheckStatsClicked: () -> Unit,
    onAdminPanelClicked: () -> Unit,
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
                onClick = onAdminPanelClicked,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
            ){
                Text("Admin Panel", color = Color.White)
            }
        }
    }
}