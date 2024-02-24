package entity

/**
 * Entity class that represents a game state of "Shift Poker". It consists of a list of [players],
 * initially set to two players "Player 1" and "Player 2" by default, and a [board] with all the displayed cards.
 *
 * @property currentPlayer index of the [players] list to indicate the current player
 * @property rounds number of rounds to be played, initially set to 2 by default
 */
class ShiftPokerGame() {
    var players: MutableList<Player> = mutableListOf(Player("Player 1"), Player("Player 2"))
    val board: Board = Board()
    var currentPlayer: Int = 0
    var rounds: Int = 2
}