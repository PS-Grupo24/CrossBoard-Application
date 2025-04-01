package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import domain.Player
import domain.TicTacToeBoard
import httpModel.BoardOutput

@Composable
fun ticTacToeBoardView(
    board: BoardOutput,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
){
    Column(modifier = modifier) {
        (0..<TicTacToeBoard.BOARD_DIM).forEach { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                (0..<TicTacToeBoard.BOARD_DIM).forEach { colIndex ->
                    val positionIndex = rowIndex * TicTacToeBoard.BOARD_DIM + colIndex
                    val playerSymbol = when(board.positions[positionIndex]){
                        Player.WHITE.toString() -> "W"
                        Player.BLACK.toString() -> "B"
                        else -> null
                    }

                    Cell(
                        playerSymbol = playerSymbol,
                        onClick = { onCellClick(rowIndex, colIndex) },
                        enabled = enabled && playerSymbol == null,
                    )
                }
            }
        }
    }
}