package crossBoard

import androidx.compose.runtime.*
import crossBoard.ui.ticTacToeApp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(client: ApiClient) {
    ticTacToeApp(client)
}