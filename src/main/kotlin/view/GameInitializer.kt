package view

import entity.Card
import entity.Player
import entity.ShiftPokerGame
import service.CardImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * The [GameInitializer] class is responsible for initializing the game components and managing the game board.
 *
 * @param gameScene The [GameScene] associated with this game initializer.
 * @constructor Creates a new instance of [GameInitializer] with the specified game scene.
 */
class GameInitializer(gameScene: GameScene) {
    val cardMap = BidirectionalMap<Card, CardView>()
    private val cardImageLoader = CardImageLoader()
    var currentPlayerOpenCardsLeft = InitialCardView(posX = 755, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(0, selectedCardIndex)
            selectedCardIndex = 0
        }
    }
    var currentPlayerOpenCardsMiddle = InitialCardView(posX = 905, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(1, selectedCardIndex)
            selectedCardIndex = 0
        }
    }
    var currentPlayerOpenCardsRight = InitialCardView(posX = 1055, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(2, selectedCardIndex)
            selectedCardIndex = 0
        }
    }

    val currentPlayerOpenCards = listOf(
        currentPlayerOpenCardsLeft, currentPlayerOpenCardsMiddle, currentPlayerOpenCardsRight)

    var currentPlayerHiddenCardLeft = InitialCardView(posX = 350, posY = 800)
    var currentPlayerHiddenCardRight = InitialCardView(posX = 400, posY = 800)

    val currentPlayerHiddenCards = listOf(
        currentPlayerHiddenCardLeft, currentPlayerHiddenCardRight)

    var selectedCard = "none"
    var selectedCardIndex = 0

    var middleCardLeft = InitialCardView(posX = 755, posY = 445).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            selectedCard = "left"
            gameScene.gameComponents.selectedButton.isVisible = true
            gameScene.refreshAfterMiddleCardSelected()
        }
    }

    var middleCardMiddle = InitialCardView(posX = 905, posY = 445).apply {
        this.isDisabled = true
        onMouseClicked = {
            if(!this.isDisabled) {
                visual = CompoundVisual(
                    ColorVisual(Color(255, 29, 29, 100))
                )
                selectedCard = "middle"
                gameScene.gameComponents.selectedButton.isVisible = true
                gameScene.refreshAfterMiddleCardSelected()
            }
        }
    }
    var middleCardRight = InitialCardView(posX = 1055, posY = 445).apply {
        this.isDisabled = true
        onMouseClicked = {
            if(!this.isDisabled) {
                visual = CompoundVisual(
                    ColorVisual(Color(255, 29, 29, 100))
                )
                selectedCard = "right"
                gameScene.gameComponents.selectedButton.isVisible = true
                gameScene.refreshAfterMiddleCardSelected()
            }
        }
    }

    val middleCardsViews: MutableList<InitialCardView> = mutableListOf(
        middleCardLeft, middleCardMiddle, middleCardRight)
    var discardPileLeft = InitialCardView(posX = 420, posY = 445)
    var discardPileRight = InitialCardView(posX = 1390, posY = 445)

    val drawPile1 = InitialCardView(posX = 1550, posY = 800)
    val drawPile2 = InitialCardView(posX = 1565, posY = 802)
    val drawPile3 = InitialCardView(posX = 1580, posY = 802)

    var rightPlayerOpenCardsLeft = InitialCardView(posX = 1655, posY = 240, rotateRight = true)
    var rightPlayerOpenCardsMiddle = InitialCardView(posX = 1655, posY = 390, rotateRight = true)
    var rightPlayerOpenCardsRight = InitialCardView(posX = 1655, posY = 540, rotateRight = true)

    val rightPlayerOpenCards = listOf(
        rightPlayerOpenCardsLeft, rightPlayerOpenCardsMiddle, rightPlayerOpenCardsRight
    )

    var topPlayerOpenCardsLeft = InitialCardView(posX = 755, posY = 90)
    var topPlayerOpenCardsMiddle = InitialCardView(posX = 905, posY = 90)
    var topPlayerOpenCardsRight = InitialCardView(posX = 1055, posY = 90)

    val topPlayerOpenCards = listOf(
        topPlayerOpenCardsLeft, topPlayerOpenCardsMiddle, topPlayerOpenCardsRight
    )

    var leftPlayerOpenCardsLeft = InitialCardView(posX = 135, posY = 240, rotateLeft = true)
    var leftPlayerOpenCardsMiddle = InitialCardView(posX = 135, posY = 390, rotateLeft = true)
    var leftPlayerOpenCardsRight = InitialCardView(posX = 135, posY = 540, rotateLeft = true)

    val leftPlayerOpenCards = listOf(
        leftPlayerOpenCardsLeft, leftPlayerOpenCardsMiddle, leftPlayerOpenCardsRight
    )

    /**
     * Initializes the card view for a single card.
     *
     * @param card The card to initialize the view for.
     * @param singleCardView The initial card view for the card.
     */
    fun initializeCardView(card: Card, singleCardView: InitialCardView) {
        val cardView = CardView(200, 130, front =  ImageVisual(
            cardImageLoader.frontImageFor(card.suit, card.value)),
            back = ImageVisual(cardImageLoader.backImage)
        )
        singleCardView.add(cardView)
        cardMap.add(card, cardView)
    }

    /**
     * Initializes the game board based on the current game state.
     *
     * @param game The current game state.
     * @param currentPlayer The current player.
     */
    fun initializeBoard(game: ShiftPokerGame, currentPlayer: Player) {

        initializePileView(game.board.drawPile, drawPile1)

        initializePlayerUI(currentPlayer, currentPlayerOpenCardsLeft,
            currentPlayerOpenCardsMiddle, currentPlayerOpenCardsRight)
        initializePlayerUI(currentPlayer, currentPlayerHiddenCardLeft,
            currentPlayerHiddenCardRight, null)

        game.board.middleCards.forEachIndexed { index, card ->
            initializeCardView(card, middleCardsViews[index])
        }
        moveCardView(cardMap.forward(game.board.drawPile[1]),
            drawPile2, hidden = true )
        moveCardView(cardMap.forward(game.board.drawPile[2]),
            drawPile3, hidden = true )
        moveCardView(cardMap.forward(game.board.drawPile[0]), drawPile1 )
        drawPile2.toFront()
        drawPile1.toFront()


        moveCardViews(game.board.middleCards, middleCardsViews)
    }

    private fun initializePileView(cardDeck: List<Card>, deckView: InitialCardView) {
        cardDeck.forEach { card -> initializeCardView(card, deckView) }
    }

    private fun moveCardView(cardView: CardView, card: InitialCardView,
                             flip: Boolean = false, hidden: Boolean = false) {
        if (flip) {
            cardView.currentSide = when (cardView.currentSide) {
                CardView.CardSide.BACK -> CardView.CardSide.FRONT
                CardView.CardSide.FRONT -> CardView.CardSide.BACK
            }
        } else cardView.currentSide = CardView.CardSide.FRONT
        if(hidden) {
            cardView.currentSide = CardView.CardSide.BACK
        }

        cardView.removeFromParent()
        card.add(cardView)
    }

    private fun moveCardViews(cards: List<Card>, cardViews: List<InitialCardView>,
                              flip: Boolean = false, hidden: Boolean = false) {
        cards.forEachIndexed { index, card ->
            if(cardViews.size == 1) moveCardView(cardMap.forward(card),
                cardViews[0], flip = flip, hidden = hidden)
            else moveCardView(cardMap.forward(card), cardViews[index], flip = flip, hidden = hidden)
        }
    }

    private fun initializePlayerUI(player: Player, leftCardView: InitialCardView,
                                   rightCardView: InitialCardView, middleCardView: InitialCardView?) {
        if(middleCardView == null) {
            initializeCardView(player.hiddenCards[0], leftCardView)
            initializeCardView(
                player.hiddenCards[1], rightCardView
            )
            moveCardViews(
                player.hiddenCards,
                currentPlayerHiddenCards, hidden = true
            )
        }
        else {
            initializeCardView(player.openCards[0], leftCardView)
            initializeCardView(player.openCards[1], middleCardView)
            initializeCardView(player.openCards[2], rightCardView)
            moveCardViews(player.openCards, listOf(currentPlayerOpenCardsLeft,
                currentPlayerOpenCardsMiddle, currentPlayerOpenCardsRight))
        }
    }
}
