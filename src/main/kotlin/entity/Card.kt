package entity

/**
 * Data class for the single type of game elements that the game "Shift Poker" knows: cards.
 *
 * It is characterized by a [CardSuit] and a [CardValue]
 */
data class Card(val suit: CardSuit, val value: CardValue) : Comparable<Card> {
    override fun toString() = "$suit$value"

    override fun compareTo(other: Card): Int {
        val thisValue = this.value.ordinal
        val otherValue = other.value.ordinal

        // Compare the values of the cards
        if (thisValue != otherValue) {
            return thisValue - otherValue
        }

        // If the values are equal, compare suits of the cards
        val thisSuit = this.suit.ordinal
        val otherSuit = other.suit.ordinal
        return thisSuit - otherSuit
    }
}

