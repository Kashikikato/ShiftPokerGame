package view
import entity.*
import service.AbstractRefreshingService

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */
interface Refreshable {

    /**
     * perform refreshes that are necessary after a game started
     */
    fun refreshAfterStartGame() {}

    /**
     * perform refreshes that are necessary after a player shifted the middle cards
     *
     * @param shiftingPlayer the player that shifted the middle cards
     * @param boardCards the cards on the board after the player shifted the middle cards
     */
    fun refreshAfterShiftCard(shiftingPlayer: Player, boardCards: Board) {}

    /**
     * perform refreshes that are necessary after a player swapped a card from their
     * open cards with a card from the middle
     *
     * @param player the player that swapped one card or all cards
     * @param boardCards the cards on the board after the player swapped one card or all cards
     *
     */
    fun refreshAfterSwapCard(player: Player, boardCards: Board) {}

    /**
     * perform refreshes that are necessary after a player finished their turn
     *
     * @param player the player that finished their turn
     */
    fun refreshAfterNextPlayer(player: Player) {}

    /**
     * perform refreshes that are necessary after the last round was played
     *
     * @param result a map of the scoreboard
     */
    fun refreshAfterGameEnd(result: Map<Player,String>) {}

}