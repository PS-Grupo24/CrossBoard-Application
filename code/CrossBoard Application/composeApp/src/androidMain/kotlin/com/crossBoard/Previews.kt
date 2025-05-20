@file:OptIn(ExperimentalMaterialApi::class)

package com.crossBoard
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.crossBoard.domain.*
import com.crossBoard.model.AuthState
import com.crossBoard.ui.MainMenu
import com.crossBoard.ui.screens.*
import com.crossBoard.ui.viewModel.MultiplayerMatchViewModel
import com.crossBoard.utils.createHttpClient
import io.ktor.client.engine.okhttp.*


@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewFindMatchScreenPreview() {
    val client = ApiClient(createHttpClient(OkHttp.create()), getHost())
    val vm = remember { MultiplayerMatchViewModel(client, "ops", 1) }
    val matchUiState by vm.matchState.collectAsState()
    FindMatchScreen(matchUiState.gameTypeInput,vm::updateGameTypeInput, vm::findMatch, matchUiState.isLoading, matchUiState.errorMessage)
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen({}, {})
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    val client = ApiClient(createHttpClient(OkHttp.create()), getHost())
    MainMenu(client,
        User(1, Username("Ruben"), Email("Ruben@gmail.com"), Password("Ruben"), Token("123")),
        { }
    )
}

@Preview
@Composable
fun PreviewWaitingMatchScreen() {
    WaitingScreen(errorMessage = null, onCancelClick = {}, )
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

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    GameScreen(
        MultiPlayerMatch.startGame(1, MatchType.TicTacToe).join(2),
        1,
        "Ruben",
        "Luis",
        {row, col -> },
        {},
        false,
        null,
        {},
        30
    )
}