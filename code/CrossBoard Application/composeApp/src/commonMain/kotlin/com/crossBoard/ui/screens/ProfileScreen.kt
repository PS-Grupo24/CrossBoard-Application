package com.crossBoard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.User
import com.crossBoard.utils.CustomColor

/**
 * A modern, polished screen for displaying user profile information.
 * Includes a TopAppBar, an avatar, and neatly organized details.
 * @param user The current logged user.
 * @param onBack Callback to navigate back.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    user: User,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(user)
        Spacer(Modifier.height(32.dp))
        InfoCard(user)
    }

}

/**
 * Displays the main user avatar, username, and email.
 */
@Composable
private fun ProfileHeader(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(CustomColor.LightBrown.value),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (user.username.value.isNotEmpty()) user.username.value.first().uppercase() else "U",
                style = MaterialTheme.typography.h3,
                color = Color.White,
            )
        }
        Text(
            text = user.username.value,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = CustomColor.DarkBrown.value
        )
    }
}

/**
 * A Card that contains less critical user information.
 */
@Composable
private fun InfoCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = user.email.value
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            InfoRow(
                icon = Icons.Default.VpnKey,
                label = "User ID",
                value = user.id.toString()
            )
        }
    }
}

/**
 * A reusable row for displaying a piece of information with an icon, label, and value.
 */
@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = CustomColor.DarkBrown.value
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            color = CustomColor.DarkBrown.value
        )
    }
}