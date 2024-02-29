package view

import entity.Card
import entity.Player
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Label
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
     * Moves a card view to a specified card stack.
     *
     * @param cardView The card view to move.
     * @param card The card stack to move the card view to.
     * @param flip Flag indicating whether to flip the card view.
     * @param hidden Flag indicating whether the card should be hidden.
     */
    private fun moveCardView(
        cardView: CardView,
        card: InitialCardView,
        flip: Boolean = false,
        hidden: Boolean = false
    ) {
        if (flip) {
            cardView.currentSide = when (cardView.currentSide) {
                CardView.CardSide.BACK -> CardView.CardSide.FRONT
                CardView.CardSide.FRONT -> CardView.CardSide.BACK
            }
        } else cardView.currentSide = CardView.CardSide.FRONT
        if (hidden) {
            cardView.currentSide = CardView.CardSide.BACK
        }

        cardView.removeFromParent()
        card.add(cardView)
    }

    /**
     * Moves multiple card views to specified card stacks.
     *
     * @param cards The list of cards to move.
     * @param cardViews The list of card stacks to move the card views to.
     * @param flip Flag indicating whether to flip the card views.
     * @param hidden Flag indicating whether the cards should be hidden.
     */
    fun moveCardViews(
        cards: List<Card>,
        cardViews: List<InitialCardView>,
        flip: Boolean = false,
        hidden: Boolean = false
    ) {
        cards.forEachIndexed { index, card ->
            val targetCardView = if (cardViews.size == 1) cardViews[0] else cardViews[index]
            moveCardView(
                gameInitializer.cardMap.forward(card),
                targetCardView,
                flip = flip,
                hidden = hidden
            )
        }
    }

    /**
     * Hides all card views in the list of initial card views.
     */
    private fun List<InitialCardView>.hide() {
        this.forEach { it.isVisible = false }
    }

    /**
     * Shows all card views in the list of initial card views.
     */
    private fun List<InitialCardView>.show() {
        this.forEach { it.isVisible = true }
    }

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

                setupPlayerUI(game.players[nextPlayerIndex], gameComponents.rightNameLabel,
                    gameInitializer.rightPlayerOpenCardsLeft,
                    gameInitializer.rightPlayerOpenCardsMiddle, gameInitializer.rightPlayerOpenCardsRight)
                setupPlayerUI(game.players[afterNextPlayerIndex], gameComponents.leftNameLabel,
                    gameInitializer.leftPlayerOpenCardsLeft,
                    gameInitializer.leftPlayerOpenCardsMiddle, gameInitializer.leftPlayerOpenCardsRight)
                    }
            4 -> {
                setupPlayerUI(game.players[nextPlayerIndex], gameComponents.rightNameLabel,
                    gameInitializer.rightPlayerOpenCardsLeft,
                    gameInitializer.rightPlayerOpenCardsMiddle, gameInitializer.rightPlayerOpenCardsRight)
                setupPlayerUI(game.players[afterNextPlayerIndex], gameComponents.topNameLabel,
                    gameInitializer.topPlayerOpenCardsLeft,
                    gameInitializer.topPlayerOpenCardsMiddle, gameInitializer.topPlayerOpenCardsRight)
                setupPlayerUI(game.players[lastPlayerIndex], gameComponents.leftNameLabel,
                    gameInitializer.leftPlayerOpenCardsLeft,
                    gameInitializer.leftPlayerOpenCardsMiddle, gameInitializer.leftPlayerOpenCardsRight)
            }
            2 -> {
                gameComponents.leftNameLabel.isVisible = false
                gameComponents.rightNameLabel.isVisible = false
                setupPlayerUI(game.players[nextPlayerIndex], gameComponents.topNameLabel,
                    gameInitializer.topPlayerOpenCardsLeft,
                    gameInitializer.topPlayerOpenCardsMiddle, gameInitializer.topPlayerOpenCardsRight)
                gameInitializer.leftPlayerOpenCards.hide()
                gameInitializer.rightPlayerOpenCards.hide()
            }
        }
        gameInitializer.initializeBoard(game, currentPlayer)
    }

    private fun setupPlayerUI(player: Player, nameLabel: Label, leftCardView: InitialCardView,
        middleCardView: InitialCardView, rightCardView: InitialCardView) {
            nameLabel.text = player.name
            nameLabel.isVisible = true
            val cardViews = listOf(leftCardView, middleCardView, rightCardView)
            cardViews.show()
            gameInitializer.initializeCardView(player.openCards[0], leftCardView)
            gameInitializer.initializeCardView(player.openCards[1], middleCardView)
            gameInitializer.initializeCardView(player.openCards[2], rightCardView)
            moveCardViews(player.openCards, listOf(leftCardView, middleCardView, rightCardView))
    }

    override fun refreshAfterShiftCard(left: Boolean) {
        // Code to refresh the scene after shifting a card
        val game = rootService.currentGame
        checkNotNull(game)

        gameComponents.refreshComponentsAfterShiftCard()

        val discardPile = if (left) game.board.discardPileLeft else game.board.discardPileRight
        discardPile?.let { moveCardView(gameInitializer.cardMap.forward(it),
            if (left) gameInitializer.discardPileLeft else gameInitializer.discardPileRight) }

        // Restliche Aktualisierungen der Kartenansichten
        moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)
        moveCardView(gameInitializer.cardMap.forward(game.board.drawPile.first()), gameInitializer.drawPile1)
    }

    override fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int?, middleIndex: Int?) {
        // Code to refresh the scene after swapping cards
        val game = rootService.currentGame
        checkNotNull(game)

        // Deaktiviere entsprechende Buttons
        gameComponents.refreshAfterSwapCard()

        // Restliche Aktualisierungen der Kartenansichten
        moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)

        // Aktualisiere die offenen Karten der Spieler je nach aktuellem Spieler
        val currentPlayerIndex = game.currentPlayer
        val currentPlayer = game.players[currentPlayerIndex]

        moveCardViews(currentPlayer.openCards, listOf(gameInitializer.currentPlayerOpenCardsLeft,
            gameInitializer.currentPlayerOpenCardsMiddle, gameInitializer.currentPlayerOpenCardsRight)
        )
        gameComponents.startTurnButton.isVisible = true
        for(card in (gameInitializer.middleCardsViews + gameInitializer.currentPlayerOpenCards)) {
            card.visual = CompoundVisual(
                ColorVisual(Color(255, 255, 255, 50)))
            card.isDisabled = true
        }
        gameComponents.selectACardLabel.apply {
            width = 200.0
            height = 30.0
            posX = 870.0
            posY = 665.0
            text = "SELECT A MIDDLE CARD"
            this.isVisible = false
        }
        gameComponents.selectedButton.apply {
            isVisible = false
            posX = 770.0
        }

    }

    override fun refreshAfterNextPlayer() {
        // Code to refresh the scene after moving to the next player
        val game = rootService.currentGame
        checkNotNull(game)
        if(game.rounds == 0) {
            rootService.pokerGameService.endGame()
            refreshAfterGameEnd()
        }
        else {
            gameComponents.refreshAfterNextPlayer()
            refreshAfterStartRound()
        }
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