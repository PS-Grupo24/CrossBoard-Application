package crossBoard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onFindMatchClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Main Menu", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onFindMatchClicked, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Find Match")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onProfileClicked, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("View Profile")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogoutClicked, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Logout")
        }
    }
}