package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.Player
import kotlin.test.*

/**
 * Unit tests for the [PokerGameService] class.
 */
class PokerGameServiceTest {
    /**
     * Tests the default case of starting a game: instantiate a [RootService] and then run
     * startNewGame on its [RootService.pokerGameService]. Also checks the number of hidden cards and open cards
     * of each player, as well as the number cards in the draw pile and in the discard piles.
     */
    @Test
    fun testStartGame() {
        val testRefreshable = TestRefreshable()
        val mc = RootService()
        mc.addRefreshable(testRefreshable)

        assertFalse(testRefreshable.refreshAfterStartGameCalled)
        assertNull(mc.currentGame)
        mc.pokerGameService.startGame(rounds = 5)
        assertTrue(testRefreshable.refreshAfterStartGameCalled)
        assertNotNull(mc.currentGame)

        assertEquals(3, mc.currentGame!!.players[0].openCards.size)
        assertEquals(3, mc.currentGame!!.players[1].openCards.size)
        assertEquals(2, mc.currentGame!!.players[0].hiddenCards.size)
        assertEquals(2, mc.currentGame!!.players[1].hiddenCards.size)

        assertEquals(3, mc.currentGame!!.board.middleCards.size)
        assertEquals(null, mc.currentGame!!.board.discardPileLeft)
        assertEquals(null, mc.currentGame!!.board.discardPileRight)
        assertEquals(39, mc.currentGame!!.board.drawPile.size)
    }

    /**
     * Tests if it is possible to start a game with 10 rounds (it shouldn't).
     */
    @Test
    fun testFailStartWithTooManyRounds() {
        val mc = RootService()
        val testRefreshable = TestRefreshable()
        mc.addRefreshable(testRefreshable)

        // expected to fail, as number of rounds cannot be higher than 7
        assertFails {
            mc.pokerGameService.startGame(rounds = 10)
        }
        // as the previous attempt failed, there should be no game started...
        assertNull(mc.currentGame)
        // ... and no refresh triggered
        assertFalse(testRefreshable.refreshAfterStartGameCalled)
    }

    /**
     * tests if the game ends successfully after calling endGame().
     */
    @Test
    fun testEndGame() {
        val mc = RootService()
        val testRefreshable = TestRefreshable()

        mc.addRefreshable(testRefreshable)
        assertFalse(testRefreshable.refreshAfterEndGameCalled)

        assertNull(mc.currentGame)
        assertFails {mc.pokerGameService.endGame()}

        mc.pokerGameService.startGame()

        assertNotNull(mc.currentGame)
        assertTrue(testRefreshable.refreshAfterStartGameCalled)

        mc.pokerGameService.endGame()
        assertTrue(testRefreshable.refreshAfterEndGameCalled)
    }

    /**
     * tests the calcResult() method and its utility methods. Note that the results are being sorted manually in
     * the test method. The results are not sorted by ranking (yet), and the order of the players was
     * shuffled in startGame(). Without sorting the players by name here, it would not be possible to
     * assign the hands to the respective player without messing up the names. Ideally, this will be improved.
     */

    @Test
    fun testCalcResult() {
        val mc = RootService()
        val testRefreshable = TestRefreshable()

        mc.addRefreshable(testRefreshable)

        val playerNames = listOf("Player 1", "Player 2", "Player 3", "Player 4")

        val players = playerNames.map { Player(it) }.toMutableList()

        mc.pokerGameService.startGame(players.map { it.name }.toMutableList())
        mc.currentGame!!.players = mc.currentGame!!.players.sortedBy { it.name }.toMutableList()

        val hand1 = mutableListOf(
            Card(CardSuit.SPADES, CardValue.TEN),
            Card(CardSuit.DIAMONDS, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.JACK),
            Card(CardSuit.CLUBS, CardValue.NINE),
            Card(CardSuit.HEARTS, CardValue.NINE)
        )
        mc.currentGame!!.players[0].openCards = mutableListOf(hand1[0], hand1[1], hand1[2])
        mc.currentGame!!.players[0].hiddenCards = mutableListOf(hand1[3], hand1[4])

        val hand2 = mutableListOf(
            Card(CardSuit.HEARTS, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.JACK),
            Card(CardSuit.HEARTS, CardValue.QUEEN),
            Card(CardSuit.HEARTS, CardValue.KING),
            Card(CardSuit.HEARTS, CardValue.NINE)
        )

        mc.currentGame!!.players[1].openCards = mutableListOf(hand2[0], hand2[1], hand2[2])
        mc.currentGame!!.players[1].hiddenCards = mutableListOf(hand2[3], hand2[4])

        val hand3 = mutableListOf(
            Card(CardSuit.SPADES, CardValue.TEN),
            Card(CardSuit.DIAMONDS, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.TEN),
            Card(CardSuit.CLUBS, CardValue.TEN),
            Card(CardSuit.CLUBS, CardValue.NINE)
        )

        mc.currentGame!!.players[2].openCards = mutableListOf(hand3[0], hand3[1], hand3[2])
        mc.currentGame!!.players[2].hiddenCards = mutableListOf(hand3[3], hand3[4])

        val hand4 = mutableListOf(

            Card(CardSuit.HEARTS, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.JACK),
            Card(CardSuit.HEARTS, CardValue.QUEEN),
            Card(CardSuit.HEARTS, CardValue.KING),
            Card(CardSuit.HEARTS, CardValue.ACE)
        )

        mc.currentGame!!.players[3].openCards = mutableListOf(hand4[0], hand4[1], hand4[2])
        mc.currentGame!!.players[3].hiddenCards = mutableListOf(hand4[3], hand4[4])

        val expectedSortedResult: List<Pair<Player, String>> = listOf(
            Player("Player 4") to "Royal Flush",
            Player("Player 2") to "Straight Flush",
            Player("Player 3") to "Four of a Kind",
            Player("Player 1") to "Two Pair"
        )
        val result = mc.pokerGameService.calcResult()
        assertEquals(expectedSortedResult, result)
    }

    /**
     * Tests the evaluateHand() method that is used by calcResult(). Uses all 10 different "Poker" hands to check if
     * these are being identified correctly.
     */
    @Test
    fun testEvaluateHand() {
        val mc = RootService()
        val hand = mutableListOf(
            Card(CardSuit.HEARTS, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.JACK),
            Card(CardSuit.HEARTS, CardValue.QUEEN),
            Card(CardSuit.HEARTS, CardValue.KING),
            Card(CardSuit.HEARTS, CardValue.ACE)
        )
        var handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Royal Flush", handEvaluation)

        hand[4] = Card(CardSuit.HEARTS, CardValue.NINE)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Straight Flush", handEvaluation)

        hand[3] = Card(CardSuit.HEARTS, CardValue.THREE)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Flush", handEvaluation)

        hand[3] = Card(CardSuit.CLUBS, CardValue.KING)
        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Straight", handEvaluation)

        hand[1] = Card(CardSuit.DIAMONDS, CardValue.TEN)
        hand[2] = Card(CardSuit.CLUBS, CardValue.TEN)
        hand[3] = Card(CardSuit.SPADES, CardValue.TEN)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Four of a Kind", handEvaluation)

        hand[3] = Card(CardSuit.SPADES, CardValue.NINE)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Full House", handEvaluation)

        hand[4] = Card(CardSuit.DIAMONDS, CardValue.QUEEN)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Three of a Kind", handEvaluation)

        hand[2] = Card(CardSuit.SPADES, CardValue.QUEEN)
        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Two Pair", handEvaluation)

        hand[0] = Card(CardSuit.CLUBS, CardValue.KING)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("Pair", handEvaluation)

        hand[4] = Card(CardSuit.CLUBS, CardValue.ACE)

        handEvaluation = mc.pokerGameService.evaluateHand(hand)
        assertEquals("High Card", handEvaluation)
    }

}





