package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.User
import com.crossBoard.utils.CustomColor

/**
 * Screen responsible for the display of the user profile information.
 * @param user The current logged user.
 */
@Composable
fun ProfileScreen(
    user: User,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("User ID", style = MaterialTheme.typography.h5, color = CustomColor.DarkBrown.value)
        Text("${user.id}", style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(16.dp))
        Text("Username", style = MaterialTheme.typography.h5, color = CustomColor.DarkBrown.value)
        Text(user.username.value, style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(16.dp))
        Text("Email", style = MaterialTheme.typography.h5, color = CustomColor.DarkBrown.value)
        Text(user.email.value, style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
    }
}
