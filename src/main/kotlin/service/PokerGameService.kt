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
     */
    fun startGame(players: MutableList<String> = mutableListOf("Player 1", "Player 2"), rounds: Int = 2){

        // check if no game has started yet
        if(rootService.currentGame == null) {
            rootService.currentGame = ShiftPokerGame()
        }

        val game = rootService.currentGame

        // check if number of players and rounds are set correctly
        if(players.size !in 2..4 || rounds !in 2..7){
            throw IllegalArgumentException()
        }

        if (game != null) {
            game.rounds = rounds
            // shuffle the order of the players to randomize first player
            game.players.shuffle()

            // empty discard piles
            game.board.discardPileLeft = null
            game.board.discardPileRight = null

            // chose the first player of the shuffled list to begin
            game.currentPlayer = 0

            // create a deck with 52 cards for the game
            val cardDeck: ArrayDeque<Card> = ArrayDeque(52)

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
     */
    fun nextPlayer() {
        // retrieve current game state
        val game = checkNotNull(rootService.currentGame)

        // check if it was the last turn of the round
        if (game.currentPlayer == game.players.size - 1) {
            // check if there are rounds to be played left
            if (game.rounds > 0) {
                //set the first player to play the next turn
                game.currentPlayer = 0
                game.rounds--
            } else calcResult()
        }
        else {
            // check if there are rounds to be played
            if (game.rounds > 0) {
                game.rounds--
            }
            // increase currentPlayer by 1 regardless of the number of rounds to be played
            game.currentPlayer++
        }
        onAllRefreshables { refreshAfterNextPlayer(game.players[game.currentPlayer-1]) }
    }

    /**
     * This method ends the game if it has not ended yet.
     */
    fun endGame() {
        // check if game has already ended
        checkNotNull(rootService.currentGame)
        // end game if it has not already
        rootService.currentGame = null

        onAllRefreshables { refreshAfterGameEnd(calcResult()) }
    }

    /**
     * This method calculates the result of the game after the last round has been played.
     *
     * @return the result as a map of the players and their respective poker hands.
     */
    private fun calcResult(): Map<Player,String> {
        val game = checkNotNull(rootService.currentGame)
        val scoreboard = mutableMapOf<Player, String>()

        for (player in game.players) {
            val hand = player.hiddenCards + player.openCards

            // evaluate hand based on rules of poker (simplified)
            val pokerHand = evaluateHand(hand)

            scoreboard[player] = pokerHand
        }

        return scoreboard
    }

    /**
     * This method evaluates the hand of a player based on the rules of "Poker".
     *
     * @param hand the hand of the player to be evaluated.
     *
     * @return the evaluated "Poker" hand as a String.
     * @return "unknown" if evaluation fails.
     */
    private fun evaluateHand(hand: List<Card>): String {

        val pokerHands = listOf(
            "Royal Flush",
            "Straight Flush",
            "4 of a Kind",
            "Full House",
            "Flush",
            "Straight",
            "3 of a Kind",
            "2 Pair",
            "1 Pair",
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
        when (pokerHand) {
            "Royal Flush" -> return hasRoyalFlush(hand)
            "Straight Flush" -> return hasStraightFlush(hand)
            "Four of a Kind" -> return hasFourOfAKind(hand)
            "Full House" -> return hasFullHouse(hand)
            "Flush" -> return hasFlush(hand)
            "Straight" -> return hasStraight(hand)
            "Three of a Kind" -> return hasThreeOfAKind(hand)
            "Two Pair" -> return hasTwoPairs(hand)
            "One Pair" -> return hasPair(hand)
            "High Card" -> return true // every hand has a high card
        }
        return false
    }

    // the following methods detect relevant combinations of cards based on the rules of poker

    /**
     * Determines whether the given hand contains a "Royal Flush". Therefore, checks whether the player
     * has a "Straight Flush" and an ace, represented by the ordinal number 12. In combination, this represents
     * a "Royal Flush".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Royal Flush", else return false.
     */
    private fun hasRoyalFlush(hand: List<Card>): Boolean {
            return hasStraightFlush(hand) && hand[4].value.ordinal == 12
    }

    /**
     * Determines whether the given hand contains a "Straight Flush". Therefore, checks whether the player
     * has a "Straight" and a "Flush" by calling the respective methods. In combination, this represents
     * a "Straight Flush".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Straight Flush", else return false.
     */
    private fun hasStraightFlush(hand: List<Card>): Boolean {
        return hasStraight(hand) && hasFlush(hand)
    }

    /**
     * Determines whether the given hand contains a "Straight Flush". Therefore, checks whether the player
     * has "3 of a Kind" and "2 Pairs", whilst not having a "Full House" by calling the respective methods.
     * In combination, this represents a "4 of a Kind", since this combination is only possible if the "2 Pairs"
     * consist of the same values.
     *
     * Note that "2 Pairs" generally means that the two pairs are not equal by value.
     * The method hasTwoPairs() ignores this for the purpose of detecting a "4 of a Kind".
     * Since evaluateHand checks for "4 of a Kind" first, this should not cause any problems
     * such as returning a "2 Pairs" hand when there are "4 of a Kind".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has "4 of a Kind", else return false.
     */
    private fun hasFourOfAKind(hand: List<Card>): Boolean {
        return hasThreeOfAKind(hand) && hasTwoPairs(hand) && !hasFullHouse(hand)
    }

    /**
     * Determines whether the given hand contains a "Flush". It simply uses a nested loop to check if all
     * card suits are the same.
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Flush", else return false.
     */
    private fun hasFlush(hand: List<Card>): Boolean {
        for(card in hand) {
            for(otherCard in hand) {
                if(card.suit.ordinal != otherCard.suit.ordinal) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Determines whether the given hand contains a "Full House". This is represented by the combination
     * of "3 of a Kind" and "1 Pair". Therefore, the function calls the two respective methods and return true,
     * if both are true.
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Full House", else return false.
     */
    private fun hasFullHouse(hand: List<Card>): Boolean {
        return hasThreeOfAKind(hand) && hasPair(hand)
    }

    /**
     * Determines whether the given hand contains a "Straight". Therefore, the method checks if the hand is sorted
     * in consecutive order, which represents a "Straight".
     *
     * @param hand the hand of the player.
     *
     * @return true if the hand contains a "Straight", else return false.
     */
    private fun hasStraight(hand: List<Card>): Boolean {
        for(i in 0 until hand.size-1) {
            if(hand[i+1].value.ordinal - hand[i].value.ordinal != 1) {
                return false
            }
        }
        return true
    }

    /**
     * Determines whether the given hand contains "3 of a Kind". Therefore, the method uses nested for-loops and
     * a [count] variable to return the proper Boolean value.
     *
     * @param hand the hand of the player.
     *
     * @return true, if the hand contains "3 of a Kind", else return false.
     */
    private fun hasThreeOfAKind(hand: List<Card>): Boolean {
        var count = 0
        for(i in 0..2) {
            for(card in hand) {
                if(card.value.ordinal == hand[i].value.ordinal) {
                    count++
                }
           }
            if(count >= 3) {
                return true
            }
        }
        return false
    }

    /**
     * Determines whether the given hand contains "2 Pairs". Therefore, it uses nested for-loops
     * and a mutableList, to which the cards that constitute a pair are added. Note that this can also
     * potentially represent "4 of a Kind", since it does not require the "2 Pairs" to be of different values.
     * In that case, it evaluateHand() will recognize it as "4 of a Kind".
     *
     * @param hand the hand of the player.
     *
     * @return true, if the hand contains "2 Pairs", else return false.
     */

    private fun hasTwoPairs(hand: List<Card>): Boolean {

        if(!hasPair(hand)) return false
        val pairs = mutableListOf<Card>()
        for(card in hand) {
            for(otherCard in hand) {
                if(card.value.ordinal == otherCard.value.ordinal) {
                    if(!pairs.contains(card)) {
                        pairs.add(card)
                    }
                    if(!pairs.contains(otherCard)) {
                        pairs.add(otherCard)
                    }
                }
            }
        }
        return pairs.size >= 4
    }

    /**
     * Determines whether the given hand consists of "One Pair". Therefore, it simply compares the cards in the hand
     * with one another and returns true, if two cards are equal in value.
     *
     * @param hand the hand of the player.
     *
     * @return true if the hand consists of "One Pair", return false otherwise.
     */
    private fun hasPair(hand: List<Card>): Boolean {
        for(card in hand) {
            for(otherCard in hand) {
                if(card.value.ordinal == otherCard.value.ordinal && card.suit.ordinal != otherCard.suit.ordinal) {
                    return true
                }
            }
        }
        return false
    }
}
