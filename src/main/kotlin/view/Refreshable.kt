package view
import entity.Player
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
     * perform refreshes that are necessary after a round started
     */
    fun refreshAfterStartRound(){}


        /**
     * perform refreshes that are necessary after a player shifted the middle cards
     *
     * @param left a boolean value indicating the direction of the shift
     */
    fun refreshAfterShiftCard(left: Boolean) {}

    /**
     * perform refreshes that are necessary after a player swaps or chooses not to
     *
     */
    fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int? = null, middleIndex: Int? = null) {}

    /**
     * perform refreshes that are necessary after a player selected a middle card for swapping
     *
     */
    fun refreshAfterMiddleCardSelected() {}

    /**
     * perform refreshes that are necessary after a player finished their turn
     *
     */

    fun refreshAfterNextPlayer() {}

    /**
     * Refreshes the user interface after the end of the game with the provided results.
     * @param result A list of pairs containing player objects and their corresponding game results as strings.
     *
     * Each pair represents one player and their result in the game.
     */
    fun refreshAfterGameEnd(result: List<Pair<Player, String>>) {}

}