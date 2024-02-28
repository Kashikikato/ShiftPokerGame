package service

import view.Refreshable
import kotlin.test.*

/**
 * Class that provides tests for [PokerGameService] and [PlayerActionService] (both at the same time,
 * as their functionality is not easily separable) by basically playing through some sample games.
 * [TestRefreshable] is used to validate correct refreshing behavior even though no GUI
 * is present.
 */

class PlayerActionServiceTest {

    /**
     * starts a game with 7 rounds to be played by two players with the default names "Player 1" and "Player 2".
     *
     * @param refreshables refreshables to be added to the root service
     * right after its instantiation (so that, e.g., start new game will already
     * be observable)
     *
     * @return the root service holding the started game as [RootService.currentGame]
     */

    private fun setUpGame(vararg refreshables: Refreshable): RootService {
        val mc = RootService()
        refreshables.forEach { mc.addRefreshable(it) }

        mc.pokerGameService.startGame(rounds = 7)
        println(mc.currentGame)
        return mc
    }

    /**
     * tests shifting the middle cards to the left and to the right.
     *
     */
    @Test
    fun testShift() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame

        assertNotNull(currentGame)

        val board = mc.currentGame!!.board
        var middleCardLeft = board.middleCards[0]
        var middleCardMiddle = board.middleCards[1]
        var middleCardRight = board.middleCards[2]
        var nextCard = board.drawPile.first()


        mc.playerActionService.shift(true)
        assertEquals(middleCardLeft, board.discardPileLeft)
        assertEquals(middleCardMiddle, board.middleCards[0])
        assertEquals(middleCardRight, board.middleCards[1])
        assertEquals(nextCard, board.middleCards[2])

        mc.currentGame!!.players[0].hasShifted = false

        middleCardLeft = board.middleCards[0]
        middleCardMiddle = board.middleCards[1]
        middleCardRight = board.middleCards[2]
        nextCard = board.drawPile.first()

        mc.playerActionService.shift(false)
        assertEquals(board.discardPileRight, middleCardRight)
        assertEquals(board.middleCards[2], middleCardMiddle)
        assertEquals(board.middleCards[1], middleCardLeft)
        assertEquals(board.middleCards[0], nextCard)

        assertTrue(testRefreshable.refreshAfterShiftCardCalled)
    }

    /**
     * tests swapping and calling nextPlayer() afterward.
     */
    @Test
    fun testSwapAndNextPlayer() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame

        assertNotNull(currentGame)

        val player1 = currentGame.players[0]
        val board = mc.currentGame!!.board
        val tempHandCard = player1.openCards[2]
        val tempMiddleCard = board.middleCards[1]

        assertFails { mc.playerActionService.pass() }

        player1.hasShifted = true

        mc.playerActionService.swap(2, 1)
        assertEquals(tempHandCard, board.middleCards[1])
        assertEquals(tempMiddleCard, player1.openCards[2])

        assertFails { mc.playerActionService.swapAll() }

        assertTrue(mc.currentGame!!.currentPlayer == 1)

        currentGame.players[1].hasShifted = true

        val tempHand = currentGame.players[1].openCards
        val tempMiddle = currentGame.board.middleCards

        mc.playerActionService.swapAll()

        val openCards = currentGame.players[1].openCards
        var middleCards = currentGame.board.middleCards

        assertEquals(tempHand, middleCards)
        assertEquals(tempMiddle, openCards)

        assertFails { mc.playerActionService.pass() }

        assertTrue(mc.currentGame!!.currentPlayer == 0)

        middleCards = currentGame.board.middleCards

        player1.hasShifted = true
        mc.playerActionService.pass()

        assertEquals(middleCards, currentGame.board.middleCards)
        assertTrue(testRefreshable.refreshAfterNextPlayerCalled)
    }
}
