package view

import entity.Card

object GameData {
    var rounds: Int = 2
    var playerNames: MutableList <String> = mutableListOf("Player 1", "Player 2")
    var middleCards: MutableList <Card> = mutableListOf()
}