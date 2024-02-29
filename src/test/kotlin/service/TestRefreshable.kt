package service

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

    override fun refreshAfterShiftCard(left: Boolean) {
        refreshAfterShiftCardCalled = true
    }

    override fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int?, middleIndex: Int?) {
        refreshAfterSwapCardCalled = true
    }

    override fun refreshAfterGameEnd(result: List<Pair<Player,String>>) {
        refreshAfterEndGameCalled = true
    }

    override fun refreshAfterNextPlayer() {
        refreshAfterNextPlayerCalled = true
    }


}