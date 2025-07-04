package com.crossBoard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.model.AdminState
import com.crossBoard.ui.viewModel.AdminViewModel
import com.crossBoard.utils.CustomColor

/**
 * Screen responsible for the display and element interactions for the admin panel.
 * @param adminState The admin state to display.
 * @param adminViewModel The viewModel with the functionalities for interactions.
 */
@Composable
fun AdminPanelScreen(
    adminState: AdminState,
    adminViewModel:AdminViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Admin Panel", style = MaterialTheme.typography.h4, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = adminState.searchQuery,
                onValueChange = adminViewModel::updateSearchQuery,
                label = { Text("Search Username") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = CustomColor.LightBrown.value,
                    focusedBorderColor = CustomColor.DarkBrown.value,
                    unfocusedLabelColor = CustomColor.LightBrown.value,
                    focusedLabelColor = CustomColor.DarkBrown.value,
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = adminViewModel::performSearch,
                enabled = !adminState.isSearching,
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.LightBrown.value)
            ) {
                if (adminState.isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colors.onPrimary
                    )
                } else {
                    Text("Search", color = Color.White)
                }
            }
        }

        if (adminState.isSearching) {
            Spacer(Modifier.height(8.dp))
            Text("Searching...", style = MaterialTheme.typography.body2)
        } else {
            adminState.searchError?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        if (adminState.searchResults.isNotEmpty()) {
            Text("Search Results:", style = MaterialTheme.typography.h6, color = CustomColor.DarkBrown.value)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Gray),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(adminState.searchResults, key = { it.id }) { user ->
                    UserSearchResultItem(
                        user = user,
                        isSelected = user.id == adminState.selectedUser?.id,
                        onClick = {
                            if ( user == adminViewModel.adminState.value.selectedUser) adminViewModel.selectUser(null)
                            else adminViewModel.selectUser(user)
                        }
                    )
                }
            }
        }

        adminState.selectedUser?.let { selectedUser ->
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
            Text("Selected User:", style = MaterialTheme.typography.h6, color = CustomColor.DarkBrown.value)
            Spacer(Modifier.height(8.dp))

            SelectedUserDetails(user = selectedUser)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedUser.state == UserState.BANNED.name) {
                    Button(
                        onClick = adminViewModel::unbanSelectedUser,
                        enabled = !adminState.isModifyingUser,
                        colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
                    ) { Text("Unban User", color = Color.White) }
                } else {
                    Button(
                        onClick = adminViewModel::banSelectedUser,
                        enabled = !adminState.isModifyingUser,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) { Text("Ban User") }
                }

                if (adminState.isModifyingUser) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }

            adminState.modifyUserSuccess?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = CustomColor.DarkBrown.value, style = MaterialTheme.typography.body1)
            }
            adminState.modifyUserError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colors.error)
            }
        }
    }
}

/**
 * Auxiliary function responsible for displaying each found user.
 * @param user The user to display.
 * @param isSelected A flag to indicate if the user is highlighted.
 * @param onClick The action to perform when this element is clicked.
 */
@Composable
fun UserSearchResultItem(
    user: UserInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .background(color = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(user.username.value, style = MaterialTheme.typography.body1)
            Text(user.state, color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
        }
    }
}

/**
 * Auxiliary function responsible for displaying the profile information of a user.
 * @param user The user to display.
 */
@Composable
fun SelectedUserDetails(user: UserInfo) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Username: ${user.username.value}", style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(4.dp))
        Text("Email: ${user.email.value}", style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(4.dp))
        Text("User ID: ${user.id}", style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (user.state == UserState.BANNED.name) "Status: Banned" else "Status: Active",
            color = if (user.state == UserState.BANNED.name) MaterialTheme.colors.error else CustomColor.LightBrown.value,
        )
    }
}