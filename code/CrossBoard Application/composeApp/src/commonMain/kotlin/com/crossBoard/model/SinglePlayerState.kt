package com.crossBoard.model

import com.crossBoard.domain.Player

data class SinglePlayerState(
    val match: SinglePlayerMatch? = null,
    val player: Player? = null,
    val matchTypeInput: String = "",
    val errorMessage: String? = null
)