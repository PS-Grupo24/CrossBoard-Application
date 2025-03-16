package org.example.project.model

interface Board {
    val positions: List<Position>;
    val moves: List<Move>
    val player1: Player
    val player2: Player
    val turn: Player
    fun play(player: Player, row: Int, column: Char): Board
    fun forfeit(player: Player): Board
    fun get(square: Square): Player?
}

