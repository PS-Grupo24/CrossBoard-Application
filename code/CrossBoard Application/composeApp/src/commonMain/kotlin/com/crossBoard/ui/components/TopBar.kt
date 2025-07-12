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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.User
import com.crossBoard.model.MainMenuState
import com.crossBoard.model.MainScreen
import com.crossBoard.model.SubScreen
import com.crossBoard.ui.viewModel.MainMenuViewModel
import com.crossBoard.utils.CustomColor

/**
 * Element responsible for the top bar of the application.
 * @param user The current user.
 * @param mainMenuState The current state of the main menu.
 * @param vm The view model for the main menu.
 * @param onLogoutClick The action to perform when the logout button is clicked.
 */
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
                color = Color.White,
                modifier = Modifier.testTag("TopBar Title Text Test")
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
                        },
                        modifier = Modifier.testTag("TopBar Icon Button Test")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.testTag("TopBar Icon Button Icon Test"))
                    }
                }
            } else null,
        actions = {
            if (mainMenuState.currentSubScreen != SubScreen.Match) {
                IconButton(
                    onClick = {
                        vm.goToProfile(user.username.value)
                    },
                    modifier = Modifier.testTag("TopBar Actions Icon Button Test")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black,
                        modifier = Modifier.testTag("TopBar Actions Icon Button Icon Test")
                    )
                }
                Spacer(Modifier.width(8.dp).testTag("TopBar Spacer Test"))
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.testTag("TopBar Logout Icon Button Test")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        tint = Color.Black,
                        contentDescription = "Logout",
                        modifier = Modifier.testTag("TopBar Logout Icon Button Icon Test")
                    )
                }
            }
        },
        backgroundColor = CustomColor.DarkBrown.value,
        modifier = Modifier.testTag("TopBar Test")
    )

