package com.crossBoard.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.User
import com.crossBoard.model.MainMenuState
import com.crossBoard.model.MainScreen
import com.crossBoard.model.SubScreen
import com.crossBoard.ui.viewModel.MainMenuViewModel
import com.crossBoard.utils.CustomColor

@Composable
fun TopBar(
    user: User,
    mainMenuState: MainMenuState,
    vm: MainMenuViewModel,
    onLogoutClick: () -> Unit,
) =
    TopAppBar(
        title = {
            Text(
                text = mainMenuState.topBarMessage,
                color = Color.White
            )
        },
        navigationIcon =
            if (mainMenuState.currentMainScreen != MainScreen.MainMenu &&
                mainMenuState.currentSubScreen != SubScreen.Match
            ) {
                {
                    IconButton(
                        onClick = {
                            vm.goToMainMenu(user.username.value)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            } else null,
        actions = {
            if (mainMenuState.currentSubScreen != SubScreen.Match) {
                IconButton(
                    onClick = {
                        vm.goToProfile(user.username.value)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onLogoutClick,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        tint = Color.Black,
                        contentDescription = "Logout",
                    )
                }
            }
        },
        backgroundColor = CustomColor.DarkBrown.value
    )

