package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crossBoard.model.UserInfoState

@Composable
fun ProfileScreen(
    user: UserInfoState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Profile", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(32.dp))
        Text("User ID: ${user.id}", style = MaterialTheme.typography.body1)
        Text("Username: ${user.username}", style = MaterialTheme.typography.body1)
        Text("Email: ${user.email}", style = MaterialTheme.typography.body1)
    }
}