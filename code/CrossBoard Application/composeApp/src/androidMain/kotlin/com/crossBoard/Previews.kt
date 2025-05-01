@file:OptIn(ExperimentalMaterialApi::class)

package com.crossBoard
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.crossBoard.domain.Player
import com.crossBoard.domain.TicTacToeBoardRun
import com.crossBoard.domain.initialTicTacToePositions
import com.crossBoard.model.AuthState
import com.crossBoard.model.UserInfoState
import com.crossBoard.ui.MainMenu
import com.crossBoard.ui.screens.*
import com.crossBoard.ui.viewModel.MatchViewModel
import com.crossBoard.utils.createHttpClient
import io.ktor.client.engine.okhttp.*


@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewFindMatchScreenPreview() {
    val client = ApiClient(createHttpClient(OkHttp.create()), getHost())
    val vm = remember { MatchViewModel(client, "ops", 1) }
    val matchUiState by vm.matchState.collectAsState()
    FindMatchScreen(matchUiState.gameTypeInput,vm::updateGameTypeInput, vm::findMatch, matchUiState.isLoading, matchUiState.errorMessage)
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen({})
}

@Preview
@Composable
fun MainMenuPreview() {
    val client = ApiClient(createHttpClient(OkHttp.create()), getHost())
    MainMenu(client,
        "ops",
        1,
        UserInfoState(1, "String", "String", false, null),
        {},
        {}
    )
}

@Preview
@Composable
fun PreviewWaitingMatchScreen() {
    WaitingScreen(errorMessage = null, onCancelClick = {}, )
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(
        UserInfoState(
            90387409,
            "Alice",
            "Alice@example.com",
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AuthenticationScreen(
        AuthState(),
        onLoginPasswordChange = {},
        onLoginClick = {},
        onLoginUsernameChange = {},
        onSwitchScreen = {},
        onRegisterEmailChange = {},
        onRegisterClick = {},
        onRegisterUsernameChange = {},
        onRegisterPasswordChange = {},
        onMaintainSession = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTicBoard(){
    ticTacToeBoardView(
        TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.BLACK,
            Player.WHITE,
            Player.BLACK,
        ),
        {row, col -> },
        player1Symbol = "X",
        player2Symbol = "O",
        player1Type = Player.WHITE,
    )
}