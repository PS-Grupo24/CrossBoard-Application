package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import httpModel.MatchOutput
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Failure
import util.Success


@Composable
@Preview
fun App(client: MatchClient) {
    MaterialTheme {
        var match by remember { mutableStateOf<MatchOutput?>(null) }

        var userId by remember { mutableStateOf("") }

        var gameType by remember { mutableStateOf("") }

        var isLoading by remember { mutableStateOf(false) }

        var errorMessage by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ){
                TextField(
                    value = userId,
                    onValueChange = { userId = it },
                    placeholder = { Text("User id") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
                TextField(
                    value = gameType,
                    onValueChange = { gameType = it },
                    placeholder = { Text("Game type") },
                    modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                )
                Button(onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        val request = client.enterMatch(userId.toInt(), gameType)

                        when (request) {
                            is Success -> match = request.value
                            is Failure -> errorMessage = request.value
                        }
                        isLoading = false
                    }
                }) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(15.dp),
                            strokeWidth = 1.dp,
                            color = Color.White
                        )
                    }
                    else{
                        Text("Find Match")
                    }
                }
                errorMessage?.let {
                    Text(
                        color = Color.Red,
                        text = it
                    )
                }
            }
    }

}