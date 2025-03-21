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
     * @param soft whether the dealer's hand contains a soft Ace
     * @return the probability of reaching the exact number of points
     */
    public double probabilityDealer(int points, int dealerHandValue, boolean soft) {
        double probTotal = 0.0;

        for (int card = 1; card <= 10; card++) {
            int cardAdjusted = card;

            // If a drawn Ace can be soft, update the hand value
            if (!soft && card == 1 && dealerHandValue <= 10) {
                cardAdjusted += 10;
                soft = true;
            }

            // For bust with soft Ace, reduce Ace value to 1 and continue drawing
            if (soft && dealerHandValue + cardAdjusted > 21) {
                probTotal += probabilityDraw(cardAdjusted) * probabilityDealer(points, dealerHandValue - 10 + cardAdjusted, false);

                break;
            }

            if (dealerHandValue + cardAdjusted == points) {
                // Add the probability of drawing the exact card needed
                probTotal += (probabilityDraw(cardAdjusted));
            } else if (dealerHandValue + cardAdjusted < 17) {
                // Add the probability of getting the point value on further draws => P(a) * P(b|a)
                probTotal += probabilityDraw(cardAdjusted) * probabilityDealer(points, dealerHandValue + cardAdjusted, soft);
            } else if (cardAdjusted != card) {
                // Soft Ace not useful; reset boolean
                soft = false;
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
        int initDealerHandValue = up;
        boolean soft = up == 1;

        // Adjust starting dealer hand value if up card is a soft ace
        if (soft) {
            initDealerHandValue += 10;
        }

        // Add the score for dealer bust
        for (int dealerScore = 22; dealerScore <= 26; dealerScore++) {
            score += probabilityDealer(dealerScore, initDealerHandValue, soft);
        }

        // Add the score for player stand beats dealer stand
        for (int points = 17; points < points(hand); points++) {
            score += probabilityDealer(points, initDealerHandValue, soft);
        }

        // Subtract the score for dealer stand beats player stand
        for (int points = points(hand) + 1; points <= 21; points++) {
            score -= probabilityDealer(points, initDealerHandValue, soft);
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
            hand.addLast(card);

            if (points(hand) + card < 17) {
                // Hit again
                score += expectedValueHit() * probabilityDraw(card);
            } else {
                // Stand
                score += expectedValueStand() * probabilityDraw(card);
            }

            hand.removeLast();
        }

        return score;
    }
}
