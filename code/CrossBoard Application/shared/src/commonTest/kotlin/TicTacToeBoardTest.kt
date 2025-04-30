import com.crossBoard.domain.initialTicTacToePositions
import kotlin.test.Test

class TicTacToeBoardTest {
    @Test fun `Test initial positions`(){
        val positions = initialTicTacToePositions()
        println(positions)
    }
}