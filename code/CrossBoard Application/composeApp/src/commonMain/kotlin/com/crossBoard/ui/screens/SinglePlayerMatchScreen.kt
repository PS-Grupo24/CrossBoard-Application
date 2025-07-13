package com.crossBoard.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.*
import com.crossBoard.domain.board.*
import com.crossBoard.domain.move.Move
import com.crossBoard.model.PlayerInfo
import com.crossBoard.model.SinglePlayerMatch
import com.crossBoard.ui.uiModule.UiModuleProvider
import com.crossBoard.ui.uiModule.blackResource
import com.crossBoard.ui.uiModule.whiteResource
import com.crossBoard.utils.CustomColor
import org.jetbrains.compose.resources.painterResource


/**
 * Screen responsible for the display of a single match.
 * @param user The current logged user; `NULL` for an anonymous user.
 * @param match The current single player match.
 * @param player The player type of the user.
 * @param errorMessage The current error message; `NULL` when there is no error message.
 * @param onMakeMove The action to perform when making a move.
 * @param onForfeit The action to perform when the forfeit button is clicked.
 * @param onPlayAgain The action to perform when the play again button is clicked.
 */
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
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW
    val userId = user?.id ?: Int.MAX_VALUE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MatchInfoPanel(
            matchId = null,
            currentUserId = userId,
            PlayerInfo(userId, user?.username?.value ?: "Anonymous"),
            PlayerInfo(0, "Machine"),
            timeLeft = null
        )

        Spacer(Modifier.height(16.dp))

        val turnString = if (match.board.turn == player) "Your turn" else "Opponent's turn"
        val status = when (val b = match.board) {
            is BoardWin -> if (b.winner == player) "You Won" else "You Lost"
            else -> if (isGameOver) "Draw" else turnString
        }
        Text(status, style = MaterialTheme.typography.h5, color = CustomColor.LightBrown.value)

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentAlignment = Alignment.Center
        ) {
            val module = UiModuleProvider.getModule<Board, Move>(match.matchType)
            module.BoardView(
                board = match.board,
                myPlayerType = player,
                onMakeMove = onMakeMove,
                enabled = !isGameOver,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        GameActions(
            isLoading = false,
            errorMessage = errorMessage,
            isGameOver = isGameOver,
            onForfeitClick = onForfeit,
            onPlayAgainClick = onPlayAgain,
            onMatchOver = {}
        )
    }
}
