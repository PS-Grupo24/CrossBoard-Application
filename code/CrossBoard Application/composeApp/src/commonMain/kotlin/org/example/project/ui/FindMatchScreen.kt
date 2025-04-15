package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FindMatchScreen(
    userId: String,
    onUserIdChange: (String) -> Unit,
    gameType: String,
    onGameTypeChange: (String) -> Unit,
    onFindMatchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Enter Match Details", style = MaterialTheme.typography.h5)

        OutlinedTextField(
            value = userId,
            onValueChange = onUserIdChange,
            label = { Text("User id") },
            isError = errorMessage != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = gameType,
            onValueChange = onGameTypeChange,
            label = {
                Text("Game Type")
            },
            isError = errorMessage != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onFindMatchClick, enabled = !isLoading) {
            if(isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.primary
                )
            }
            else {
                Text("Find Match")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let{
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}