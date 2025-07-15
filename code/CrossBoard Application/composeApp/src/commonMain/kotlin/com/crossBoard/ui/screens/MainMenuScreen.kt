package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.Admin
import com.crossBoard.domain.User
import com.crossBoard.utils.CustomColor

/**
 * A polished main menu screen with a welcoming header and icon-based navigation.
 *
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
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuButton(
                text = "Single Player",
                icon = Icons.Default.Person,
                onClick = onSinglePlayerClicked
            )
            MenuButton(
                text = "Multiplayer Match",
                icon = Icons.Default.Groups,
                onClick = onFindMatchClicked
            )
            MenuButton(
                text = "Check Statistics",
                icon = Icons.Default.Leaderboard,
                onClick = onCheckStatsClicked
            )
            if (user is Admin) {
                MenuButton(
                    text = "Admin Panel",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = onAdminPanelClicked,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    )
                )
            }
        }
    }

}

/**
 * A reusable, styled button for the main menu to avoid code repetition.
 * @param text The text to display on the button.
 * @param icon The icon to display on the button.
 * @param onClick The action to perform when clicked.
 * @param colors The colors to apply to the button.
 */
@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = CustomColor.LightBrown.value,
        contentColor = Color.White
    )
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // A more substantial button height
        shape = MaterialTheme.shapes.medium,
        colors = colors,
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // Align content to the left
        ) {
            Icon(imageVector = icon, contentDescription = null) // Decorative icon
            Spacer(Modifier.width(16.dp))
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}