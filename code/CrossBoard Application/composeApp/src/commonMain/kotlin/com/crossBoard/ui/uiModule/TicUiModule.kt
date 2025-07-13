package com.crossBoard.ui.uiModule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.getSquare
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.utils.CustomColor
import crossboardapplication.composeapp.generated.resources.Res
import crossboardapplication.composeapp.generated.resources.circleSymbol
import crossboardapplication.composeapp.generated.resources.crossSymbol
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

class TicUiModule : UiModule<TicTacToeBoard, TicTacToeMove> {
    override val matchType: MatchType
        get() = MatchType.TicTacToe

    override fun generateRandomMachineMove(board: TicTacToeBoard, machinePlayerType: Player): TicTacToeMove {
        val position = board.positions.filter { it .player == Player.EMPTY }.random()
        return TicTacToeMove(
            machinePlayerType,
            position.square
        )
    }

    @Composable
    override fun BoardView(
        board: TicTacToeBoard,
        myPlayerType: Player,
        onMakeMove: (move: TicTacToeMove) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val blackResource = Res.drawable.crossSymbol
        val whiteResource = Res.drawable.circleSymbol
        val mySymbol = if (myPlayerType == Player.BLACK) blackResource else whiteResource

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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val boardSize = minOf(maxWidth, maxHeight)

                Column(modifier = Modifier.size(boardSize)) {
                    repeat(TicTacToeBoard.BOARD_DIM) { rowIndex ->
                        Row(modifier = Modifier.weight(1f)) {
                            repeat(TicTacToeBoard.BOARD_DIM) { colIndex ->
                                val position = board.positions[rowIndex * TicTacToeBoard.BOARD_DIM + colIndex]
                                val symbol = when (position.player) {
                                    Player.EMPTY -> null
                                    Player.BLACK -> blackResource
                                    Player.WHITE -> whiteResource
                                }

                                Cell(
                                    rowIndex = rowIndex,
                                    colIndex = colIndex,
                                    symbol = symbol,
                                    onClick = { onMakeMove(TicTacToeMove(myPlayerType, getSquare(rowIndex, colIndex, TicTacToeBoard.BOARD_DIM))) },
                                    enabled = enabled && symbol == null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Responsible for displaying the information of each cell.
 * @param rowIndex The row index of this cell.
 * @param colIndex The column index of this cell.
 * @param symbol The symbol of the player for this cell.
 * @param onClick The action to perform when this cell is clicked.
 * @param modifier The modifier elements.
 * @param enabled The flag indicating if this cell is clickable or not.
 */
@Composable
fun Cell(
    rowIndex: Int,
    colIndex: Int,
    symbol: DrawableResource?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
){
    val borderWidth = 8.dp
    val borderColor = CustomColor.DarkBrown.value
    Box(
        modifier = modifier
            .size(90.dp)
            .drawBehind {
                val width = size.width
                val height = size.height
                if (colIndex < 2) {
                    drawLine(
                        color = borderColor,
                        start = Offset(width, 0f),
                        end = Offset(width, height),
                        strokeWidth = borderWidth.toPx(),
                    )
                }
                if (rowIndex < 2) {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = borderWidth.toPx()
                    )
                }
            }
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){
                onClick()
            }
            .padding(8.dp),

        contentAlignment = Alignment.Center
    ){
        if (symbol != null){
            Image(
                painter = painterResource(symbol),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}