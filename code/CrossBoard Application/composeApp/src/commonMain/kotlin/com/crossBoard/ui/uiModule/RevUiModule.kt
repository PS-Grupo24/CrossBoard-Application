package com.crossBoard.ui.uiModule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.possibleMoves
import com.crossBoard.domain.getSquare
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.utils.CustomColor
import crossboardapplication.composeapp.generated.resources.Res
import crossboardapplication.composeapp.generated.resources.blackPiece
import crossboardapplication.composeapp.generated.resources.whitePiece
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

//Constants representing the size of the squares, lines, the board and text in the board.
val squareSize = 65.dp
val lineSize = 1.dp
val boardSize = squareSize * ReversiBoard.BOARD_DIM + /*lineSize * (BOARD_DIM - 1)*/ (squareSize / 4)
val boardTextSize = 16.sp
val whiteResource: DrawableResource = Res.drawable.whitePiece
val blackResource: DrawableResource = Res.drawable.blackPiece

class RevUiModule : UiModule<ReversiBoard,ReversiMove> {
    override val matchType: MatchType = MatchType.Reversi

    override fun generateRandomMachineMove(board : ReversiBoard, machinePlayerType: Player): ReversiMove {
        val possibleSquares = possibleMoves(board.player2, board.positions)
        val position = possibleSquares.random()
        return ReversiMove(machinePlayerType, position)
    }

    @Composable
    override fun BoardView(
        board: ReversiBoard,
        myPlayerType: Player,
        onMakeMove: (move: ReversiMove) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val mySymbol = if (myPlayerType == Player.WHITE) whiteResource else blackResource
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "You are:")
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(mySymbol),
                    contentDescription = "Your Symbol",
                    modifier = Modifier.size(32.dp)
                )
            }
            BoxWithConstraints(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                val boardSize = minOf(maxWidth, maxHeight)

                Column(modifier = Modifier.size(boardSize)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.1f)
                    ) {
                        Spacer(modifier = Modifier.weight(0.1f))
                        Row(modifier = Modifier.weight(0.9f)) {
                            repeat(ReversiBoard.BOARD_DIM) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${'A' + it}",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.1f)
                        ) {
                            repeat(ReversiBoard.BOARD_DIM) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${it + 1}",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.9f)
                                .background(Color.White)
                        ) {
                            repeat(ReversiBoard.BOARD_DIM) { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    repeat(ReversiBoard.BOARD_DIM) { col ->
                                        Box(modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(1.dp)
                                        ) {
                                            val square = Square(Row(row, ReversiBoard.BOARD_DIM), Column('a' + col))
                                            squareView(square, board) {
                                                onMakeMove(
                                                    ReversiMove(
                                                        myPlayerType,
                                                        getSquare(row, col, ReversiBoard.BOARD_DIM),
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable function "squareView" that displays a square in the Reversi board.
 * It is designed to fill the space provided by its parent container.
 *
 * @param square The square to display.
 * @param board The Reversi board containing the square's state.
 * @param onClick Callback function to handle square clicks.
 */
@Composable
fun squareView(
    square: Square,
    board: Board,
    onClick: () -> Unit = {}
) {
    val player = board.get(square)

    val squareModifier = Modifier
        .fillMaxSize()
        .background(CustomColor.LightBrown.value)

    if (player == Player.EMPTY) {
        Box(
            modifier = squareModifier.clickable(onClick = onClick)
        )
    } else {
        Box(
            modifier = squareModifier,
            contentAlignment = Alignment.Center,
        ) {
            val symbol = when (player) {
                Player.WHITE -> whiteResource
                Player.BLACK -> blackResource
                else -> throw IllegalArgumentException("Invalid player type: $player")
            }
            Image(
                painter = painterResource(symbol),
                contentDescription = "Player Piece",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}