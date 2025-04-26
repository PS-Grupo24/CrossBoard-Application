package crossBoard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import crossBoard.ApiClient
import crossBoard.viewModel.AuthViewModel
import crossBoard.viewModel.TicTacToeViewModel
@Composable
fun ticTacToeApp(client: ApiClient){
    val authViewModel = remember { AuthViewModel(client) }
    val gameViewModel = remember { TicTacToeViewModel(client) }
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clear()
            gameViewModel.clear()
        }
    }

    val authState by authViewModel.authState.collectAsState()
    val matchState by gameViewModel.matchState.collectAsState()

    if (authState.isAuthenticated) {

        val userToken = authState.userToken
        val currentUserId = authState.currentUser?.id

        if (userToken != null && currentUserId != null){
            LoggedInContent(
                matchState = matchState,
                currentUserId = currentUserId,

                onGameTypeChange = gameViewModel::updateGameTypeInput,
                onFindMatch = {
                    gameViewModel.findMatch(userToken)
                },
                onMakeMove = { row, col ->
                    gameViewModel.makeMove(currentUserId, userToken, row, col)
                },
                onForfeit = {
                    gameViewModel.forfeit(currentUserId, userToken)
                },
                onResetMatch = gameViewModel::resetMatch,
                onLogout = authViewModel::logout,


            )
        }
        else{
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Authentication state inconsistent. Please logout.")
                Spacer(Modifier.height(20.dp))
                Button(onClick = { authViewModel.logout() }) { Text("Logout") }
            }
        }

    }
    else{
        AuthenticationScreen(
            authState = authState,

            onLoginUsernameChange = authViewModel::updateLoginUsername,
            onLoginPasswordChange = authViewModel::updateLoginPassword,

            onRegisterEmailChange = authViewModel::updateRegisterEmail,
            onRegisterPasswordChange = authViewModel::updateRegisterPassword,
            onRegisterUsernameChange = authViewModel::updateRegisterUsername,

            onLoginClick = authViewModel::login,
            onRegisterClick = authViewModel::register,
            onSwitchScreen = authViewModel::showLoginScreen
            )
    }
}