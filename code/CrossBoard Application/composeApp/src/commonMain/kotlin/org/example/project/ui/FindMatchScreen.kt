package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.MatchType

@ExperimentalMaterialApi
@Composable
fun FindMatchScreen(
    selectedGameTypeValue: String,
    onGameTypeChange: (String) -> Unit,
    onFindMatchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
){
    var expanded by remember { mutableStateOf(false) }
    val gameTypes = remember { MatchType.entries.toTypedArray() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Select Game Type", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (!isLoading) { expanded = !expanded } },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = gameTypes.find { it.value == selectedGameTypeValue }?.name ?: "Select...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Game Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = errorMessage != null,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                gameTypes.forEach { selectionOption: MatchType ->
                    DropdownMenuItem(
                        onClick = {
                            onGameTypeChange(selectionOption.value)
                            expanded = false
                        },
                        enabled = !isLoading
                    ) {
                        Text(text = selectionOption.name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFindMatchClick,
            enabled = !isLoading && selectedGameTypeValue.isNotBlank()
        ) {
            if(isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.onPrimary
                )
            }
            else {
                Text("Find Match")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoading) {
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
}