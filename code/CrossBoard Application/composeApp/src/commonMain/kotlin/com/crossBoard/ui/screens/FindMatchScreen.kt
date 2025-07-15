package com.crossBoard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.MatchType
import com.crossBoard.utils.CustomColor

/**
 * A polished screen for finding a game match. Features a clear layout,
 * an enhanced action button, and better user feedback during loading states.
 *
 * @param selectedGameTypeValue The current selected match type.
 * @param onGameTypeChange The action to perform when a new match type is selected.
 * @param onFindMatchClick The action to perform when the find match button is clicked.
 * @param isLoading The current Loading state.
 * @param errorMessage The current error message; `NULL` if there is no error message.
 * @param buttonMessage A dynamic message for the search button (not used in this version, but kept for API compatibility).
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FindMatchScreen(
    selectedGameTypeValue: String,
    onGameTypeChange: (String) -> Unit,
    onFindMatchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    buttonMessage: String = "Find Match",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = CustomColor.DarkBrown.value.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Ready for a Challenge?",
            style = MaterialTheme.typography.h5,
            color = CustomColor.DarkBrown.value
        )
        Text(
            "Select a match type to begin your search.",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(32.dp))

        GameTypeDropdown(
            selectedValue = selectedGameTypeValue,
            onValueChange = onGameTypeChange,
            isLoading = isLoading,
            isError = errorMessage != null
        )

        Spacer(Modifier.height(24.dp))

        FindMatchButton(
            onClick = onFindMatchClick,
            isLoading = isLoading,
            isEnabled = !isLoading && selectedGameTypeValue.isNotBlank()
        )

        Spacer(Modifier.height(24.dp))

        AnimatedVisibility(
            visible = !isLoading && errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

/**
 * A self-contained, styled dropdown menu for selecting game types.
 */
@ExperimentalMaterialApi
@Composable
private fun GameTypeDropdown(
    selectedValue: String,
    onValueChange: (String) -> Unit,
    isLoading: Boolean,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val gameTypes = remember { MatchType.availableTypes.toTypedArray() }
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedBorderColor = CustomColor.LightBrown.value,
        focusedBorderColor = CustomColor.DarkBrown.value,
        unfocusedLabelColor = CustomColor.LightBrown.value,
        focusedLabelColor = CustomColor.DarkBrown.value,
        textColor = CustomColor.DarkBrown.value,
        trailingIconColor = CustomColor.DarkBrown.value,
        backgroundColor = CustomColor.LightBrown.value.copy(alpha = 0.1f)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!isLoading) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = gameTypes.find { it.toString() == selectedValue }?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Game Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = textFieldColors,
            shape = MaterialTheme.shapes.medium
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            gameTypes.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(selectionOption.toString())
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption.name)
                }
            }
        }
    }
}

/**
 * A primary action button that shows a loading state.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FindMatchButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    isEnabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 8.dp),

        colors = ButtonDefaults.buttonColors(
            backgroundColor = CustomColor.LightBrown.value,
            contentColor = Color.White,
            disabledBackgroundColor = CustomColor.LightBrown.value.copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn() with fadeOut() }
        ) { isCurrentlyLoading ->
            if (isCurrentlyLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                    Text("Searching...")
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Find Match Icon",
                    )
                    Text("Find Match")
                }
            }
        }
    }
}