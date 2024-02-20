package entity
class Board (
    val discardPileLeft : Card? = null,
    val discardPileRight : Card? = null,
    val middleCards: MutableList<Card> = mutableListOf(),
    val drawPile : MutableList<Card> = mutableListOf()
)