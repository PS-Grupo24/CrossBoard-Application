import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.Player
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.ui.screens.Cell
import crossboardapplication.composeapp.generated.resources.Res
import crossboardapplication.composeapp.generated.resources.circleSymbol
import crossboardapplication.composeapp.generated.resources.crossSymbol
import org.jetbrains.compose.resources.painterResource

@Composable
fun ticTacToeBoardView(
    board: Board,
    myPlayerType: Player,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val blackResource = Res.drawable.crossSymbol
    val whiteResource = Res.drawable.circleSymbol

    val mySymbol = if (myPlayerType == Player.BLACK) blackResource else whiteResource

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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

        (0..<TicTacToeBoard.BOARD_DIM).forEach { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (0..<TicTacToeBoard.BOARD_DIM).forEach { colIndex ->
                    val positionIndex = rowIndex * TicTacToeBoard.BOARD_DIM + colIndex
                    val position = board.positions[positionIndex] as TicPosition
                    val symbol = when (position.player) {
                        Player.EMPTY -> null
                        Player.BLACK -> blackResource
                        Player.WHITE -> whiteResource
                    }
                    Cell(
                        rowIndex = rowIndex,
                        colIndex = colIndex,
                        symbol = symbol,
                        onClick = { onCellClick(rowIndex, colIndex) },
                        enabled = enabled && symbol == null,
                    )
                }
            }
        }
    }
}
