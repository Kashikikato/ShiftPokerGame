package view

import entity.Card
import entity.Player
import entity.ShiftPokerGame
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color


class GameScene(val rootService: RootService)
    : BoardGameScene(1920, 1080), Refreshable {
    val game = rootService.currentGame
    val gameComponents: GameComponents = GameComponents(rootService, this)
    val gameInitializer: GameInitializer = GameInitializer(this)

    private fun initializePileView(cardDeck: List<Card>, deckView: InitialCardView) {
        cardDeck.forEach { card -> initializeCardView(card, deckView) }
    }

    private fun initializeCardView(card: Card, singleCardView: InitialCardView) {
        val cardView = CardView(200, 130, front =  ImageVisual(
            gameInitializer.cardImageLoader.frontImageFor(card.suit, card.value)),
            back = ImageVisual(gameInitializer.cardImageLoader.backImage))
        singleCardView.add(cardView)
        gameInitializer.cardMap.add(card, cardView)
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

    fun moveCardViews(cards: List<Card>, cardViews: List<InitialCardView>,
        flip: Boolean = false, hidden: Boolean = false) {
        cards.forEachIndexed { index, card ->
            if(cardViews.size == 1) moveCardView(gameInitializer.cardMap.forward(card),
                cardViews[0], flip = flip, hidden = hidden)
            else moveCardView(gameInitializer.cardMap.forward(card), cardViews[index], flip = flip, hidden = hidden)
        }
    }

    private fun List<InitialCardView>.hide() {
        for(cardView in this){
            cardView.isVisible = false
        }
    }

    private fun List<InitialCardView>.show() {
        for(cardView in this){
            cardView.isVisible = true
        }
    }

    override fun refreshAfterStartGame() {
        gameInitializer.cardMap.clear()
        gameInitializer.discardPileLeft.clear()
        gameInitializer.discardPileRight.clear()
        refreshAfterStartRound()
    }
    override fun refreshAfterStartRound(){
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
        initializeBoard(game, currentPlayer)
    }

    private fun initializeBoard(game: ShiftPokerGame, currentPlayer: Player) {

        initializePileView(game.board.drawPile, gameInitializer.drawPile)

        initializePlayerUI(currentPlayer, gameInitializer.currentPlayerOpenCardsLeft,
            gameInitializer.currentPlayerOpenCardsMiddle, gameInitializer.currentPlayerOpenCardsRight)
        initializePlayerUI(currentPlayer, gameInitializer.currentPlayerHiddenCardLeft,
            gameInitializer.currentPlayerHiddenCardRight, null)

        game.board.middleCards.forEachIndexed { index, card ->
            initializeCardView(card, gameInitializer.middleCardsViews[index])
        }

        moveCardView(gameInitializer.cardMap.forward(game.board.drawPile.first()), gameInitializer.drawPile)
        moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)
    }



    private fun setupPlayerUI(player: Player, nameLabel: Label, leftCardView: InitialCardView,
        middleCardView: InitialCardView, rightCardView: InitialCardView) {
            nameLabel.text = player.name
            nameLabel.isVisible = true
            val cardViews = listOf(leftCardView, middleCardView, rightCardView)
            cardViews.show()
            initializeCardView(player.openCards[0], leftCardView)
            initializeCardView(player.openCards[1], middleCardView)
            initializeCardView(player.openCards[2], rightCardView)
            moveCardViews(player.openCards, listOf(leftCardView, middleCardView, rightCardView))
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
                gameInitializer.currentPlayerHiddenCards, hidden = true
            )
        }
         else {
            initializeCardView(player.openCards[0], leftCardView)
             initializeCardView(player.openCards[1], middleCardView)
               initializeCardView(player.openCards[2], rightCardView)
            moveCardViews(player.openCards, listOf(gameInitializer.currentPlayerOpenCardsLeft,
                gameInitializer.currentPlayerOpenCardsMiddle, gameInitializer.currentPlayerOpenCardsRight))
           }
    }

    override fun refreshAfterShiftCard(left: Boolean) {
        val game = rootService.currentGame
        checkNotNull(game)

        gameComponents.refreshComponentsAfterShiftCard()

        val discardPile = if (left) game.board.discardPileLeft else game.board.discardPileRight
        discardPile?.let { moveCardView(gameInitializer.cardMap.forward(it),
            if (left) gameInitializer.discardPileLeft else gameInitializer.discardPileRight) }

        // Restliche Aktualisierungen der Kartenansichten
        moveCardViews(game.board.middleCards, gameInitializer.middleCardsViews)
        moveCardView(gameInitializer.cardMap.forward(game.board.drawPile.first()), gameInitializer.drawPile)
    }

    override fun refreshAfterSwapCard(all: Boolean, pass: Boolean, handIndex: Int?, middleIndex: Int?) {
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
        background = ColorVisual(108, 168, 59) // Dark green for "Casino table" flair
        opacity = .5
        addComponents(
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
            gameInitializer.drawPile, gameComponents.showCardsButton, gameInitializer.middleCardLeft,
            gameInitializer.middleCardMiddle, gameInitializer.middleCardRight, gameComponents.shiftLeftButton,
            gameComponents.shiftRightButton,gameComponents.swapOneButton, gameComponents.swapAllButton,
            gameComponents.passButton, gameComponents.nextPlayerLabel, gameComponents.startTurnButton,
            gameComponents.selectedButton, gameComponents.selectACardLabel
        )
    }
}