package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * Manages and controls the user interface components of the game.
 *
 * @property rootService The root service of the game.
 * @property gameScene The GameScene of the game.
 */
class GameComponents(private val rootService: RootService, private val gameScene: GameScene) {
    // Definition of UI components

    /**
     * Label to display the current round of the game.
     */
    val roundLabel = Label(
        width = 250, height = 50,
        posX = 0, posY = 10,
        font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     * Label to display the name of the next player.
     */
    val nextPlayerLabel = Label(
        width = 450, height = 50,
        posX = 1500, posY = 10,
        font = Font(size = 25, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     * Button to start a turn.
     */
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

    /**
     * Label to display the name of the bottom player.
     */
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

    /**
     * Label to display the name of the top player.
     */
    val topNameLabel = Label(
        width = 200, height = 50,
        posX = 870, posY = 20,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     * Label to display the name of the right-side player.
     */
    val rightNameLabel = Label(
        width = 200, height = 50,
        posX = 1775, posY = 480,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        rotate(-90)
    }

    /**
     * Label to display the name of the left-side player.
     */
    val leftNameLabel = Label(
        width = 200, height = 50,
        posX = -50, posY = 500,
        font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        rotate(90)
    }

    /**
     * Button to show the current player's hidden cards.
     */
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
            gameScene.gameInitializer.moveCardViews(hiddenCards,
                gameScene.gameInitializer.currentPlayerHiddenCards, flip = true)
            visual = if (((visual as? ColorVisual)?.color?.red ?: 0) > 100) {
                ColorVisual(96, 60, 30)
            } else ColorVisual(250, 90, 90)
        }
    }

    /**
     * Button to shift cards to the left on the game board.
     */
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

    /**
     * Button to shift cards to the right on the game board.
     */
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

    /**
     * Button to swap a single card.
     */
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
            startTurnButton.isVisible = false
            selectACardLabel.isVisible = true
        }
    }

    /**
     * Button to swap all cards.
     */
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

    /**
     * Button for a player to pass their turn.
     */
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

    /**
     * Label indicating a card is selected.
     */
    val selectedButton = Label(
        width = 100, height = 30,
        posX = 770, posY = 665,
        text = "SELECTED",
        font = Font(size = 14, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
    ).apply {
        visual = ColorVisual(96, 60, 30)
        this.isVisible = false
    }

    /**
     * Label prompting the player to select a card.
     */
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

    /**
     * Refreshes UI components after shifting a card on the game board.
     */
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

    /**
     * Refreshes UI components after swapping a card.
     */
    fun refreshAfterSwapCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Deactivate corresponding buttons
        swapOneButton.disable()
        passButton.disable()
        swapAllButton.disable()

        swapOneButton.font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
        swapAllButton.font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)
        passButton.font = Font(color = Color.BLACK, size = 18, fontWeight = Font.FontWeight.BOLD)

        gameScene.gameInitializer.middleCardsViews.forEach { it.isDisabled = false }

        startTurnButton.isVisible = true

        selectACardLabel.apply {
            width = 200.0
            height = 30.0
            posX = 870.0
            posY = 665.0
            text = "SELECT A MIDDLE CARD"
            this.isVisible = false
        }
        selectedButton.apply {
            isVisible = false
            posX = 770.0
        }
    }

    /**
     * Refreshes UI components after switching to the next player.
     */
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
