package com.crossBoard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossBoard.domain.Column
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.position.ReversiPosition

//Constants representing the size of the squares, lines, the board and text in the board.
val squareSize = 65.dp
val lineSize = 1.dp
val boardSize = squareSize * ReversiBoard.BOARD_DIM + /*lineSize * (BOARD_DIM - 1)*/ (squareSize / 4)
val boardTextSize = 16.sp

/**
 * Composable function "reversiBoardView" that displays the Reversi board.
 * @param board The Reversi board to display.
 * @param onClick Callback function to handle square clicks in the board.
 * @param modifier Modifier to apply to the board view.
 * @param player1Symbol Symbol for player 1, used to display the player's piece.
 * @param player2Symbol Symbol for player 2, used to display the player's piece.
 * @param player1Type Type of player 1, used to determine the player's type.
 */
@Composable
fun reversiBoardView(
    board: Board,
    onClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    player1Symbol: String,
    player2Symbol: String,
    player1Type: Player
) =
    //Column representing the Reversi Board.
    Column(
        modifier = Modifier.size(boardSize).background(Color.Black),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Row representing the top of the board with column labels.
        Row(modifier = Modifier.width(boardSize).height(squareSize / 4).background(Color.DarkGray).padding(start = squareSize / 4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
            repeat(ReversiBoard.BOARD_DIM) {
                Text("${'A' + it}", color = Color.White, fontSize = boardTextSize)
            }
        }
        //Rows representing the squares of the board.
        repeat(ReversiBoard.BOARD_DIM) { row ->
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start) {
                Text("${row + 1}", modifier = Modifier.background(Color.DarkGray).width(squareSize / 4).height(squareSize + 1.dp).padding(top = 23.dp, start = 4.dp), color = Color.White, fontSize = boardTextSize)
                repeat(ReversiBoard.BOARD_DIM) {col ->
                    val square = Square(Row(row, ReversiBoard.BOARD_DIM), Column('a' + col))
                    squareView(square, board) {onClick(square.row.index, square.column.index)}
                }
            }
        }
    }

/**
 * Composable function "squareView" that displays a square in the Reversi board.
 * @param square The square to display.
 * @param board The Reversi board containing the square and the information of the square to represent.
 * @param modifier Modifier to apply to the square view.
 * @param onClick Callback function to handle square clicks.
 */
@Composable
fun squareView(
    square: Square,
    board: Board,
    //modifier: Modifier = Modifier.size(squareSize).background(Color.Green).border(lineSize, Color.Black),
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
            var fill by remember(board.positions[square.row.index + square.column.index] as ReversiPosition) {mutableStateOf(0.1f)}

            val symbol = when(player) {
                Player.WHITE -> "W"
                Player.BLACK -> "B"
                else -> throw IllegalArgumentException("Invalid player type: $player")
            }
            Text(symbol, style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black))
        }
    }
}

/*
fun blackPiece(): ImageBitmap {
    val sprites: Sprites = Sprites("sprites.png")
    return sprites.get(1, 6)
}

fun whitePiece(): ImageBitmap {
    val sprites: Sprites = Sprites("sprites.png")
    return sprites.get(0,6)
}*/