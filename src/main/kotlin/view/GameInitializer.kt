package view

import entity.Card
import service.CardImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

class GameInitializer(gameScene: GameScene) {
    val cardMap = BidirectionalMap<Card, CardView>()
    val cardImageLoader = CardImageLoader()
    var currentPlayerOpenCardsLeft = InitialCardView(posX = 755, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(0, selectedCardIndex)
        }
    }
    var currentPlayerOpenCardsMiddle = InitialCardView(posX = 905, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(1, selectedCardIndex)
        }
    }
    var currentPlayerOpenCardsRight = InitialCardView(posX = 1055, posY = 800).apply {
        this.isDisabled = true
        onMouseClicked = {
            visual = CompoundVisual(
                ColorVisual(Color(255, 29, 29, 100))
            )
            gameScene.rootService.playerActionService.swap(2, selectedCardIndex)
        }
    }

    val currentPlayerOpenCards = listOf(
        currentPlayerOpenCardsLeft, currentPlayerOpenCardsMiddle, currentPlayerOpenCardsRight)

    var currentPlayerHiddenCardLeft = InitialCardView(posX = 300, posY = 800)
    var currentPlayerHiddenCardRight = InitialCardView(posX = 450, posY = 800)

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
    val drawPile = InitialCardView(posX = 1550, posY = 800)

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
}