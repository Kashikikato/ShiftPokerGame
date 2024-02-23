package entity
import kotlin.test.*

class ShiftPokerGameTest {
    //create game to test with
    private val game = ShiftPokerGame()

    /**
     * Check if the number of rounds is set correctly.
     */
    @Test
    fun testRoundsInBounds() {
        assertFalse(game.rounds == 8)
        assertTrue(game.rounds in 2..7)
    }

    /**
     * Check if the current player is set correctly.
     */
    @Test
    fun testCurrentPLayerInBounds() {
        assertFalse(game.currentPlayer < 0)
        assertTrue(game.currentPlayer in 0..1)
    }

    /**
     * Check if initial number of players is set to 2.
     */
    @Test
    fun testInitialNumberPlayers() {
        assertTrue(game.players.size == 2)
    }
}