package entity

data class Player(
    val name: String,
    var hasShifted: Boolean,
    val hiddenCards : MutableList<Card>,
    val openCards : MutableList<Card>
)