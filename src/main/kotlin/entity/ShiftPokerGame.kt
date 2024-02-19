package entity

data class ShiftPokerGame(
    val players: MutableList<Player> = MutableList(2) {Player("Player ${it + 1}",false, emptyList(), emptyList()) },
    val board: Board,
    var currentPlayer: Int,
    var rounds : Int = 2
)