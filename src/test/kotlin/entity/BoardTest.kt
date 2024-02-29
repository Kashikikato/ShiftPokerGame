package entity

import kotlin.test.*

/**
 * Unit tests for the [Board] class.
 */
class BoardTest {
    // create a board to test with
    private val board = Board()

    /**
     * Checks if all piles are empty.
     */
    @Test
    fun testPilesEmpty() {
        assertTrue(board.discardPileLeft == null)
        assertTrue(board.discardPileRight == null)
        assertTrue(board.middleCards.isEmpty())
        assertTrue(board.drawPile.isEmpty())
    }
}
