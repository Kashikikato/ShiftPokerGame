package entity

data class ShiftPokerGame(

    /* initialize players with minimum size 2
    *  create default names starting with "Player 1"
    *  set hasShifted to false & initialize openCards & hiddenCards as empty
    */
    val players: MutableList<Player> = MutableList(2) {Player("Player ${it + 1}",false, emptyList(), emptyList()) },
    
    val board: Board,
    var currentPlayer: Int,

    //set rounds to minimum of 2
    var rounds : Int = 2
)
