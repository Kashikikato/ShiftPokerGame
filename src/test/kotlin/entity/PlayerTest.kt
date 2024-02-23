package entity
import kotlin.test.*

class PlayerTest {
    // create 2 players to test with
    private val player1 = Player("Player 1", false, mutableListOf(), mutableListOf())
    private val player2 = Player("Player 2", false, mutableListOf(), mutableListOf())

    /**
     * Check if the names of the players are unique.
     */
    @Test
    fun testPlayerNamesEquality() {
        assertNotEquals(player1.name, player2.name)
    }

    /**
     * Check if initial attributes are set correctly. Verify that hasShifted is false and the player's hand is empty.
     */
    @Test
    fun testInitialPlayerAttributes()  {
        assertFalse(player1.hasShifted)
        assertTrue(player1.hiddenCards.isEmpty())
        assertTrue(player1.openCards.isEmpty())
    }

}