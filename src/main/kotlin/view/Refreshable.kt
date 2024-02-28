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
     * @param left a boolean value indicating the direction of the shift
     */
    fun refreshAfterShiftCard(left: Boolean) {}

    /**
     * perform refreshes that are necessary after a player swapped a card from their
     * open cards with a card from the middle
     *
     * @param middleIndex the index of the middle card to be swapped
     * @param handIndex the index of the hand card to be swapped
     *
     */
    fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int? = null, middleIndex: Int? = null) {}

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