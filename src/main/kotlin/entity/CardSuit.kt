package entity

/**
 * Enum to distinguish between the four possible suits in a french-suited card game:
 * clubs, spades, hearts, or diamonds
 */
enum class CardSuit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES
    ;

    /**
     * provide a single character to represent this suit.
     * Returns one of: ♣/♠/♥/♦
     */
    override fun toString() = when(this) {
        CLUBS -> "♣"
        DIAMONDS -> "♦"
        HEARTS -> "♥"
        SPADES -> "♠"
    }


}