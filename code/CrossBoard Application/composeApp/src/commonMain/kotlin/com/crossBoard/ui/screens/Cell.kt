package com.crossBoard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.crossBoard.utils.CustomColor
@Composable
fun Cell(
    rowIndex: Int,
    colIndex: Int,
    playerSymbol: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    player1Symbol: String
){
    val borderWidth = 8.dp
    val borderColor = CustomColor.DarkBrown.value
    Box(
        modifier = modifier
        .size(90.dp)
        .drawBehind {
            val width = size.width
            val height = size.height

            if (colIndex < 2) {
                drawLine(
                    color = borderColor,
                    start = Offset(width, 0f),
                    end = Offset(width, height),
                    strokeWidth = borderWidth.toPx(),
                )
            }
            if (rowIndex < 2) {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = borderWidth.toPx()
                )
            }
        }
        .clickable(
            enabled = enabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ){
            onClick()
        }
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