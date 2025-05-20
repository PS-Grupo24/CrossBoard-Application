package com.crossBoard.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.crossBoard.domain.User
import com.crossBoard.ui.screens.FindMatchScreen
import com.crossBoard.ui.screens.SinglePlayerMatchScreen
import com.crossBoard.ui.viewModel.SinglePlayerViewModel
import com.crossBoard.utils.CustomColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SinglePlayerMatch(
    user: User?,
    ongoBack: () -> Unit
){
    val vm = remember { SinglePlayerViewModel() }
    val singleMatchState = vm.singlePlayerMatch.collectAsState()

    DisposableEffect(Unit){
        onDispose {
            vm.clear()
        }
    }
    Scaffold(
        topBar = {
            if (user == null){
                TopAppBar(
                    title = {
                        Text("Single Player Match", color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = ongoBack,
                        ){
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    backgroundColor = CustomColor.DarkBrown.value
                )
            }
        }
    ){
        val match = singleMatchState.value.match
        val player = singleMatchState.value.player
        if (match == null || player == null){
            FindMatchScreen(
                selectedGameTypeValue = singleMatchState.value.matchTypeInput,
                onGameTypeChange = vm::updateGameTypeInput,
                onFindMatchClick = vm::startGame,
                isLoading = false,
                errorMessage = singleMatchState.value.errorMessage,
                buttonMessage = "Start Game"
            )
        }
        else{
            SinglePlayerMatchScreen(
                user,
                match = match,
                player = player,
                errorMessage = singleMatchState.value.errorMessage,
                onMakeMove = vm::makeMove,
                onForfeit = vm::forfeit,
                onPlayAgain = vm::startGame,
                onGoBack = vm::stopMatch)
        }

    }
}