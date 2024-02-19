package entity

data class Player(
    val name: String,
    var hasShifted: Boolean,
    val hiddenCards : List<Card>,
    val openCards : List<Card>
)