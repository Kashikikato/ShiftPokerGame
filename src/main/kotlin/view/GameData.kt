package view

import entity.Card

/**
 * Object containing the data for the Shift Poker game.
 */
object GameData {
    /** The number of rounds in the game. */
    var rounds: Int = 2

    /** The names of the players participating in the game. */
    var playerNames: MutableList<String> = mutableListOf("Player 1", "Player 2")

    /** The middle cards available in the game. */
    var middleCards: MutableList<Card> = mutableListOf()
}
