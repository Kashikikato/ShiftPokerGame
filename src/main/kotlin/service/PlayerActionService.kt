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
     * @throws IllegalStateException if [Player] has already shifted the cards.
     *
     * @throws IllegalStateException if no game has started yet.
     */
    fun shift(left: Boolean) {
        // receive current game state
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]
        if (player.hasShifted) {
            throw IllegalStateException("You have shifted already!")
        }

        // receive middleCards
        val gameMiddleCards = game.board.middleCards

        if (left) {
            // place the left card from middle cards on top of discardPile
            game.board.discardPileLeft = gameMiddleCards[0]

            // shift middleCards one to the left
            gameMiddleCards[0] = gameMiddleCards[1]
            gameMiddleCards[1] = gameMiddleCards[2]

            // replace free spot with card from drawPile
            gameMiddleCards[2] = game.board.drawPile.removeFirst()
        } else {
            // place the right card from middle cards on top of discardPile
            game.board.discardPileRight = gameMiddleCards[2]

            // shift middleCards one to the right
            gameMiddleCards[2] = gameMiddleCards[1]
            gameMiddleCards[1] = gameMiddleCards[0]

            // replace free spot with card from drawPile
            gameMiddleCards[0] = game.board.drawPile.removeFirst()
        }


        // refresh the GUI
        onAllRefreshables { refreshAfterShiftCards(player, game.board) }

        // player finished shifting
        player.hasShifted = true
    }

    /**
     * Swaps all the cards from [Player]'s open cards with the middle cards.
     * Calls nextPlayer() afterward.
     *
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

        onAllRefreshables { refreshAfterSwapCard(player, game.board) }

        rootService.pokerGameService.nextPlayer()
    }

    /**
     * Swaps one chosen card from [Player]'s open cards with one chosen middle card.
     * Calls nextPlayer() afterward.
     *
     * @param handIndex the index of the open hand card to be swapped
     * @param middleIndex the index of the middle card to be swapped
     *
     * @throws IndexOutOfBoundsException if the index of either the middle card or the hand card is out of bounds
     */
    fun swap(handIndex: Int, middleIndex: Int) {
        //retrieve current game state
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]

        //check if indices are in bounds
        if (handIndex !in 0..2 || middleIndex !in 0..2) {
            throw IndexOutOfBoundsException()
        }

        if (!player.hasShifted) {
            throw IllegalStateException("You need to shift first!")
        }

        val temp = player.openCards[handIndex]
        player.openCards[handIndex] = game.board.middleCards[middleIndex]
        game.board.middleCards[middleIndex] = temp

        onAllRefreshables { refreshAfterSwapCard(player, game.board) }

        rootService.pokerGameService.nextPlayer()
    }


    /**
     * Skips swapping cards and simply calls nextPlayer().
     *
     * @throws IllegalStateException if the player has not shifted yet.
     */

    fun pass() {
        // retrieve current game from root service
        val game = checkNotNull(rootService.currentGame)

        val player = game.players[game.currentPlayer]

        if (!player.hasShifted) {
            throw IllegalStateException("You need to shift first!")
        }
        rootService.pokerGameService.nextPlayer()
    }
}
