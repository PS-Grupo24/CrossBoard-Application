package org.example.project.model

interface Board {
    val positions: List<Position>;
    val moves: List<Move>
    val turn: Player
    fun play(player: Player, square: Square): Board
    fun forfeit(player: Player): Board
    fun get(square: Square): Player?
}

