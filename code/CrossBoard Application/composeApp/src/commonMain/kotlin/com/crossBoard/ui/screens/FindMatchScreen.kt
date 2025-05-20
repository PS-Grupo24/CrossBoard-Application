package com.crossBoard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.MatchType
import com.crossBoard.utils.CustomColor


@ExperimentalMaterialApi
@Composable
fun FindMatchScreen(
    selectedGameTypeValue: String,
    onGameTypeChange: (String) -> Unit,
    onFindMatchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    buttonMessage: String = "Find Match",
){
    var expanded by remember { mutableStateOf(false) }
    val gameTypes = remember { MatchType.entries.toTypedArray() }

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedBorderColor = CustomColor.LightBrown.value,
        focusedBorderColor = CustomColor.DarkBrown.value,
        unfocusedLabelColor = CustomColor.LightBrown.value,
        focusedLabelColor = CustomColor.DarkBrown.value,
        textColor = CustomColor.LightBrown.value,
        trailingIconColor = CustomColor.DarkBrown.value,
    )
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Select Game Type", style = MaterialTheme.typography.h5, color = CustomColor.DarkBrown.value)
        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (!isLoading) { expanded = !expanded } },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = gameTypes.find { it.value == selectedGameTypeValue }?.name ?: "Select...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Game Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = errorMessage != null,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = textFieldColors,
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
                        Text(text = selectionOption.name, color = CustomColor.LightBrown.value)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFindMatchClick,
            enabled = !isLoading && selectedGameTypeValue.isNotBlank(),
            colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.LightBrown.value),
        ) {
            if(isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.onPrimary
                )
            }
            else {
                Text(buttonMessage, color = Color.White)
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