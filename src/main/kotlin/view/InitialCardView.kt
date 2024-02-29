package view

import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

/**
 * The [InitialCardView] class represents a visual component for displaying an initial card stack.
 * It is used to visually represent the initial cards dealt in the game.
 *
 * @property posX The x-coordinate position of the card stack.
 * @property posY The y-coordinate position of the card stack.
 * @property rotateLeft Flag indicating whether the card stack should be rotated to the left.
 * @property rotateRight Flag indicating whether the card stack should be rotated to the right.
 * @constructor Creates a new instance of [InitialCardView].
 */
class InitialCardView(
    posX: Number,
    posY: Number,
    rotateLeft: Boolean = false,
    rotateRight: Boolean = false
) : CardStack<CardView>(height = 200, width = 130, posX = posX, posY = posY) {

    init {
        visual = CompoundVisual(
            ColorVisual(Color(255, 255, 255, 50))
        ).apply {
            if (rotateLeft) rotation = 90.0
            else if (rotateRight) rotation = -90.0
        }
    }
}
