package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.Player
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.board.TicTacToeBoard

/**
 * The view for the `TicTacToeBoard`.
 * It is responsible for displaying board positions
 * @param board The current board to display.
 * @param onCellClick The action to perform when a `Cell` is clicked.
 * @param modifier The elements modifier.
 * @param enabled The flag indicating if the cells are enabled.
 * @param player1Symbol The symbol to display for the player1.
 * @param player2Symbol The symbol to display for the player2.
 * @param player1Type The type for the player1.
 */
@Composable
fun ticTacToeBoardView(
    board: Board,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    player1Symbol: String,
    player2Symbol: String,
    player1Type: Player
){
    Column(modifier = modifier) {
        (0..<TicTacToeBoard.BOARD_DIM).forEach { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                (0..<TicTacToeBoard.BOARD_DIM).forEach { colIndex ->
                    val positionIndex = rowIndex * TicTacToeBoard.BOARD_DIM + colIndex
                    val playerSymbol = when((board.positions[positionIndex] as TicPosition).player){
                        player1Type -> player1Symbol
                        player1Type.other() -> player2Symbol
                        else -> null
                    }

                    Cell(
                        rowIndex = rowIndex,
                        colIndex = colIndex,
                        playerSymbol = playerSymbol,
                        onClick = { onCellClick(rowIndex, colIndex) },
                        enabled = enabled && playerSymbol == null,
                        player1Symbol = player1Symbol,
                    )
                }
            }
        }
    }
}