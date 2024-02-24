package service

import entity.Board
import entity.Player
import view.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var refreshAfterStartGameCalled: Boolean = false
        private set

    var refreshAfterShiftCardCalled: Boolean = false
        private set

    var refreshAfterSwapCardCalled: Boolean = false
        private set

    var refreshAfterEndGameCalled: Boolean = false
        private set

    var refreshAfterNextPlayerCalled: Boolean = false
        private set

    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartGameCalled = false
        refreshAfterShiftCardCalled = false
        refreshAfterSwapCardCalled = false
        refreshAfterEndGameCalled = false
        refreshAfterNextPlayerCalled = false
    }

    override fun refreshAfterStartGame() {
        refreshAfterStartGameCalled = true
    }

    override fun refreshAfterShiftCard(shiftingPlayer: Player, boardCards: Board) {
        refreshAfterShiftCardCalled = true
    }

    override fun refreshAfterSwapCard(player: Player, boardCards: Board) {
        refreshAfterSwapCardCalled = true
    }

    override fun refreshAfterGameEnd(result: Map<Player,String>) {
        refreshAfterEndGameCalled = true
    }

    override fun refreshAfterNextPlayer(player: Player) {
        refreshAfterNextPlayerCalled = true
    }


}