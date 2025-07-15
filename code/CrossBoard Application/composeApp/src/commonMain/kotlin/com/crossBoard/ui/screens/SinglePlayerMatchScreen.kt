package com.crossBoard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.crossBoard.domain.*
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.move.Move
import com.crossBoard.model.PlayerInfo
import com.crossBoard.model.SinglePlayerMatch
import com.crossBoard.ui.components.MyAlertDialog
import com.crossBoard.ui.uiModule.UiModuleProvider

/**
 * A polished, robust screen for a single-player match against the machine.
 * Uses a Scaffold for a stable layout and an overlay for a clear game-over state.
 *
 * @param user The current logged user; `NULL` for an anonymous user.
 * @param match The current single player match data.
 * @param player The player type of the human user.
 * @param errorMessage An optional error message to display.
 * @param onMakeMove Callback when a move is made.
 * @param onForfeit Callback when the forfeit button is clicked.
 * @param onPlayAgain Callback for the "Play Again" button.
 * @param onGoBack Callback to navigate back to the previous screen.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SinglePlayerMatchScreen(
    user: User?,
    match: SinglePlayerMatch,
    player: Player,
    errorMessage: String?,
    onMakeMove: (Move) -> Unit,
    onForfeit: () -> Unit,
    onPlayAgain: () -> Unit,
    onGoBack: () -> Unit,
) {
    val board = match.board
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW

    BackHandler {
        onGoBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val humanPlayerInfo = PlayerInfo(user?.id, user?.username?.value ?: "Player")
                val machinePlayerInfo = PlayerInfo(null, "Machine")

                PlayerInfoPanel(
                    boardTurn = board.turn,
                    player1 = humanPlayerInfo,
                    player2 =machinePlayerInfo,
                    timeLeft = null,
                    myPlayerType = player,
                    player1Type = board.player1,
                    player2Type = board.player2,
                )
            },
            bottomBar = {
                if (!isGameOver) {
                    SinglePlayerActions(
                        errorMessage = errorMessage,
                        onForfeitClick = onForfeit
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game Status Text
                val turnString = if (board.turn == player) "Your Turn" else "Machine's Turn"
                val status = when {
                    !isGameOver -> turnString
                    board is BoardWin && board.winner == player -> "You Won!"
                    board is BoardWin -> "You Lost!"
                    else -> "It's a Draw!"
                }
                GameStatusText(status)

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BoxWithConstraints {
                        val isMobile = maxWidth < 600.dp
                        val horizontalPadding = if (isMobile) 8.dp else 16.dp

                        val availableWidth = maxWidth - (horizontalPadding * 2)
                        val availableHeight = maxHeight
                        val side = min(availableWidth, availableHeight).coerceAtMost(500.dp)

                        Box(modifier = Modifier.size(side)) {
                            val module = UiModuleProvider.getModule<Board, Move>(match.matchType)
                            module.BoardView(
                                board = board,
                                myPlayerType = player,
                                onMakeMove = onMakeMove,
                                enabled = !isGameOver && board.turn == player,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            val resultText = when {
                board is BoardWin && board.winner == player -> "Victory!"
                board is BoardWin -> "Defeat"
                else -> "Draw"
            }
            GameOverOverlay(
                resultText = resultText,
                onPlayAgainClick = onPlayAgain,
                onExitClick = onGoBack
            )
        }
    }
}

/**
 * A bottom bar for single-player game actions.
 */
@Composable
fun SinglePlayerActions(
    errorMessage: String?,
    onForfeitClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        MyAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = "Confirm Forfeit",
            text = "Are you sure you want to end the game?",
            onConfirm = {
                showConfirmDialog = false
                onForfeitClick()
            },
            confirmText = "Yes",
            dismissText = "Cancel",
            onDismiss = { showConfirmDialog = false }
        )
    }

    Surface(elevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showConfirmDialog = true },
                border = BorderStroke(1.dp, MaterialTheme.colors.error),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Text("End Game")
            }
        }
    }
}