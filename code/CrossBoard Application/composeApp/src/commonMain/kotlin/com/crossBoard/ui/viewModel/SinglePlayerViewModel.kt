package com.crossBoard.ui.viewModel

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.Player
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.possibleMoves
import com.crossBoard.domain.position.Position
import com.crossBoard.domain.position.ReversiPosition
import com.crossBoard.domain.toMatchType
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.SinglePlayerMatch
import com.crossBoard.model.SinglePlayerState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the SinglePlayerMatch resources.
 * Uses `SinglePlayerState` to manage the resources.
 * @param mainDispatcher The coroutine Dispatcher; `Dispatchers.Main` by default.
 */
class SinglePlayerViewModel(
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {

    /**
     * The scope for this viewModel
     */
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _singlePlayerMatch = MutableStateFlow(SinglePlayerState())
    val singlePlayerMatch: StateFlow<SinglePlayerState> = _singlePlayerMatch.asStateFlow()

    /**
     * Function "startMatch" responsible for updating the `SinglePlayerState` with a new Match.
     * If the match starts with the Machine Turn it will generate a randomMove for it after 1 sec.
     */
    fun startMatch(){
        val currentState = _singlePlayerMatch.value

        if (currentState.matchTypeInput.isBlank()){
            _singlePlayerMatch.update { it.copy(errorMessage = "Please enter a valid gameType") }
            return
        }

        viewModelScope.launch {
            val matchType = currentState.matchTypeInput.toMatchType()
            val match = SinglePlayerMatch.startGame(matchType)
            _singlePlayerMatch.update { it.copy(match = match, player = match.board.player1, errorMessage = null) }

            if (match.board.turn == match.board.player2){
                delay(1000L)
                randomMachineMove()
            }
        }
    }

    /**
     * Function "makeMove" that performs a move to the match.
     * After a move is made and the turn changes to the machine, it will generate a random machine move after 1 sec.
     * @param move The move to be made in the match.
     */
    fun makeMove(move: Move){
        try {
            val currentState = _singlePlayerMatch.value
            val match = currentState.match
            if (match == null) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is not started!") }
                return
            }
            if (match.state != MatchState.RUNNING) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is over!") }
                return
            }
            val newMatch = match.makeMove(move)
            viewModelScope.launch {
                _singlePlayerMatch.update{
                    it.copy(match = newMatch, errorMessage = null)
                }
                if (
                    newMatch.state == MatchState.RUNNING
                    && newMatch.board.turn == newMatch.board.player2
                    ){
                    delay(1000L)
                    randomMachineMove()
                }
            }


        }
        catch (e: Exception){
            _singlePlayerMatch.update { it.copy(errorMessage = e.message ?: e.cause?.message) }
            return
        }
    }

    /**
     * Function "forfeit" performs a forfeit on the `SinglePlayerMatch`
     */
    fun forfeit(){
        try {
            val currentState = _singlePlayerMatch.value
            val match = currentState.match
            val player = currentState.player
            if (match == null) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is not started!") }
                return
            }
            if (match.state != MatchState.RUNNING) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is over!") }
                return
            }
            if (player == null) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Player is not set!") }
                return
            }
            _singlePlayerMatch.update { it.copy(match = match.forfeit(player)) }
        }
        catch (e: Exception){
            _singlePlayerMatch.update { it.copy(errorMessage = e.message ?: e.cause?.message) }
            return
        }

    }

    /**
     * Function "stopMatch" used to clear the match resources.
     */
    fun stopMatch(){
        _singlePlayerMatch.update { it.copy(match = null, player = null, errorMessage = null) }
    }

    /**
     * Private Auxiliary Function "randomMachineMove" responsible for generating a random move for the machine.
     */
    private fun randomMachineMove(){
        try {
            val currentState = _singlePlayerMatch.value
            val match = currentState.match
            if (match == null) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is not started!") }
                return
            }
            if (match.state != MatchState.RUNNING) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Match is over!") }
                return
            }
            if (match.board.turn != match.board.player2) {
                _singlePlayerMatch.update { it.copy(errorMessage = "Not the machine's turn!") }
                return
            }
            when(match.matchType) {
                MatchType.Reversi -> {
                    val possibleSquares = possibleMoves(match.board.player2, match.board.positions as List<ReversiPosition>)
                    val position = possibleSquares.random()
                    val newMatch = match.makeMove(ReversiMove(match.board.player2, position))
                    _singlePlayerMatch.update { it.copy(
                        newMatch
                    )}
                    if(newMatch.board.turn == newMatch.board.player2) {
                        viewModelScope.launch {
                            delay(1000L)
                            randomMachineMove()
                        }
                    }
                }
                MatchType.TicTacToe -> {
                    val position = match.board.positions.filter { (it as TicPosition).player == Player.EMPTY }.random()
                    _singlePlayerMatch.update { it.copy(
                        match.makeMove(
                            TicTacToeMove(
                                match.board.player2,
                                position.square,
                            )
                        )
                    )}
                }
            }
        }
        catch (e: Exception){
            _singlePlayerMatch.update { it.copy(errorMessage = e.message ?: e.cause?.message) }
        }
    }

    /**
     * Function "updateMatchTypeInput" responsible for updating the type of match to start.
     * @param input The string input that represents the match type.
     */
    fun updateMatchTypeInput(input: String){
        _singlePlayerMatch.update { it.copy(matchTypeInput = input, errorMessage = null) }
    }

    /**
     * Function "clear" responsible for the cleanup of this viewModel.
     * Clears the `SinglePlayerState` and cancels the viewModel scope.
     */
    override fun clear() {
        _singlePlayerMatch.value = SinglePlayerState()
        viewModelScope.cancel()
    }
}