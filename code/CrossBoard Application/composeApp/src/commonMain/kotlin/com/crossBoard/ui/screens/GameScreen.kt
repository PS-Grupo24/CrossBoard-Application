package com.crossBoard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.crossBoard.domain.*
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.move.Move
import com.crossBoard.model.PlayerInfo
import com.crossBoard.ui.components.MyAlertDialog
import com.crossBoard.ui.uiModule.UiModuleProvider
import com.crossBoard.utils.CustomColor

/**
 * A polished, robust screen for displaying an ongoing or ended match.
 * Uses a Scaffold for a stable layout and an overlay for a clear game-over state.
 *
 * @param match The current match data.
 * @param currentUserId The ID of the human user viewing the screen.
 * @param player1Username The username for Player 1.
 * @param player2Username The username for Player 2.
 * @param onMakeMove Callback when a move is made.
 * @param onForfeitClick Callback when the forfeit button is clicked.
 * @param isLoading A flag indicating if an operation is in progress.
 * @param errorMessage An optional error message to display.
 * @param onPlayAgainClick Callback for the "Play Again" button.
 * @param timeLeft The time remaining in the current turn.
 * @param onBack Callback to navigate away from the screen after the game is over.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    match: MultiPlayerMatch,
    currentUserId: Int,
    player1Username: String,
    player2Username: String,
    onMakeMove: (move: Move) -> Unit,
    onForfeitClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPlayAgainClick: () -> Unit,
    timeLeft: Int?,
    onBack: () -> Unit,
    onMatchOver: () -> Unit,
) {
    val board = match.board
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW
    val myPlayerType = remember(match.user1) { match.getPlayerType(currentUserId) }

    BackHandler(enabled = isGameOver) {
        onBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                PlayerInfoPanel(
                    boardTurn = board.turn,
                    player1 = PlayerInfo(match.user1, player1Username),
                    player2 = PlayerInfo(match.user2, player2Username),
                    timeLeft = timeLeft,
                    myPlayerType = myPlayerType,
                    player1Type = board.player1,
                    player2Type = board.player2,
                )
            },
            bottomBar = {
                if (!isGameOver) {
                    GameActions(
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onForfeitClick = onForfeitClick
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
                val turnString = if (board.turn == myPlayerType) "Your Turn" else "Opponent's Turn"
                val status = when {
                    !isGameOver -> turnString
                    board is BoardWin && board.winner == myPlayerType -> "You Won!"
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
                                myPlayerType = myPlayerType,
                                onMakeMove = onMakeMove,
                                enabled = !isGameOver && board.turn == myPlayerType,
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
            onMatchOver()
            val resultText = when {
                board is BoardWin && board.winner == myPlayerType -> "Victory!"
                board is BoardWin -> "Defeat"
                else -> "Draw"
            }
            GameOverOverlay(
                resultText = resultText,
                onPlayAgainClick = onPlayAgainClick,
                onExitClick = onBack
            )
        }
    }
}

/**
 * A TopAppBar that displays info for both players, the turn timer, and highlights
 * the player whose turn it currently is.
 */
@Composable
fun PlayerInfoPanel(
    boardTurn: Player,
    player1: PlayerInfo,
    player2: PlayerInfo,
    timeLeft: Int?,
    myPlayerType: Player,
    player1Type: Player,
    player2Type: Player,
) {
    TopAppBar(
        backgroundColor = CustomColor.DarkBrown.value,
        contentColor = Color.White,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerInfoCard(
                playerInfo = player1,
                isActiveTurn = boardTurn == player1Type,
                isMe = myPlayerType == player1Type,
            )
            if (timeLeft != null && timeLeft > 0) {
                Text(
                    text = timeLeft.toString(),
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }
            PlayerInfoCard(
                playerInfo = player2,
                isActiveTurn = boardTurn == player2Type,
                isMe = myPlayerType == player2Type,
            )
        }
    }
}

/**
 * A styled card representing a single player in the game.
 */
@Composable
fun PlayerInfoCard(
    playerInfo: PlayerInfo,
    isActiveTurn: Boolean,
    isMe: Boolean
) {
    val shape = RoundedCornerShape(12.dp)
    val border = if (isActiveTurn) BorderStroke(2.dp, Color.White) else null

    Card(
        shape = shape,
        border = border,
        backgroundColor = CustomColor.LightBrown.value.copy(alpha = if (isActiveTurn) 0.5f else 0.2f),
        elevation = 0.dp
    ) {
        val displayName = playerInfo.username ?: "Waiting..."
        val text = if (isMe) "$displayName (You)" else displayName

        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontWeight = if (isActiveTurn) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * A styled "chip" for displaying the current game status (e.g., "Your Turn").
 */
@Composable
fun GameStatusText(status: String) {
    Surface(
        shape = CircleShape,
        color = CustomColor.LightBrown.value.copy(alpha = 0.15f),
        contentColor = CustomColor.DarkBrown.value
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

/**
 * A bottom bar for in-game actions like forfeiting.
 */
@Composable
fun GameActions(
    isLoading: Boolean,
    errorMessage: String?,
    onForfeitClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        MyAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = "Confirm Forfeit",
            text = "Are you sure you want to forfeit the match?",
            onConfirm = {
                showConfirmDialog = false
                onForfeitClick()
            },
            confirmText = "Yes, Forfeit",
            dismissText = "Cancel",
            onDismiss = {showConfirmDialog = false}
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
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (errorMessage != null) {
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
                enabled = !isLoading,
                border = BorderStroke(1.dp, MaterialTheme.colors.error),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Text("Forfeit Match")
            }
        }
    }
}

/**
 * A full-screen overlay to display the final result of the game.
 */
@Composable
fun GameOverOverlay(
    resultText: String,
    onPlayAgainClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = resultText,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onPlayAgainClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
                ) {
                    Text("Play Again", color = Color.White)
                }
                OutlinedButton(
                    onClick = onExitClick,
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
                ) {
                    Text("Exit", color = Color.White)
                }
            }
        }
    }
}