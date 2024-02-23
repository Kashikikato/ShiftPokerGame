package entity

/**
 * Entity to represent a player in the game "Shift Poker". Besides have a [name] and the information
 * whether the player has shifted in this round ([hasShifted]), the player just consists of two lists of cards:
 * [hiddenCards] and [openCards]
 */
data class Player(val name: String) {
    var hiddenCards: MutableList<Card> = mutableListOf()
    var openCards: MutableList<Card> = mutableListOf()
    var hasShifted: Boolean = false
}