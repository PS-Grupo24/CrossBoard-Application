package com.crossBoard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Cell(
    playerSymbol: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    player1Symbol: String
){
    Box(
        modifier = modifier
        .size(90.dp)
        .border(BorderStroke(1.dp, Color.Black))
        .clickable(enabled = enabled){onClick()}
        .padding(8.dp),
        contentAlignment = Alignment.Center
    ){
        if (playerSymbol != null){
            Text(
                text = playerSymbol,
                style = TextStyle(
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(playerSymbol == player1Symbol) Color.Red else Color.Black
                )
            )
        }
    }
}