package com.crossBoard.ui.uiModule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.position.ReversiPosition
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
        val module = ModuleProvider.getModule(matchType)
        val mySymbol = if (myPlayerType == Player.BLACK) blackResource else whiteResource

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.White),
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

            Column(
                modifier = Modifier.size(boardSize).background(Color.Black),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(modifier = Modifier.width(boardSize).height(squareSize / 4).background(Color.DarkGray).padding(start = squareSize / 4),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    repeat(ReversiBoard.BOARD_DIM) {
                        Text("${'A' + it}", color = Color.White, fontSize = boardTextSize)
                    }
                }
                repeat(ReversiBoard.BOARD_DIM) { row ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start) {
                        Text("${row + 1}", modifier = Modifier.background(Color.DarkGray).width(squareSize / 4).height(squareSize + 1.dp).padding(top = 23.dp, start = 4.dp), color = Color.White, fontSize = boardTextSize)
                        repeat(ReversiBoard.BOARD_DIM) {col ->
                            val square = Square(Row(row, ReversiBoard.BOARD_DIM), Column('a' + col))
                            squareView(square, board) { onMakeMove(ReversiMove(myPlayerType, module.getSquare(row, col)))}
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable function "squareView" that displays a square in the Reversi board.
 * @param square The square to display.
 * @param board The Reversi board containing the square and the information of the square to represent.
 * @param onClick Callback function to handle square clicks.
 */
@Composable
fun squareView(
    square: Square,
    board: Board,
    onClick: () -> Unit = {}
) {
    val player = board.get(square)

    if(player == Player.EMPTY) {
        // If the square is empty, display a clickable box with a green background and black border.
        Box(modifier = Modifier.size(squareSize).background(Color.Green).border(lineSize, Color.Black).clickable(onClick = onClick))
    } else {
        // If the square is occupied, display a box with the player's symbol.
        Box(modifier = Modifier.size(squareSize).background(Color.Green).border(lineSize, Color.Black),
            contentAlignment = Alignment.Center,
        ) {

            val symbol = when(player) {
                Player.WHITE -> whiteResource
                Player.BLACK -> blackResource
                else -> throw IllegalArgumentException("Invalid player type: $player")
            }
            Image(
                painter = painterResource(symbol),
                contentDescription = "Player Piece",
                modifier = Modifier.size(squareSize)
            )
        }
    }
}