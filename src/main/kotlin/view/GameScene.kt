package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color


/**
 * The [GameScene] class represents the scene where the poker game is played.
 * It displays the game board and controls for players to interact with during the game.
 *
 * @property rootService The root service for accessing game-related functionalities.
 * @constructor Creates a new instance of [GameScene] with the specified [rootService].
 */
class GameScene(val rootService: RootService) :
    BoardGameScene(1920, 1080), Refreshable {

    // Initialize game components and initializer
    val gameComponents: GameComponents = GameComponents(rootService, this)
    val gameInitializer: GameInitializer = GameInitializer(this)

    /**
     * Hides all card views in the list of initial card views.
     */
    private fun List<InitialCardView>.hide() {
        this.forEach { it.isVisible = false }
    }

    /**
     * Shows all card views in the list of initial card views.
     */

    // Implementation of refresh methods
    override fun refreshAfterStartGame() {
        // Clear existing game data
        gameInitializer.cardMap.clear()
        gameInitializer.discardPileLeft.clear()
        gameInitializer.discardPileRight.clear()
        // Refresh after starting the game
        refreshAfterStartRound()
    }

    override fun refreshAfterStartRound(){
        // Code to refresh the scene after starting a new round
        val game = rootService.currentGame ?: throw IllegalStateException("No started game found.")
        GameData.middleCards = game.board.middleCards
        val size = game.players.size
        val currentPlayer = game.players[game.currentPlayer]
        val nextPlayerIndex = (game.currentPlayer + 1) % size
        val nextPlayer = game.players[nextPlayerIndex]
        val afterNextPlayerIndex = (game.currentPlayer + 2) % size
        val lastPlayerIndex = (game.currentPlayer + 3) % size

        gameComponents.roundLabel.text = "ROUNDS LEFT: ${game.rounds}"
        gameComponents.bottomNameLabel.text = currentPlayer.name
        gameComponents.nextPlayerLabel.text = "NEXT PLAYER: ${nextPlayer.name}"

        when (size) {
            3 -> {
                gameComponents.topNameLabel.isVisible = false
                gameInitializer.topPlayerOpenCards.hide()

                gameInitializer.setupPlayerUI(game.players[nextPlayerIndex], gameComponents.rightNameLabel,
                    gameInitializer.rightPlayerOpenCardsLeft,
                    gameInitializer.rightPlayerOpenCardsMiddle, gameInitializer.rightPlayerOpenCardsRight)
                gameInitializer.setupPlayerUI(game.players[afterNextPlayerIndex], gameComponents.leftNameLabel,
                    gameInitializer.leftPlayerOpenCardsLeft,
                    gameInitializer.leftPlayerOpenCardsMiddle, gameInitializer.leftPlayerOpenCardsRight)
                    }
            4 -> {
                gameInitializer.setupPlayerUI(game.players[nextPlayerIndex], gameComponents.rightNameLabel,
                    gameInitializer.rightPlayerOpenCardsLeft,
                    gameInitializer.rightPlayerOpenCardsMiddle, gameInitializer.rightPlayerOpenCardsRight)
                gameInitializer.setupPlayerUI(game.players[afterNextPlayerIndex], gameComponents.topNameLabel,
                    gameInitializer.topPlayerOpenCardsLeft,
                    gameInitializer.topPlayerOpenCardsMiddle, gameInitializer.topPlayerOpenCardsRight)
                gameInitializer.setupPlayerUI(game.players[lastPlayerIndex], gameComponents.leftNameLabel,
                    gameInitializer.leftPlayerOpenCardsLeft,
                    gameInitializer.leftPlayerOpenCardsMiddle, gameInitializer.leftPlayerOpenCardsRight)
            }
            2 -> {
                gameComponents.leftNameLabel.isVisible = false
                gameComponents.rightNameLabel.isVisible = false
                gameInitializer.setupPlayerUI(game.players[nextPlayerIndex], gameComponents.topNameLabel,
                    gameInitializer.topPlayerOpenCardsLeft,
                    gameInitializer.topPlayerOpenCardsMiddle, gameInitializer.topPlayerOpenCardsRight)
                gameInitializer.leftPlayerOpenCards.hide()
                gameInitializer.rightPlayerOpenCards.hide()
            }
        }
        gameInitializer.initializeBoard(game, currentPlayer)
    }

    override fun refreshAfterShiftCard(left: Boolean) {
        // Code to refresh the scene after shifting a card
        val game = rootService.currentGame
        checkNotNull(game)

        gameComponents.refreshComponentsAfterShiftCard()

        val discardPile = if (left) game.board.discardPileLeft else game.board.discardPileRight
        discardPile?.let { gameInitializer.moveCardView(gameInitializer.cardMap.forward(it),
            if (left) gameInitializer.discardPileLeft else gameInitializer.discardPileRight) }

        // Restliche Aktualisierungen der Kartenansichten
        gameInitializer.moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)
        gameInitializer.moveCardView(gameInitializer.cardMap.forward(game.board.drawPile.first()),
            gameInitializer.drawPile1, hidden = true)
        gameInitializer.moveCardViews(game.players[game.currentPlayer].hiddenCards,
            gameInitializer.currentPlayerHiddenCards, hidden = true
        )
    }

    override fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int?, middleIndex: Int?) {
        // Code to refresh the scene after swapping cards
        val game = rootService.currentGame
        checkNotNull(game)

        // Deaktiviere entsprechende Buttons
        gameComponents.refreshAfterSwapCard()

        // Restliche Aktualisierungen der Kartenansichten
        gameInitializer.moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)

        // Aktualisiere die offenen Karten der Spieler je nach aktuellem Spieler
        val currentPlayerIndex = game.currentPlayer
        val currentPlayer = game.players[currentPlayerIndex]

        gameInitializer.moveCardViews(currentPlayer.openCards, listOf(gameInitializer.currentPlayerOpenCardsLeft,
            gameInitializer.currentPlayerOpenCardsMiddle, gameInitializer.currentPlayerOpenCardsRight)
        )
        for(card in (gameInitializer.middleCardsViews + gameInitializer.currentPlayerOpenCards)) {
            card.visual = CompoundVisual(
                ColorVisual(Color(255, 255, 255, 50)))
            card.isDisabled = true
        }
        gameInitializer.moveCardViews(game.players[game.currentPlayer].hiddenCards,
            gameInitializer.currentPlayerHiddenCards, hidden = true
        )
    }

    override fun refreshAfterNextPlayer() {
        // Code to refresh the scene after moving to the next player
        gameComponents.refreshAfterNextPlayer()
        refreshAfterStartRound()
    }

    override fun refreshAfterMiddleCardSelected() {
        // Code to refresh the scene after selecting a middle card
        for(card in gameInitializer.middleCardsViews + gameInitializer.currentPlayerOpenCards) {
            card.isDisabled = card in gameInitializer.middleCardsViews
        }
        when(gameInitializer.selectedCard) {
            "right" -> {
                gameComponents.selectedButton.posX += 300
                gameInitializer.selectedCardIndex = 2
            }
            "middle" -> {
                gameComponents.selectedButton.posX += 150
                gameInitializer.selectedCardIndex = 1
            }
        }
        gameComponents.selectedButton.isVisible = true
        gameComponents.selectACardLabel.apply{
            posY += 90
            width += 50
            posX -= 20
            text = "SELECT AN OPEN HAND CARD"
        }
    }

    init{
        // Initialize scene components
        background = ColorVisual(108, 168, 59) // Dark green for "Casino table" flair
        opacity = .5
        addComponents(
            // Add all game components to the scene
            gameComponents.roundLabel, gameComponents.bottomNameLabel, gameComponents.rightNameLabel,
            gameComponents.topNameLabel, gameComponents.leftNameLabel, gameInitializer.currentPlayerHiddenCardLeft,
            gameInitializer.currentPlayerHiddenCardRight,
            gameInitializer.discardPileLeft, gameInitializer.discardPileRight,
            gameInitializer.currentPlayerOpenCardsLeft, gameInitializer.currentPlayerOpenCardsRight,
            gameInitializer.currentPlayerOpenCardsMiddle, gameInitializer.rightPlayerOpenCardsLeft,
            gameInitializer.rightPlayerOpenCardsMiddle,gameInitializer.rightPlayerOpenCardsRight,
            gameInitializer.topPlayerOpenCardsLeft, gameInitializer.topPlayerOpenCardsRight,
            gameInitializer.topPlayerOpenCardsMiddle, gameInitializer.leftPlayerOpenCardsLeft,
            gameInitializer.leftPlayerOpenCardsRight,gameInitializer.leftPlayerOpenCardsMiddle,
            gameInitializer.drawPile1, gameInitializer.drawPile2, gameInitializer.drawPile3,
            gameComponents.showCardsButton, gameInitializer.middleCardLeft,
            gameInitializer.middleCardMiddle, gameInitializer.middleCardRight, gameComponents.shiftLeftButton,
            gameComponents.shiftRightButton,gameComponents.swapOneButton, gameComponents.swapAllButton,
            gameComponents.passButton, gameComponents.nextPlayerLabel, gameComponents.startTurnButton,
            gameComponents.selectedButton, gameComponents.selectACardLabel
        )
    }
}