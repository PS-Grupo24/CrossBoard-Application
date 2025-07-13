
# Guia de Integração de Novo Jogo na Plataforma CrossBoard Application

Este guia descreve os passos necessários para integrar um novo jogo de tabuleiro na plataforma CrossBoard Application, seguindo as interfaces e contratos definidos comum da aplicação.

## Regras dos Jogos Suportados

A plataforma suporta jogos com as seguintes características:

- Jogo de tabuleiro para **2 jogadores** com posições e lista de jogadas.
- Sistema de **turnos**.
- Não depende essencialmente de **temporizadores de jogadas** (por simplicidade, é usadonum temporizador de 30 segundos por jogada).

Alguns jogos que satisfazem estas regras são Damas, Cinco em Linha, Hex, etc.

## Adicionar novos Jogos

Para adicionar/criar um novo jogo é então necessário realizar implementações nos diversos módulos da aplicação.

### 1. Implementação no Módulo Domain

As novas estruturas e métodos a criar neste módulo são necessários para o servidor e cliente.

#### 1.1 MatchType

Este novo MatchType serve como uma chave para representar o novo modo de jogo. Deste modo, é necessário aceder ao ficheiro “shared/src/commonMain/kotlin/com.crossBoard/domain/MatchType.kt” e adicionar à Enum Class MatchType um novo valor. Quando todas as estruturas forem implementadas, coloca-se na lista ‘availableTypes’, disponibilizada por companion object. Colocar o novo tipo em ‘availableTypes’ pode resultar num erro NoSuchElementException na execução, quando for obtido UiModule ou MatchModule.

```kotlin

enum class MatchType() {
    NewType,
    TicTacToe,
    Reversi;
}
```
#### 1.2 Position

A nova posição deverá ser criada na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/position/”.
Para implementar uma nova posição é necessário respeitar o seguinte contrato:

```kotlin
interface Position {
    val square: Square
}
```

É necessário a nova posição conter a propriedade do tipo Square de forma a que esta posição possa ser associada a uma tela do tabuleiro. É responsabilidade de quem implementa a nova posição saber como associá-la a um tipo de jogador Player.
A seguinte listagem contém um exemplo de implementação de uma nova posição:

```kotlin
    data class NewPosition(
        override val square: Square,
        val player: Player,
        val piece: NewPiece
    ): Position
```

#### 1.3 Move

O novo movimento deverá ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/move/”.
O contrato a respeitar para que seja implementado um novo Move é o seguinte:

```kotlin
sealed interface Move {
    val player: Player
}
```

A propriedade do tipo Player é necessária para que seja possível saber qual o jogador a efetuar a jogada. Fica a responsabilidade de quem está a desenvolver o novo Move adicionar a informação que ache relevante para representar um movimento no novo tipo de jogo. A listagem 10 representa um exemplo de um novo tipo de movimento.

```kotlin
data class NewMove(
    override val player: Player,
    val from: Square,
    val to: Square,
    val piece: NewPiece
): Move
```

#### 1.4 Board

O novo Board deverá ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/board/”.
O contrato a respeitar de forma a implementar o Board associado ao novo jogo é o seguinte:

```kotlin
sealed interface Board {
   val positions: List<Position>
   val moves: List<Move>
   val player1: Player
   val player2: Player
   val turn: Player
   fun play(move: Move): Board
   fun forfeit(player: Player): Board
   fun get(square: Square): Player?
}
```

A variável positions é responsável por guardar as posições do novo tipo de jogo, a variável moves é responsável por guardar os movimentos feitos no jogo, player1 e player2 representam o tipo do jogador associado (Player.WHITE ou Player.BLACK). O método play contém a lógica a ser implementada para realizar uma jogada e obter o novo Board. O método forfeit contém a lógica necessária para obter um Board já terminado e o método get é responsável por saber se uma posição, associada ao Square recebido, está disponível ou não.

É necessário criar um Board geral abstrato para o novo tipo de jogo que vai ser utilizado na aplicação. Este Board não deve conter a implementação da lógica do jogo, pois serve apenas como um agrupador para os diferentes estados do Board (BoardRun, BoardWin, BoardDraw) e para especificar tipos já criados para este novo jogo (Move, Positon, etc).

A seguinte listagem seguinte demonstra um exemplo de implementação:

```kotlin

abstract class NewBoard : Board{
   abstract override val positions: List<NewPosition>
   abstract override val moves: List<NewMove>

   override fun get(square: Square): Player? {
       positions.find{it.square == square}?.Player?
   }
}
```

#### 1.5 BoardWin

O novo BoardWin deverá ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/board/” ou no mesmo ficheiro que o seu Board abstrato.
Este contrato é necessário de forma a que a Web API consiga entender que o jogo chegou ao fim e quem venceu. O contrato a respeitar é o seguinte:

```kotlin
sealed interface BoardWin : Board {
   val winner: Player
}
```

A implementação deste novo BoardWin deve então respeitar o contrato referido e ainda o Board abstrato criado anteriormente para indicar que é um jogo terminado em vitória para esse Board.

```kotlin
data class NewBoardWin(
   override val winner: Player,
   override val positions: List<NewPosition>,
   override val moves: List<NewMove>,
   override val turn: Player,
   override val player1: Player,
   override val player2: Player,
) : BoardWin, NewBoard(){
   override fun play(move: Move): Board = throw IllegalStateException("The player $winner already won the game.")
   override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game!")
}
```

#### 1.6 BoardDraw

O novo BoardDraw deverá ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/board/” ou no mesmo ficheiro que o seu Board abstrato.
O contrato BoardDraw tem como responsabilidade, indicar à Web API que o jogo terminou em empate. Também é necessário indicar o Board abstrato do novo jogo. A implementação de um Board que cumpra este contrato não é necessário se o novo jogo a implementar não aceitar empates.

```kotlin
sealed interface BoardDraw : Board
```

A listagem 16 demonstra um exemplo para a implementação de um novo BoardDraw.

```kotlin
data class NewBoardDraw(
   override val positions: List<NewPosition>,
   override val moves: List<NewMove>,
   override val turn: Player,
   override val player1: Player,
   override val player2: Player,
) : BoardDraw, NewBoard(){
   override fun play(move: Move): Board = throw IllegalStateException("This game ended on a draw.")
   override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game!")
}
```

#### 1.7 BoardRun

O novo BoardRun deverá ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/domain/board/” ou no mesmo ficheiro que o seu Board abstrato.
O contrato BoardRun tem como responsabilidade indicar à Web API que o jogo ainda está a decorrer. É responsabilidade do novo BoardRun implementar a lógica dos movimentos no método play e saber quando acabar o jogo em vitória ou empate. Também é sua responsabilidade implementar o método forfeit e chamar o BoardWin correto com o vencedor correto. É também necessário indicar o Board abstrato do novo tipo de jogo.

```kotlin
sealed interface BoardRun : Board
```

```kotlin
data class NewBoardRun(
   override val positions: List<NewPosition>,
   override val moves: List<NewMove>,
   override val turn: Player,
   override val player1: Player,
   override val player2: Player,
) : BoardRun, NewBoard(){

   override fun play(move: Move): Board {
       require(move is NewMove) {"Wrong move type"}
       require(move.player == turn) {"Not your turn"}
       require(get(move.square) == null) {"This position is not empty!"}
       val newPositions = getNewPositions(positions)
       if (move.player == Player.BLACK) return NewBoardWin(move.player, newPositions, moves + move, turn.other(), player1, player2)
       else NewBoardDraw(newPositions, moves + move, turn.other(), player1, player2)
   }
}
```

#### 1.8 MoveHttp

O contrato MoveHttp, tem como função, representar um dado Move numa estrutura cujas propriedades são representadas em tipos primitivos (capítulo 3.2.1). Este novo tipo tem de ser obrigatoriamente marcado com a anotação @Serializable e deve ser criado na diretoria “shared/src/commonMain/kotlin/com.crossBoard/httpModel/moveHttp/”.
A listagem 20 demonstra o exemplo de uma nova implementação de um MoveHttp.

```kotlin
@Serializable
data class newMoveHttp(
   val player: String,
   val from: String,
   val to: String,
   val piece: String,
) : MoveHttp
```

#### 1.9 MatchModule

O contrato MatchModule<Board, Move, Position, MoveHttp> serve como um agrupador de todos os novos tipos criados, associando-os ao MatchType e é necessário que este se encontre na diretoria “shared/src/commonMain/kotlin/com/crossBoard/domain/matchModule/”.
Este contrato tem como requisitos, garantir que são implementados métodos necessários tanto para cliente como para servidor, mas que não são possíveis de realizar sem saber as especificações dos novos tipos criados. O objeto MatchProvider serve como um gerenciador para os MatchModule existentes e disponibiliza o método get(matchType: MatchType): MatchModule e através deste método é então possível obter o módulo com as implementações esperadas para cada tipo de partida. A listagem seguinte demonstra com maior detalhe os requisitos a cumprir com este contrato:

```kotlin
interface MatchModule<
       B : Board,
       M : Move,
       P : Position,
       MH: MoveHttp
       >
{
   val matchType: MatchType
   fun getInitialBoard(): B
   fun stringToPosition(input: String): P
   fun positionToString(position: P): String
   fun moveToMoveHttp(move: M): MH
   fun moveHttpToMove(move: MH): M
   fun getBoard(
       positions: List<P>,
       moves: List<M>,
       player1: Player,
       player2: Player,
       turn: Player,
       winner: Player?,
       state: MatchState,
   ): B
}
```

Quando a implementação do novo MatchModule for completa, é necessário colocar este na lista ‘modules‘ disponibilizada por MatchProvider, caso contrário, é possível encontrar o erro NoSuchElementException durante a execução.

### 2. Implementação no Módulo ComposeApp

Neste módulo é criada uma estrutura de dados que será responsável por implementar métodos necessários apenas para o cliente.

#### 2.1 UiModule

O contrato UiModule é responsável por garantir que o novo MatchType contém um método BoardView, que é responsável por obter o visual do novo jogo, e um método generateRandomMachineMove que obtém uma jogada válida a ser realizada pelo computador para que seja possível uma partida de jogador único. É responsabilidade do método BoardView, garantir que as interações do utilizador com os elementos visuais, criam um Move para ser passado em onMakeMove. A criação deste módulo deve ser feita na diretoria “composeApp/src/commonMain/kotlin/com/crossBoard/ui/uiModule”.
A seguinte listagem demonstra o contrato UiModule mais detalhado.

```kotlin
interface UiModule<B: Board, M: Move> {
   val matchType: MatchType
   @Composable
   fun BoardView(
       board: B,
       myPlayerType: Player,
       onMakeMove: (move: M) -> Unit,
       enabled: Boolean = true,
       modifier: Modifier = Modifier
   )

   fun generateRandomMachineMove(board: B, machinePlayerType: Player): M
}
```

Após a criação do novo UiModule, é necessário adicionar na lista ‘uiModules‘ disponibilizada por UiModuleProvider, caso contrário, é possível obter o erro NoSuchElementException durante a execução.
