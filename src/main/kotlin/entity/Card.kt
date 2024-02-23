package entity

/**
 * Data class for the single type of game elements that the game "Shift Poker" knows: cards.
 *
 * It is characterized by a [CardSuit] and a [CardValue]
 */
data class Card(val suit: CardSuit, val value: CardValue) : Comparable<Card> {

    private val cardDeck: ArrayDeque<Card> = ArrayDeque(52)

    override fun toString() = "$suit$value"
    override fun compareTo(other: Card): Int {
        val thisValue = this.value.ordinal
        val otherValue = other.value.ordinal

        // Compare the values of the cards
        return thisValue - otherValue
    }
}

