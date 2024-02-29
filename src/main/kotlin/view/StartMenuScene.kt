package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

class StartMenuScene(private val rootService: RootService): MenuScene(
    600, 800, ColorVisual.LIGHT_GRAY), Refreshable {

    private val headlineLabel = Label(
        width = 550, height = 50, posX = 30, posY = 50,
        text = "WELCOME TO SHIFT POKER!",
        font = Font(size = 32, color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    private val roundLabel = Label(
        width = 80, height = 35,
        posX = 160, posY = 125,
        text = "ROUNDS: ",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)

    )
    private var rounds = 2
    private var numPlayers = 2

    private val roundList: ComboBox<Int> = ComboBox(
        width = 50, height = 25,
        posX = 250, posY = 130,
        items = listOf(2, 3, 4, 5, 6, 7)
        ).apply {
            selectedItemProperty.addListener{ _, newValue ->
                if(newValue != null) {
                    onRoundsSelected(newValue)
                    rounds = newValue
                }
        }
    }

    private val numRoundsLabel = Label(
        width = 30, height = 25,
        posX = 250, posY = 130,
        text = rounds.toString(),
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    private fun onRoundsSelected(rounds: Int) {
        numRoundsLabel.text = "$rounds"
    }

    private fun onNumberPlayersSelected(numPlayers: Int) {
        numPlayersLabel.text = "$numPlayers"
        when (numPlayers) {
            3 -> {
                quitButton.posY = 380.0
                startButton.posY = 380.0
                p3Label.isVisible = true
                p3Input.isVisible = true
                p4Input.isVisible = false
                p4Label.isVisible = false
            }
            4 -> {
                quitButton.posY = 435.0
                startButton.posY = 435.0
                p3Label.isVisible = true
                p3Input.isVisible = true
                p4Input.isVisible = true
                p4Label.isVisible = true
            }
            2 -> {
                quitButton.posY = 325.0
                startButton.posY = 325.0
                p3Label.isVisible = false
                p3Input.isVisible = false
                p4Input.isVisible = false
                p4Label.isVisible = false
            }
        }
    }

    private val playersLabel = Label(
        width = 80, height = 35,
        posX = 158, posY = 175,
        text = "PLAYERS:",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    private val numPlayersInput: ComboBox<Int> = ComboBox(
        width = 50, height = 25,
        posX = 250, posY = 180,
        items = listOf(2,3,4),
    ).apply {
        selectedItemProperty.addListener { _, newValue ->
            if (newValue != null) {
                onNumberPlayersSelected(newValue)
                numPlayers = newValue
            }
        }
    }


    private val numPlayersLabel = Label(
        width = 30, height = 25,
        posX = 250, posY = 180,
        text = numPlayers.toString(),
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    private val p1Label = Label(
        width = 100, height = 35,
        posX = 150, posY = 225,
        text = "PLAYER 1:",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )
    private val p1Input: TextField = TextField(
        width = 200, height = 35,
        posX = 250, posY = 225,
        text = "Player 1",
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank()
        }
    }

    private val p2Label = Label(
        width = 100, height = 35,
        posX = 150, posY = 275,
        text = "PLAYER 2:",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    )

    private val p2Input: TextField = TextField(
        width = 200, height = 35,
        posX = 250, posY = 275,
        text = "Player 2"
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = p1Input.text.isBlank() || this.text.isBlank()
        }
    }

    private val p3Label = Label(
        width = 100, height = 35,
        posX = 150, posY = 325,
        text = "PLAYER 3:",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply { this.isVisible = false }

    private val p3Input: TextField = TextField(
        width = 200, height = 35,
        posX = 250, posY = 325,
        text = "Player 3"
    ).apply {
        this.isVisible = false
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank()
        }
    }

    private val p4Label = Label(
        width = 100, height = 35,
        posX = 150, posY = 375,
        text = "PLAYER 4:",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply { this.isVisible = false }

    private val p4Input: TextField = TextField(
        width = 200, height = 35,
        posX = 250, posY = 375,
        text = "Player 4"
    ).apply {
        this.isVisible = false
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank()
        }
    }

    val quitButton = Button(
        width = 140, height = 35,
        posX = 150, posY = 325,
        text = "QUIT",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(150, 50, 50)
    }

    private val startButton = Button(
        width = 140, height = 35,
        posX = 310, posY = 325,
        text = "START",
        font = Font(color = Color.WHITE, family = "Monospace", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        visual = ColorVisual(50, 120, 50)
        onMouseClicked = {
            val p1 = p1Input.text.trim()
            val p2 = p2Input.text.trim()
            val p3 = p3Input.text.trim()
            val p4 = p4Input.text.trim()
            GameData.playerNames = mutableListOf(p1,p2)
            GameData.rounds = rounds
            when(numPlayers) {
                3 -> GameData.playerNames.add(p3)
                4 -> GameData.playerNames = ((GameData.playerNames + mutableListOf(p3,p4)).toMutableList())
            }
            rootService.pokerGameService.startGame(GameData.playerNames,rounds)
            refreshAfterStartGame()
        }
    }

    init {
        background = ColorVisual(50, 70, 50) // Dark green for "Casino table" flair
        opacity = .9
        addComponents(
            headlineLabel,
            roundLabel, roundList, numRoundsLabel,
            numPlayersLabel, numPlayersInput, playersLabel,
            p1Label, p1Input,
            p2Label, p2Input,
            p3Label, p3Input,
            p4Label, p4Input,
            startButton, quitButton,
        )
    }
}