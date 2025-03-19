package domain

/**
 * Enum class "Difficulty" represents the difficulty of the game.
 * @param char the character representation of the difficulty.
 * @property EASY the easy difficulty.
 * @property MEDIUM the medium difficulty.
 * @property HARD the hard difficulty.
 */
enum class Difficulty(char: Char) {
    EASY('e'),
    MEDIUM('m'),
    HARD('h');
}