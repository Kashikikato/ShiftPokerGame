package view

import entity.Player
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * The [LeaderboardScene] class represents the leaderboard scene of the Shift Poker game.
 * This scene displays the final results of the game, including player rankings and the winner.
 *
 * @constructor Creates a new instance of [LeaderboardScene].
 */
class LeaderboardScene : MenuScene(
    600, 800, ColorVisual.LIGHT_GRAY
), Refreshable {

    // Labels for displaying player information
    private val p1Label = Label(
        width = 360, height = 35,
        posX = 160, posY = 250,
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    )

    private val p2Label = Label(
        width = 360, height = 35,
        posX = 160, posY = 300,
        text = "PLAYER 2: ",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    )

    private val p3Label = Label(
        width = 360, height = 35,
        posX = 160, posY = 350,
        text = "PLAYER 3: " + "ROYAL FLUSH",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        this.isVisible = false
    }

    private val p4Label = Label(
        width = 360, height = 35,
        posX = 160, posY = 400,
        text = "PLAYER 4: ",
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT
    ).apply { this.isVisible = false }

    // Label for the headline
    private val headlineLabel = Label(
        width = 550, height = 50, posX = 30, posY = 50,
        text = "GAME OVER!",
        font = Font(size = 30, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    // Button for quitting the game
    val quitButton = Button(
        width = 140, height = 35,
        posX = 150, posY = 625,
        text = "QUIT",
        font = Font(size = 18, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(150, 50, 50)
    }

    // Button for starting a new game
    val newGameButton = Button(
        width = 140, height = 35,
        posX = 310, posY = 625,
        text = "NEW GAME"
    ).apply {
        font = Font(size = 18, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
        visual = ColorVisual(50, 120, 50)
    }

    // Label for displaying the winner
    private val winnerLabel = Label(
        width = 350, height = 100,
        posX = 145, posY = 125,
        font = Font(size = 20, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER
    ).apply {
        isWrapText = true
    }

    private fun sortResults(result: List<Pair<Player, String>>): List<Pair<Player, String>>  {
        return result.sortedByDescending {
                (_, handStrength) ->
            when (handStrength) {
                "Royal Flush" -> 10
                "Straight Flush" -> 9
                "Four of a Kind" -> 8
                "Full House" -> 7
                "Flush" -> 6
                "Straight" -> 5
                "Three of a Kind" -> 4
                "Two Pair" -> 3
                "Pair" -> 2
                "High Card" -> 1
                else -> 0
            }
        }
    }

    override fun refreshAfterGameEnd(result: List<Pair<Player, String>>) {
        // sort result by hand strength
        val playerHandStrengthPairs = sortResults(result)

        //assign name labels by hand strength rankings
        playerHandStrengthPairs.forEachIndexed { index, (player, handStrength) ->
            val label = when (index) {
                0 -> p1Label
                1 -> p2Label
                2 -> p3Label
                3 -> p4Label
                else -> null
            }
            label?.apply {
                text = "${index+1}. ${player.name}: $handStrength"
                isVisible = true
                if(index+1 == 1){
                    winnerLabel.text =(
                            "${player.name} wins with a $handStrength!"
                            )
                }
            }
        }

        // group strengths to display tie
        val sameRankPlayers = playerHandStrengthPairs.groupBy { it.second }.filter { it.value.size > 1 }
        if (sameRankPlayers.isNotEmpty()) {
            val sharedRank = sameRankPlayers.entries.first().key
            val sharedRankPlayers = sameRankPlayers.entries.first().value.map { it.first.name }
            winnerLabel.apply {
                val sharedRankPlayersText = sharedRankPlayers.dropLast(1).joinToString(separator = ", ") +
                        if (sharedRankPlayers.size > 1) " & ${sharedRankPlayers.last()}" else sharedRankPlayers.last()
                text = "It's a tie between $sharedRankPlayersText with a $sharedRank!"

            }
        }
    }

    /**
     * Initializes the leaderboard scene by adding components and setting up event handlers.
     */
    init {
        background = ColorVisual(50, 70, 50) // Dark green for "Casino table" flair
        opacity = .9
        addComponents(
            headlineLabel, newGameButton, quitButton,
            p1Label, p2Label, p3Label, p4Label, winnerLabel
        )
    }
}
