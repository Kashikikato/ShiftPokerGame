package view
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Implementation of the BGW [BoardGameApplication] for the card game "Shift Poker"
 */

class GameApplication: BoardGameApplication("Shift Poker"), Refreshable {
    // Central service from which all others are created/accessed
    // also holds the currently active game
    private val rootService = RootService()
    // Scenes
    // This is where the actual game takes place
    private val gameScene = GameScene(rootService).apply {

    }
    // This menu scene is shown after application start and if the "new game" button
    // is clicked in the gameFinishedMenuScene
    private val startMenuScene = StartMenuScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

      // This menu scene is shown after each finished game (i.e. no more cards to draw)
    private val leaderboardScene = LeaderboardScene().apply {
        newGameButton.onMouseClicked = {
            this@GameApplication.showMenuScene(StartMenuScene(rootService))
        }
        quitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        // all scenes and the application itself need to react to changes done in the service layer
        rootService.addRefreshables(
            this,
            gameScene,
//            gameFinishedMenuScene,
            startMenuScene
        )

        // This is just done so that the blurred background when showing
        // the new game menu has content and looks nicer
        rootService.pokerGameService.startGame(rounds = 7)
        this.showGameScene(gameScene)
        this.showMenuScene(startMenuScene, 0)
    }

    override fun refreshAfterStartGame() {
        this.hideMenuScene()
    }

    override fun refreshAfterGameEnd() {
        this.showMenuScene(leaderboardScene)
        rootService.pokerGameService.calcResult()
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEachIndexed { index, player ->
            val label = when (index) {
                0 -> leaderboardScene.p1Label
                1 -> leaderboardScene.p2Label
                2 -> leaderboardScene.p3Label
                3 -> leaderboardScene.p4Label
                else -> null // Hier kannst du weitere Fälle hinzufügen, falls du mehr als 4 Spieler hast
            }
            label?.apply {
                text = "${player.name}: ${rootService.pokerGameService.evaluateHand(
                    player.openCards + player.hiddenCards)}"
                this.isVisible = true
            }
        }
    }
}
