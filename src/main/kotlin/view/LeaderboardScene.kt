package view

import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

class LeaderboardScene: MenuScene(
    600, 800, ColorVisual.LIGHT_GRAY), Refreshable {

    val p1Label = Label(
        width = 350, height = 35,
        posX = 150, posY = 225,
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    )

    val p2Label = Label(
        width = 350, height = 35,
        posX = 150, posY = 275,
        text = "PLAYER 2: ",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    )

    val p3Label = Label(
        width = 350, height = 35,
        posX = 150, posY = 325,
        text = "PLAYER 3: " + "ROYAL FLUSH",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        this.isVisible = false
    }

    val p4Label = Label(
        width = 350, height = 35,
        posX = 150, posY = 375,
        text = "PLAYER 4: ",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    ).apply { this.isVisible = false }

    private val headlineLabel = Label(
        width = 550, height = 50, posX = 30, posY = 50,
        text = "GAME OVER!",
        font = Font(size = 30, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )
    val quitButton = Button(
        width = 140, height = 35,
        posX = 150, posY = 625,
        text = "QUIT",
        font = Font(size = 18, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(150, 50, 50)}

    val newGameButton = Button(
        width = 140, height = 35,
        posX = 310, posY = 625,
        text = "NEW GAME"
    ).apply {
        font = Font(size = 18, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
        visual = ColorVisual(50, 120, 50)    }
    init {
        background = ColorVisual(50, 70, 50) // Dark green for "Casino table" flair
        opacity = .9
        addComponents(
            headlineLabel, newGameButton, quitButton,
            p1Label, p2Label, p3Label, p4Label
        )
    }

}