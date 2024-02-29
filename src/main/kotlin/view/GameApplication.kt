package view

import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Implementation of the BGW [BoardGameApplication] for the card game "Shift Poker".
 */
class GameApplication : BoardGameApplication("Shift Poker"), Refreshable {
    // Central service from which all others are created/accessed
    // also holds the currently active game.
    private val rootService = RootService()

    // Scenes
    // This is where the actual game takes place.
    private val gameScene = GameScene(rootService).apply {

    }

    // This menu scene is shown after application start and if the "new game" button
    // is clicked in the gameFinishedMenuScene.
    private val startMenuScene = StartMenuScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

    // This menu scene is shown after each finished game (i.e. no more cards to draw).
    private val leaderboardScene = LeaderboardScene().apply {
        newGameButton.onMouseClicked = {
            this@GameApplication.showMenuScene(StartMenuScene(rootService))
        }
        quitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        // All scenes and the application itself need to react to changes done in the service layer.
        rootService.addRefreshables(
            this,
            gameScene,
            leaderboardScene,
            startMenuScene
        )

        // This is just done so that the blurred background when showing
        // the new game menu has content and looks nicer.
        rootService.pokerGameService.startGame(rounds = 7)
        this.showGameScene(gameScene)
        this.showMenuScene(startMenuScene, 0)
    }

    override fun refreshAfterStartGame() {
        this.hideMenuScene()
    }

    override fun refreshAfterGameEnd(result: List<Pair<Player, String>>) {
        this.showMenuScene(leaderboardScene)
    }
}
