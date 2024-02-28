package view

import entity.Card
import service.CardImageLoader
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

    private fun initializeCardView(cardDeck: List<Card>, deckView: InitialCardView, cardImageLoader: CardImageLoader) {
        for (card in cardDeck) {
            initializeSingleCardView(card, deckView, cardImageLoader)
        }
    }

    private fun initializeSingleCardView(
        card: Card,
        singleCardView: InitialCardView,
        cardImageLoader: CardImageLoader
    ) {
        val cardView = CardView(
            height = 200,
            width = 130,
            front = ImageVisual(cardImageLoader.frontImageFor(card.suit, card.value)),
            back = ImageVisual(cardImageLoader.backImage)
        )
        singleCardView.add(cardView)
        cardMap.add(card to cardView)
    }

    private fun moveCardView(cardView: CardView, card: InitialCardView, flip: Boolean = false) {
        if (flip) {
            when (cardView.currentSide) {
                CardView.CardSide.BACK -> cardView.showFront()
                CardView.CardSide.FRONT -> cardView.showBack()
            }
        }
        else cardView.showFront()

        cardView.removeFromParent()
        card.add(cardView)
    }

    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private val cardImageLoader = CardImageLoader()

    private var currentPlayerOpenCardsLeft = InitialCardView(posX = 755, posY = 800)
    private var currentPlayerOpenCardsMiddle = InitialCardView(posX = 905, posY = 800)
    private var currentPlayerOpenCardsRight = InitialCardView(posX = 1055, posY = 800)

    private var currentPlayerHiddenCardLeft = InitialCardView(posX = 300, posY = 800)
    private var currentPlayerHiddenCardRight = InitialCardView(posX = 450, posY = 800)

    private var middleCardLeft = InitialCardView(posX = 755, posY = 445)
    private var middleCardMiddle = InitialCardView(posX = 905, posY = 445)
    private var middleCardRight = InitialCardView(posX = 1055, posY = 445)

    private var discardPileLeft = InitialCardView(posX = 420, posY = 445)
    private var discardPileRight = InitialCardView(posX = 1390, posY = 445)

    private val drawPile = InitialCardView(posX = 1550, posY = 800)

    private var rightPlayerOpenCardsLeft = InitialCardView(posX = 1655, posY = 240, rotateRight = true)
    private var rightPlayerOpenCardsMiddle = InitialCardView(posX = 1655, posY = 390, rotateRight = true)
    private var rightPlayerOpenCardsRight = InitialCardView(posX = 1655, posY = 540, rotateRight = true)

    private var topPlayerOpenCardsLeft = InitialCardView(posX = 755, posY = 90)
    private var topPlayerOpenCardsMiddle = InitialCardView(posX = 905, posY = 90)
    private var topPlayerOpenCardsRight = InitialCardView(posX = 1055, posY = 90)

    private var leftPlayerOpenCardsLeft = InitialCardView(posX = 135, posY = 240, rotateLeft = true)
    private var leftPlayerOpenCardsMiddle = InitialCardView(posX = 135, posY = 390, rotateLeft = true)
    private var leftPlayerOpenCardsRight = InitialCardView(posX = 135, posY = 540, rotateLeft = true)

    override fun refreshAfterStartGame() {
        val game = rootService.currentGame

        checkNotNull(game) { "No started game found." }

        cardMap.clear()

        roundLabel.text = "Rounds left: ${game.rounds}"
        bottomNameLabel.text = game.players[0].name
        val size = game.players.size
        nextPlayer.text.apply {
            if (game.currentPlayer < size - 1) {
                nextPlayer.text =  "Next player: " + game.players[game.currentPlayer + 1].name
            } else {
                nextPlayer.text = "Next player: " + game.players[0].name
            }
        }

        when (size) {
            3 -> {
                topNameLabel.isVisible = false
                leftNameLabel.isVisible = true
                rightNameLabel.isVisible = true

                topPlayerOpenCardsLeft.isVisible = false
                topPlayerOpenCardsMiddle.isVisible = false
                topPlayerOpenCardsRight.isVisible = false

                rightPlayerOpenCardsLeft.isVisible = true
                rightPlayerOpenCardsMiddle.isVisible = true
                rightPlayerOpenCardsRight.isVisible = true

                leftPlayerOpenCardsLeft.isVisible = true
                leftPlayerOpenCardsMiddle.isVisible = true
                leftPlayerOpenCardsRight.isVisible = true

                rightNameLabel.text = game.players[1].name
                leftNameLabel.text = game.players[2].name

                initializeSingleCardView(game.players[1].openCards[0], rightPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[1], rightPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[2], rightPlayerOpenCardsRight, cardImageLoader)

                initializeSingleCardView(game.players[2].openCards[0], leftPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[2].openCards[1], leftPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[2].openCards[2], leftPlayerOpenCardsRight, cardImageLoader)

                moveCardView(cardMap.forward(game.players[2].openCards[0]), leftPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[2].openCards[1]), leftPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[2].openCards[2]), leftPlayerOpenCardsRight)

                moveCardView(cardMap.forward(game.players[1].openCards[0]), rightPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[1].openCards[1]), rightPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[1].openCards[2]), rightPlayerOpenCardsRight)
            }

            4 -> {
                topNameLabel.text = game.players[2].name
                rightNameLabel.text = game.players[1].name
                leftNameLabel.text = game.players[3].name
                rightNameLabel.isVisible = true
                leftNameLabel.isVisible = true
                topNameLabel.isVisible = true

                rightPlayerOpenCardsLeft.isVisible = true
                rightPlayerOpenCardsMiddle.isVisible = true
                rightPlayerOpenCardsRight.isVisible = true

                leftPlayerOpenCardsLeft.isVisible = true
                leftPlayerOpenCardsMiddle.isVisible = true
                leftPlayerOpenCardsRight.isVisible = true


                initializeSingleCardView(game.players[1].openCards[0], rightPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[1], rightPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[2], rightPlayerOpenCardsRight, cardImageLoader)

                initializeSingleCardView(game.players[2].openCards[0], topPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[2].openCards[1], topPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[2].openCards[2], topPlayerOpenCardsRight, cardImageLoader)

                initializeSingleCardView(game.players[3].openCards[0], leftPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[3].openCards[1], leftPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[3].openCards[2], leftPlayerOpenCardsRight, cardImageLoader)

                moveCardView(cardMap.forward(game.players[1].openCards[0]), leftPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[1].openCards[1]), leftPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[1].openCards[2]), leftPlayerOpenCardsRight)

                moveCardView(cardMap.forward(game.players[3].openCards[0]), rightPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[3].openCards[1]), rightPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[3].openCards[2]), rightPlayerOpenCardsRight)

                moveCardView(cardMap.forward(game.players[2].openCards[0]), topPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[2].openCards[1]), topPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[2].openCards[2]), topPlayerOpenCardsRight)
            }

            2 -> {
                rightNameLabel.isVisible = false
                leftNameLabel.isVisible = false

                rightPlayerOpenCardsLeft.isVisible = false
                rightPlayerOpenCardsRight.isVisible = false
                rightPlayerOpenCardsMiddle.isVisible = false

                leftPlayerOpenCardsLeft.isVisible = false
                leftPlayerOpenCardsRight.isVisible = false
                leftPlayerOpenCardsMiddle.isVisible = false

                bottomNameLabel.text = game.players[game.currentPlayer].name
                topNameLabel.text = game.players[game.currentPlayer+1].name

                initializeSingleCardView(game.players[1].openCards[0], topPlayerOpenCardsLeft, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[1], topPlayerOpenCardsMiddle, cardImageLoader)
                initializeSingleCardView(game.players[1].openCards[2], topPlayerOpenCardsRight, cardImageLoader)

                moveCardView(cardMap.forward(game.players[1].openCards[0]), topPlayerOpenCardsLeft)
                moveCardView(cardMap.forward(game.players[1].openCards[1]), topPlayerOpenCardsMiddle)
                moveCardView(cardMap.forward(game.players[1].openCards[2]), topPlayerOpenCardsRight)
            }
        }

        initializeCardView(game.board.drawPile, drawPile, cardImageLoader)

        initializeSingleCardView(game.players[0].openCards[0], currentPlayerOpenCardsLeft, cardImageLoader)
        initializeSingleCardView(game.players[0].openCards[1], currentPlayerOpenCardsMiddle, cardImageLoader)
        initializeSingleCardView(game.players[0].openCards[2], currentPlayerOpenCardsRight, cardImageLoader)

        initializeSingleCardView(game.board.middleCards[0], middleCardLeft, cardImageLoader)
        initializeSingleCardView(game.board.middleCards[1], middleCardMiddle, cardImageLoader)
        initializeSingleCardView(game.board.middleCards[2], middleCardRight, cardImageLoader)

        initializeSingleCardView(game.players[0].hiddenCards[0], currentPlayerHiddenCardLeft, cardImageLoader)
        initializeSingleCardView(game.players[0].hiddenCards[1], currentPlayerHiddenCardRight, cardImageLoader)

        moveCardView(cardMap.forward(game.board.drawPile.first()), drawPile)
        moveCardView(cardMap.forward(game.players[0].openCards[0]), currentPlayerOpenCardsLeft)
        moveCardView(cardMap.forward(game.players[0].openCards[1]), currentPlayerOpenCardsMiddle)
        moveCardView(cardMap.forward(game.players[0].openCards[2]), currentPlayerOpenCardsRight)

        moveCardView(cardMap.forward(game.board.middleCards[0]), middleCardLeft)
        moveCardView(cardMap.forward(game.board.middleCards[1]), middleCardMiddle)
        moveCardView(cardMap.forward(game.board.middleCards[2]), middleCardRight)
    }

    override fun refreshAfterShiftCard(left: Boolean) {
        val game = rootService.currentGame
        checkNotNull(game)

        swapOneButton.isDisabled = false
        passButton.isDisabled = false
        swapAllButton.isDisabled = false

        passButton.font = Font(color = Color.WHITE)
        swapAllButton.font = Font(color = Color.WHITE)
        swapOneButton.font = Font(color = Color.WHITE)

        shiftLeftButton.isDisabled = true
        shiftRightButton.isDisabled = true

        shiftLeftButton.font = Font(color = Color.BLACK)
        shiftRightButton.font = Font(color = Color.BLACK)

        if(left) {
            game.board.discardPileLeft?.let { cardMap.forward(it) }?.let { moveCardView(it, discardPileLeft) }
            moveCardView(cardMap.forward(game.board.middleCards[0]), middleCardLeft)
            moveCardView(cardMap.forward(game.board.middleCards[1]), middleCardMiddle)
            moveCardView(cardMap.forward(game.board.middleCards[2]), middleCardRight)
            moveCardView(cardMap.forward(game.board.drawPile.first()), drawPile, false)
        }
        else {
            game.board.discardPileRight?.let { cardMap.forward(it) }?.let { moveCardView(it, discardPileRight) }
            moveCardView(cardMap.forward(game.board.middleCards[0]), middleCardLeft)
            moveCardView(cardMap.forward(game.board.middleCards[1]), middleCardMiddle)
            moveCardView(cardMap.forward(game.board.middleCards[2]), middleCardRight)
            moveCardView(cardMap.forward(game.board.drawPile.first()), drawPile, false)
        }
    }

    override fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int?, middleIndex: Int?) {
        val game = rootService.currentGame
        checkNotNull(game)

        swapAllButton.isDisabled = true
        passButton.isDisabled = true
        swapOneButton.isDisabled = true

        swapAllButton.font = Font(color = Color.BLACK)
        swapAllButton.font = Font(color = Color.BLACK)
        passButton.font = Font(color = Color.BLACK)

        moveCardView(cardMap.forward(game.board.middleCards[0]), middleCardLeft)
        moveCardView(cardMap.forward(game.board.middleCards[1]), middleCardMiddle)
        moveCardView(cardMap.forward(game.board.middleCards[2]), middleCardRight)

        if(game.currentPlayer != 0){
            moveCardView(cardMap.forward(game.players[game.currentPlayer-1].openCards[0]),
                currentPlayerOpenCardsLeft, false)
            moveCardView(cardMap.forward(game.players[game.currentPlayer-1].openCards[1]),
                currentPlayerOpenCardsMiddle, false)
            moveCardView(cardMap.forward(game.players[game.currentPlayer-1].openCards[2]),
                currentPlayerOpenCardsRight, false)

            if(game.players.size != 2) {
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[0]),
                    rightPlayerOpenCardsLeft,
                    false
                )
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[1]),
                    rightPlayerOpenCardsMiddle,
                    false
                )
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[2]),
                    rightPlayerOpenCardsRight,
                    false
                )
            }
            else {
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[0]),
                    topPlayerOpenCardsLeft,
                    false
                )
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[1]),
                    topPlayerOpenCardsMiddle,
                    false
                )
                moveCardView(
                    cardMap.forward(game.players[game.currentPlayer].openCards[2]),
                    topPlayerOpenCardsRight,
                    false
                )
            }
        }
        else {
            moveCardView(cardMap.forward(game.players[game.players.size-1].openCards[0]),
                currentPlayerOpenCardsLeft, false)
            moveCardView(cardMap.forward(game.players[game.players.size-1].openCards[1]),
                currentPlayerOpenCardsMiddle, false)
            moveCardView(cardMap.forward(game.players[game.players.size-1].openCards[2]),
                currentPlayerOpenCardsRight, false)
        }
    }

    private val roundLabel = Label(
        width = 250, height = 50,
        posX = 0, posY = 10,
        font = Font(size = 30, color = Color.WHITE)
    )

    private val nextPlayer = Label(
        width = 450, height = 50,
        posX = 1500, posY = 10,
        font = Font(size = 30, color = Color.WHITE)
    )

    private val bottomNameLabel = Label(
        width = 200, height = 50,
        posX = 870, posY = 1010,
        font = Font(size = 40, color = Color.WHITE)
    ).apply {
        val game = rootService.currentGame
        if (game != null) {
            text = game.players[game.currentPlayer].name
        }
    }

    private val topNameLabel = Label(
        width = 200, height = 50,
        posX = 870, posY = 20,
        font = Font(size = 40, color = Color.WHITE)
    )

    private val rightNameLabel = Label(
        width = 200, height = 50,
        posX = 1775, posY = 480,
        font = Font(size = 40, color = Color.WHITE)
    ).apply{
        rotate(-90)
    }

    private val leftNameLabel = Label(
        width = 200, height = 50,
        posX = -50, posY = 500,
        font = Font(size = 40, color = Color.WHITE),
    ).apply {
        rotate(90)
    }


    private val showCardsButton = Button(
        width = 100, height = 30,
        posX = 390, posY = 760,
        text = "SHOW",
        font = Font(color = Color.WHITE)
    ).apply {
        visual = ColorVisual(90, 136, 136)
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
                moveCardView(cardMap.forward(
                    game.players[game.currentPlayer].hiddenCards[1]),
                    currentPlayerHiddenCardRight, true
                )
                moveCardView(cardMap.forward(
                    game.players[game.currentPlayer].hiddenCards[0]),
                    currentPlayerHiddenCardLeft, true
                    )
                visual = if(((visual as? ColorVisual)?.color?.red ?: 0) > 100) {
                    ColorVisual(90, 136, 136)
                } else ColorVisual(250, 90, 90)
            }
        }

    private val shiftLeftButton = Button(
        width = 100, height = 30,
        posX = 630, posY = 520,
        text = "LEFT",
        font = Font(color = Color.WHITE)
     ).apply {
        visual = ColorVisual(90, 136, 136)
        if(!this.isDisabled)
            onMouseClicked = {
                val game = rootService.currentGame
                checkNotNull(game)
                    visual = ColorVisual(250, 90, 90)
                    rootService.playerActionService.shift(true)
                    refreshAfterShiftCard(true)
            }
    }

    private val shiftRightButton = Button(
        width = 100, height = 30,
        posX = 1210, posY = 520,
        text = "RIGHT",
        font = Font(color = Color.WHITE)
    ).apply {
        visual = ColorVisual(90, 136, 136)
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.shift(false)
            refreshAfterShiftCard(false)
        }
    }

    private val swapOneButton = Button(
        width = 100, height = 30,
        posX = 1300, posY = 860,
        text = "SWAP ONE"
    ).apply {
        visual = ColorVisual(90, 136, 136)
        this.isDisabled = true
    }

    private val swapAllButton = Button(
        width = 100, height = 30,
        posX = 1300, posY = 910,
        text = "SWAP ALL"
    ).apply {
        visual = ColorVisual(90, 136, 136)
        this.isDisabled = true
        onMouseClicked = {
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.swapAll()
            refreshAfterSwapCard(all = true, pass = false)
        }
    }

    private val passButton = Button(
        width = 100, height = 30,
        posX = 1300, posY = 960,
        text = "PASS"
    ).apply {
        visual = ColorVisual(90, 136, 136)
        this.isDisabled = true
        onMouseClicked = {
            visual = ColorVisual(250, 90, 90)
            rootService.playerActionService.pass()
            refreshAfterSwapCard(all = false, pass = true)
        }
    }

        init {
            // dark green for "Casino table" flair
            background = ColorVisual(108, 168, 59)
            opacity = .5
            addComponents(
                roundLabel,
                bottomNameLabel, rightNameLabel, topNameLabel, leftNameLabel,
                currentPlayerHiddenCardLeft, currentPlayerHiddenCardRight,
                discardPileLeft, discardPileRight, currentPlayerOpenCardsLeft, currentPlayerOpenCardsRight,
                currentPlayerOpenCardsMiddle, rightPlayerOpenCardsLeft, rightPlayerOpenCardsMiddle,
                rightPlayerOpenCardsRight, topPlayerOpenCardsLeft, topPlayerOpenCardsRight,
                topPlayerOpenCardsMiddle, leftPlayerOpenCardsLeft, leftPlayerOpenCardsRight,
                leftPlayerOpenCardsMiddle, drawPile, showCardsButton,middleCardLeft,
                middleCardMiddle, middleCardRight ,shiftLeftButton, shiftRightButton,
                swapOneButton, swapAllButton, passButton, nextPlayer
            )
        }
}
