//----------------------------------------------------------------------------------------------------------------------
//  BlackjackInfinite.java
//
//  Program for calculating the expected values of certain Blackjack hands, assuming an infinite deck.
//----------------------------------------------------------------------------------------------------------------------

import java.util.ArrayList;

public class BlackjackInfinite {
    private final int up;
    private final ArrayList<Integer> hand;

    // Constructor: Creates a new Blackjack game with a particular player hand and up card.
    public BlackjackInfinite(int up, int card1, int card2) {
        this.up = up;
        this.hand = new ArrayList<>(2);
        hand.add(card1);
        hand.add(card2);
    }

    /**
     * Gets the number of points associated with the hand.
     * @param hand the Blackjack hand
     * @return the point value of the hand
     */
    private int points(ArrayList<Integer> hand) {
        int sum = 0;

        for (Integer integer : hand) {
            sum += integer;
        }

        return sum;
    }

    /**
     * Calculates the probability of the dealer getting a hand with a certain number of points.
     * @param points the exact number of points the dealer obtains
     * @param dealerHandValue the current value of the dealer's hand (initially just the up card)
     * @return the probability of reaching the exact number of points
     */
    public double probabilityDealer(int points, int dealerHandValue) {
        double probTotal = 0.0;

        for (int card = 1; card <= 10; card++) {
            // Stop considering cards when the hand's value exceeds points
            if (dealerHandValue + card > points) break;

            if (dealerHandValue + card == points) {
                // Add the probability of drawing the exact card needed
                probTotal += (probabilityDraw(card));
            } else if (dealerHandValue + card < 17) {
                dealerHandValue += card;

                // Add the probability of getting the point value on further draws => P(a) * P(b|a)
                probTotal += probabilityDraw(card) * probabilityDealer(points, dealerHandValue);

                dealerHandValue -= card;
            }
        }

        return probTotal;
    }

    /**
     * Finds the probability of drawing a given card from the deck, assuming
     * an infinite deck of cards.
     * @param card the card to be drawn
     * @return the probability of drawing the card
     */
    private double probabilityDraw(int card) {
        if (card != 10) {
            return (1.0 / 13);
        }

        return (4.0 / 13);
    }

    /**
     * Calculates the expected value for the player's hand, assuming that they
     * choose to stand.
     * @return the expected value for the hand versus the dealer's up card
     */
    public double expectedValueStand() {
        // Player bust has a score of -1
        if (points(hand) > 21) return -1.0;

        double score = 0.0;

        // Add the score for dealer bust
        for (int dealerScore = 22; dealerScore <= 26; dealerScore++) {
            score += probabilityDealer(dealerScore, up);
        }

        // Add the score for player stand beats dealer stand
        for (int points = 17; points < points(hand); points++) {
            score += probabilityDealer(points, up);
        }

        // Subtract the score for dealer stand beats player stand
        for (int points = points(hand) + 1; points <= 21; points++) {
            score -= probabilityDealer(points, up);
        }

        return score;
    }

    /**
     * Calculates the expected value for the player's hand, assuming that they
     * hit and will hit again on a 16 or less and stand on a 17 or higher.
     * @return the expected value for the hand versus the dealer's up card
     */
    public double expectedValueHit() {
        double score = 0.0;

        for (int card = 1; card <= 10; card++) {
            if (points(hand) + card < 17) {
                // Add card and hit again
                score += expectedValueHit() * probabilityDraw(card);
            } else {
                // Add card and stand afterward
                score += expectedValueStand() * probabilityDraw(card);
            }
        }

        return score;
    }
}
