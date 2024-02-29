package service

import entity.Card
import entity.CardValue

/**
 * Utility class for evaluating the value of a hand in poker.
 * It provides methods to detect various combinations of cards based on the rules of poker.
 */

class HandValue {
    // the following methods detect relevant combinations of cards based on the rules of poker

    /**
     * Determines whether the given hand contains a "Royal Flush". Therefore, checks whether the player
     * has a "Straight Flush" and an ace, represented by the ordinal number 12. In combination, this represents
     * a "Royal Flush".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Royal Flush", else return false.
     */
    fun hasRoyalFlush(hand: List<Card>): Boolean {
        return hasStraightFlush(hand) && hand.any { it.value == CardValue.ACE }
    }
    /**
     * Determines whether the given hand contains a "Straight Flush". Therefore, checks whether the player
     * has a "Straight" and a "Flush" by calling the respective methods. In combination, this represents
     * a "Straight Flush".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Straight Flush", else return false.
     */
    fun hasStraightFlush(hand: List<Card>): Boolean {
        return hasStraight(hand) && hasFlush(hand)
    }

    /**
     * Determines whether the given hand contains a "Straight Flush". Therefore, checks whether the player
     * has "3 of a Kind" and "2 Pairs", whilst not having a "Full House" by calling the respective methods.
     * In combination, this represents a "4 of a Kind", since this combination is only possible if the "2 Pairs"
     * consist of the same values.
     *
     * Note that "2 Pairs" generally means that the two pairs are not equal by value.
     * The method hasTwoPair() ignores this for the purpose of detecting a "4 of a Kind".
     * Since evaluateHand checks for "4 of a Kind" first, this should not cause any problems
     * such as returning a "2 Pairs" hand when there are "4 of a Kind".
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has "4 of a Kind", else return false.
     */
    fun hasFourOfAKind(hand: List<Card>): Boolean {
        return hasThreeOfAKind(hand) && hasTwoPair(hand) && !hasFullHouse(hand)
    }

    /**
     * Determines whether the given hand contains a "Flush". It simply uses a nested loop to check if all
     * card suits are the same.
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Flush", else return false.
     */
    fun hasFlush(hand: List<Card>): Boolean {

        val suits = hand.groupBy { it.suit }
        val isFlush = suits.size == 1

        return isFlush
    }

    /**
     * Determines whether the given hand contains a "Full House". This is represented by the combination
     * of "3 of a Kind" and "1 Pair". Therefore, the function calls the two respective methods and return true,
     * if both are true.
     *
     * @param hand the hand of the player.
     *
     * @return true if the player has a "Full House", else return false.
     */
    fun hasFullHouse(hand: List<Card>): Boolean {
        return hasThreeOfAKind(hand) && hasPair(hand)
    }

    /**
     * Determines whether the given hand contains a "Straight". Therefore, the method checks if the hand is sorted
     * in consecutive order, which represents a "Straight".
     *
     * @param hand the hand of the player.
     *
     * @return true if the hand contains a "Straight", else return false.
     */
    fun hasStraight(hand: List<Card>): Boolean {
        //sort the cards based on their value
        val sortedHand = hand.sortedBy{it.value.ordinal}

        for(i in 0 until sortedHand.size-1) {
            if(sortedHand[i+1].value.ordinal - sortedHand[i].value.ordinal != 1) {
                return false
            }
        }
        return true
    }

    /**
     * Determines whether the given hand contains "3 of a Kind". Therefore, the method uses nested for-loops and
     * a [count] variable to return the proper Boolean value.
     *
     * @param hand the hand of the player.
     *
     * @return true, if the hand contains "3 of a Kind", else return false.
     */
    fun hasThreeOfAKind(hand: List<Card>): Boolean {
        var count = 0
        for(i in 0..2) {
            for(card in hand) {
                if(card.value.ordinal == hand[i].value.ordinal) {
                    count++
                }
            }
            if(count >= 3) {
                return true
            }
            else {
                count = 0
            }
        }
        return false
    }

    /**
     * Determines whether the given hand contains "2 Pairs". Therefore, it uses nested for-loops
     * and a mutableList, to which the cards that constitute a pair are added. Note that this can also
     * potentially represent "4 of a Kind", since it does not require the "2 Pairs" to be of different values.
     * In that case, it evaluateHand() will recognize it as "4 of a Kind".
     *
     * @param hand the hand of the player.
     *
     * @return true, if the hand contains "2 Pairs", else return false.
     */

    fun hasTwoPair(hand: List<Card>): Boolean {
        var tempCount = 0
        var pairCount = 0
        for (card in hand) {
            for (otherCard in hand) {
                if (card.value.ordinal == otherCard.value.ordinal && card.suit.ordinal != otherCard.suit.ordinal) {
                    tempCount += 1
                }
            }
            if (tempCount == 1 || tempCount == 3) {
                pairCount += 1
            }
            tempCount = 0
        }
        return pairCount > 2
    }

    /**
     * Determines whether the given hand consists of "Pair". Therefore, it simply compares the cards in the hand
     * with one another and returns true, if two cards are equal in value.
     *
     * @param hand the hand of the player.
     *
     * @return true if the hand consists of "Pair", return false otherwise.
     */
    fun hasPair(hand: List<Card>): Boolean {
        var pairCount = 0
        for(card in hand) {
            for(otherCard in hand) {
                if(card.value.ordinal == otherCard.value.ordinal && card.suit.ordinal != otherCard.suit.ordinal) {
                    pairCount += 1
                }
            }
            if(pairCount == 1) {
                return true
            }
            else {
                pairCount = 0
            }
        }
        return false
    }
}