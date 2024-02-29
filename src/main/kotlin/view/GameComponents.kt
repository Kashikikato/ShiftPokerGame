package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

class GameComponents(private val rootService: RootService, private val gameScene: GameScene) {
    val roundLabel = Label(
        width = 250, height = 50,
        posX = 0, posY = 10,
        font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    val nextPlayerLabel = Label(
        width = 450, height = 50,
        posX = 1500, posY = 10,
        font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    val startTurnButton = Button(
        width = 250, height = 50,
        posX = 1600, posY = 70,
        text = "START TURN",
        font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
        ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isVisible = false
        onMouseClicked = {
            rootService.pokerGameService.nextPlayer()
            refreshAfterNextPlayer()
        }
    }

    val bottomNameLabel = Label(
        width = 200, height = 50,
        posX = 870, posY = 1010,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        val game = rootService.currentGame
        if (game != null) {
            text = game.players[game.currentPlayer].name
        }
    }

    val topNameLabel = Label(
        width = 200, height = 50,
        posX = 870, posY = 20,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    val rightNameLabel = Label(
        width = 200, height = 50,
        posX = 1775, posY = 480,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        rotate(-90)
    }

    val leftNameLabel = Label(
        width = 200, height = 50,
        posX = -50, posY = 500,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        rotate(90)
    }


    val showCardsButton = Button(
        width = 100, height = 40,
        posX = 390, posY = 740,
        text = "SHOW",
        font = Font(color = Color.WHITE, fontWeight = Font.FontWeight.BOLD, size = 18)
    ).apply {
        visual = ColorVisual(96, 60, 30)
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
            val hiddenCards = game.players[game.currentPlayer].hiddenCards
            gameScene.moveCardViews(hiddenCards, gameScene.gameInitializer.currentPlayerHiddenCards, flip = true)
            visual = if (((visual as? ColorVisual)?.color?.red ?: 0) > 100) {
                ColorVisual(96, 60, 30)
            } else ColorVisual(250, 90, 90)
        }
    }

    val shiftLeftButton = Button(
        width = 100, height = 50,
        posX = 610, posY = 520,
        text = "LEFT",
        font = Font(color = Color.WHITE, size = 18, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        visual = ColorVisual(96, 60, 30)
        if (!this.isDisabled)
            onMouseClicked = {
                val game = rootService.currentGame
                checkNotNull(game)
                visual = ColorVisual(250, 90, 90)
                rootService.playerActionService.shift(true)
                gameScene.refreshAfterShiftCard(true)
            }
    }

    val shiftRightButton = Button(
        width = 100, height = 50,
        posX = 1230, posY = 520,
        text = "RIGHT",
        font = Font(color = Color.WHITE, size = 18, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        visual = ColorVisual(96, 60, 30)
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.shift(false)
            gameScene.refreshAfterShiftCard(false)
        }
    }

    private val shiftButtons: List<Button> = listOf(shiftRightButton, shiftLeftButton)

    val swapOneButton = Button(
        width = 150, height = 50,
        posX = 1300, posY = 803,
        text = "SWAP ONE",
        font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.disable()
        onMouseClicked = {
            visual = ColorVisual(250, 90, 90)
            refreshAfterSwapCard()
        }
    }

    val swapAllButton = Button(
        width = 150, height = 50,
        posX = 1300, posY = 875,
        text = "SWAP ALL",
        font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isDisabled = true
        onMouseClicked = {
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.swapAll()
            gameScene.refreshAfterSwapCard(all = true, pass = false)
        }
    }

    val passButton = Button(
        width = 150, height = 50,
        posX = 1300, posY = 947,
        text = "PASS",
        font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isDisabled = true
        onMouseClicked = {
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.pass()
            gameScene.refreshAfterSwapCard(all = false, pass = true)
        }
    }

    private val swapButtons: List<Button> = listOf(swapOneButton, swapAllButton, passButton)

    val selectedButton = Label(
        width = 100, height = 30,
        posX = 770, posY = 665,
        text = "SELECTED",
        font = Font(size = 14, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isVisible = false
    }

    val selectACardLabel = Label(
        width = 200, height = 30,
        posX = 870, posY = 665,
        text = "SELECT A MIDDLE CARD",
        font = Font(size = 14, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isVisible = false
    }


    private fun Button.enable() {
        this.isDisabled = false
    }

    private fun Button.disable() {
        this.isDisabled = true
    }

    fun refreshComponentsAfterShiftCard() {
        swapOneButton.enable()
        passButton.enable()
        swapAllButton.enable()

        passButton.font = Font(color = Color.WHITE, size = 18, fontWeight = Font.FontWeight.BOLD)
        swapAllButton.font = Font(color = Color.WHITE, size = 18, fontWeight = Font.FontWeight.BOLD)
        swapOneButton.font = Font(color = Color.WHITE, size = 18, fontWeight = Font.FontWeight.BOLD)

        shiftLeftButton.disable()
        shiftRightButton.disable()

        shiftLeftButton.font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
        shiftRightButton.font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
    }

    fun refreshAfterSwapCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Deaktiviere entsprechende Buttons
        swapOneButton.disable()
        passButton.disable()
        swapAllButton.disable()

        selectACardLabel.isVisible = true

        gameScene.gameInitializer.middleCardsViews.forEach { it.isDisabled = false }
    }

    fun refreshAfterNextPlayer() {
        startTurnButton.apply {
            isVisible = false
            visual = ColorVisual(96, 60, 30)
            font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
        }
        shiftLeftButton.apply {
            visual = ColorVisual(96, 60, 30)
            font = Font(size = 18, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
            this.isDisabled = false
        }
        for (button in shiftButtons) {
            button.apply {
                visual = ColorVisual(96, 60, 30)
                font = Font(size = 18, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                this.isDisabled = false
            }
        }
        for (button in swapButtons) {
            button.apply {
                visual = ColorVisual(96, 60, 30)
                font = Font(size = 18, color = Color.BLACK, fontWeight = Font.FontWeight.BOLD)
                this.isDisabled = true
            }
        }
        showCardsButton.apply {
            visual = ColorVisual(96, 60, 30)
            font = Font(size = 18, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
        }
    }
}