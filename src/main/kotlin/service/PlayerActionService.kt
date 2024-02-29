package service

import entity.*

/**
 * Service layer class that provides the logic for the possible actions a player
 * can take in Shift Poker: shift left or right, swap one card, swap all cards, or skip swapping by passing
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {
     /**
     * Shifts the middle cards to the desired direction, takes a card from the draw pile,
     * and puts it onto the free spot.
     * Afterwards, sets [Player.hasShifted] to `true`.
     *
     * @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     * @throws IllegalStateException if [Player] has already shifted the cards.
     *
     */
    fun shift(left: Boolean) {
        // receive current game state
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]
        if (player.hasShifted) {
            throw IllegalStateException("You have shifted already!")
        }

        // receive middleCards
        val middleCards = game.board.middleCards

        if (left) {
            // place the left card from middle cards on top of discardPile
            game.board.discardPileLeft = middleCards[0]

            // shift middleCards one to the left
            middleCards[0] = middleCards[1]
            middleCards[1] = middleCards[2]

            // replace free spot with card from drawPile
            middleCards[2] = game.board.drawPile.removeFirst()
        } else {
            // place the right card from middle cards on top of discardPile
            game.board.discardPileRight = middleCards[2]

            // shift middleCards one to the right
            middleCards[2] = middleCards[1]
            middleCards[1] = middleCards[0]

            // replace free spot with card from drawPile
            middleCards[0] = game.board.drawPile.removeFirst()
        }


        // refresh the GUI
        onAllRefreshables { refreshAfterShiftCard(left) }

        // player finished shifting
        player.hasShifted = true
    }

    /**
     * Swaps all the cards from [Player]'s open cards with the middle cards.
     * Calls nextPlayer() afterward.
     *
     * @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     * @throws IllegalStateException if player has not shifted yet.
     */
    fun swapAll() {
        // retrieve current game from root service
        val game = checkNotNull(rootService.currentGame)

        // set
        val player = game.players[game.currentPlayer]

        if (!player.hasShifted) {
            throw IllegalStateException("You need to shift first!")
        }

        val temp = player.openCards
        player.openCards = game.board.middleCards
        game.board.middleCards = temp

        onAllRefreshables { refreshAfterSwapCard(true, false) }

//        rootService.pokerGameService.nextPlayer()
    }

    /**
     * Swaps one chosen card from [Player]'s open cards with one chosen middle card.
     * Calls nextPlayer() afterward.
     *
     * @param handIndex the index of the open hand card to be swapped
     * @param middleIndex the index of the middle card to be swapped
     *
     *  @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     *  @throws IllegalStateException if the index of either the middle card or the hand card is out of bounds
     */
    fun swap(handIndex: Int, middleIndex: Int) {
        //retrieve current game state
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]

        //check if indices are in bounds
        if (handIndex !in 0..2 || middleIndex !in 0..2) {
            throw IllegalStateException()
        }

        if (!player.hasShifted) {
            throw IllegalStateException("You need to shift first!")
        }

        val temp = player.openCards[handIndex]
        player.openCards[handIndex] = game.board.middleCards[middleIndex]
        game.board.middleCards[middleIndex] = temp

        onAllRefreshables { refreshAfterSwapCard(false, false, handIndex, middleIndex) }

//        rootService.pokerGameService.nextPlayer()
    }


    /**
     * Skips swapping cards and simply calls nextPlayer().
     *
     *  @throws IllegalStateException if [ShiftPokerGame] has not started yet or already ended.
     *  @throws IllegalStateException if the player has not shifted yet.
     */

    fun pass() {
        // retrieve current game from root service
        val game = checkNotNull(rootService.currentGame)

        val player = game.players[game.currentPlayer]

        if (!player.hasShifted) {
            throw IllegalStateException("You need to shift first!")
        }
//        rootService.pokerGameService.nextPlayer()
    }
}
