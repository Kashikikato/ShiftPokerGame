package view

import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

class InitialCardView(posX: Number, posY: Number, rotateLeft: Boolean = false, rotateRight: Boolean = false) :
    CardStack<CardView>(height = 200, width = 130, posX = posX, posY = posY) {
    init {
        visual = CompoundVisual(
            ColorVisual(Color(255, 255, 255, 50)),
        ).apply {
            if (rotateLeft) rotation = 90.0
            else if (rotateRight) rotation = -90.0
        }
    }

}