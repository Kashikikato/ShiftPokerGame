package service

import entity.*

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 */
class PokerGameService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Starts the game by creating one if necessary, checking the number of players and rounds,
     * and shuffling the player order. The function also creates the deck, shuffles it, hands the cards to the
     * respective players, and defines the remaining cards as the draw pile.
     *
     * @param players a list of the players in the game. Set to "Player 1" and "Player 2" by default.
     * @param rounds the number of rounds to be played. Set to "2" by default.
     *
     * @throws IllegalArgumentException if the size of [players] or the number of [rounds] are not in bounds.
     */
    fun startGame(players: MutableList<String> = mutableListOf("Player 1", "Player 2"), rounds: Int = 2){

        // check if number of players and rounds are set correctly
        if(players.size !in 2..4 || rounds !in 2..7){
            throw IllegalArgumentException()
        }
        // check if no game has started yet
        if(rootService.currentGame == null) {
            rootService.currentGame = ShiftPokerGame()
        }

        val game = rootService.currentGame

        if (game != null) {
            game.rounds = rounds
            // shuffle the order of the players to randomize first player
            game.players = players.map { Player(it) }.shuffled().toMutableList()
            // empty discard piles
            game.board.discardPileLeft = null
            game.board.discardPileRight = null

            game.board.middleCards.clear()

            // chose the first player of the shuffled list to begin
            game.currentPlayer = 0

            // create a deck with 52 cards for the game
            val cardDeck: ArrayDeque<Card> = ArrayDeque(52)
            game.board.cardDeck = cardDeck

            // add all cards (all possible combinations of suits and values) to the deck
            for (suit in CardSuit.values()) {
                for (value in CardValue.values()) {
                    val card = Card(suit, value)
                    // add the card to the deck
                    cardDeck.add(card)
                }
            }

            // shuffle the deck
            cardDeck.shuffle()

            // hand out the three open and two hidden cards to each player
            for (player in game.players) {
                player.openCards.clear()
                player.hiddenCards.clear()
                repeat(3) {
                    player.openCards.add(cardDeck.removeFirst())
                }
                repeat(2) {
                    player.hiddenCards.add(cardDeck.removeFirst())
                }
            }
            repeat(3) {
                game.board.middleCards.add(cardDeck.removeFirst())
            }

            // use the remaining cards as the draw pile
            game.board.drawPile = cardDeck

            onAllRefreshables {refreshAfterStartGame() }
        }
    }

    /**
     * This method determines which player is next to play their turn. It also potentially reduces the
     * number of rounds to be played by checking if the last player has played their turn, in which case
     * the first player is next to play their turn. In the case of this being the last turn of the last round,
     * the method calls calcResult().
     *
     * @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     */
    fun nextPlayer() {
        // retrieve current game state
        val game = checkNotNull(rootService.currentGame)
        val currentPlayerIndex = game.currentPlayer
        val nextPlayerIndex = (currentPlayerIndex+1+game.players.size) % game.players.size
        val nextPlayer = game.players[nextPlayerIndex]

        game.currentPlayer = nextPlayerIndex

        if(nextPlayerIndex == 0) {
            game.rounds --
            if(game.rounds == 0) {
                endGame()
            }
        }
        nextPlayer.hasShifted = false
        onAllRefreshables { refreshAfterNextPlayer() }
    }

    /**
     * This method ends the game if it has started but not ended yet.
     *
     * @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     */
    fun endGame() {
        // check if game has not started yet or already ended
        checkNotNull(rootService.currentGame) {"Game has not started yet or already ended"}
        // end game if it has not already
        calcResult()
        onAllRefreshables { refreshAfterGameEnd(rootService.pokerGameService.calcResult()) }
    }

    /**
     * This method calculates the result of the game after the last round has been played.
     *
     * @return the result as a map of the players and their respective poker hands.
     *
     * @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     */
    fun calcResult(): List<Pair<Player, String>> {
        val game = checkNotNull(rootService.currentGame)
        val scoreboard = game.players.map{ player ->
            player to evaluateHand(player.openCards + player.hiddenCards)}.toMutableList()

        return sortResults(scoreboard)
    }

    private fun sortResults(result: List<Pair<Player, String>>): List<Pair<Player, String>>  {
        return result.sortedByDescending {
                (_, handStrength) ->
            when (handStrength) {
                "Royal Flush" -> 10
                "Straight Flush" -> 9
                "Four of a Kind" -> 8
                "Full House" -> 7
                "Flush" -> 6
                "Straight" -> 5
                "Three of a Kind" -> 4
                "Two Pair" -> 3
                "Pair" -> 2
                "High Card" -> 1
                else -> 0
            }
        }
    }

    /**
     * This method evaluates the hand of a player based on the rules of "Poker".
     *
     * @param hand the hand of the player to be evaluated.
     *
     * @return the evaluated "Poker" hand as a String.
     * @return "unknown" if evaluation fails.
     */
    fun evaluateHand(hand: List<Card>): String {

        val pokerHands = mutableListOf(
            "Royal Flush",
            "Straight Flush",
            "Four of a Kind",
            "Full House",
            "Flush",
            "Straight",
            "Three of a Kind",
            "Two Pair",
            "Pair",
            "High Card"
            )

        for (pokerHand in pokerHands) {
            if (matchPokerHand(hand,pokerHand)) {
                return pokerHand
            }
        }
        return "unknown"
    }

    /**
     * This method is used by evaluateHand(hand) to determine the "Poker" hand. It uses a String parameter that contains
     * a specific "Poker" hand to return the Boolean value of the respective Boolean function, i.e. return
     * the value of hasRoyalFlush(hand) when the String parameter equals "Royal Flush". The purpose of this method is to
     * find a match between the true Boolean value and the respective String. In that case, the String can be returned
     * in evaluateHand(hand).
     *
     * @param hand a list of cards consisting of the open and hidden cards of a player, i.e. representing the hand.
     * @param pokerHand a String parameter representing a "Poker" hand
     *
     * @return true if the player has the "Poker" hand that was passed as a String parameter, else return false.
     */
    private fun matchPokerHand(hand: List<Card>, pokerHand: String): Boolean {
        val handValue = HandValue()
        when (pokerHand) {
            "Royal Flush" -> return handValue.hasRoyalFlush(hand)
            "Straight Flush" -> return handValue.hasStraightFlush(hand)
            "Four of a Kind" -> return handValue.hasFourOfAKind(hand)
            "Full House" -> return handValue.hasFullHouse(hand)
            "Flush" -> return handValue.hasFlush(hand)
            "Straight" -> return handValue.hasStraight(hand)
            "Three of a Kind" -> return handValue.hasThreeOfAKind(hand)
            "Two Pair" -> return handValue.hasTwoPair(hand)
            "Pair" -> return handValue.hasPair(hand)
            "High Card" -> return true // every hand has a high card
        }
        return false
    }

}

