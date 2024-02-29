package entity

/**
 * Entity class that represents the game board
 *
 * @property middleCards contains the three middle cards
 * @property drawPile contains the card deck form which the players draw cards
 * @property discardPileLeft holds the discard pile on the left side of the middle cards
 * @property discardPileRight holds the discard pile on the right side of the middle cards
 */
class Board {
    var cardDeck: ArrayDeque<Card> = ArrayDeque(52)
    var middleCards: MutableList<Card> = mutableListOf()
    var drawPile: MutableList<Card> = mutableListOf()
    var discardPileLeft: Card? = null
    var discardPileRight: Card? = null
}