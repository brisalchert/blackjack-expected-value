//----------------------------------------------------------------------------------------------------------------------
//  Blackjack.java
//
//  Program for calculating the expected values of certain Blackjack hands.
//----------------------------------------------------------------------------------------------------------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

public class Blackjack {
    private final int up;
    private final ArrayList<Integer> hand;
    private final HashMap<Integer, Integer> deck;
    private final int numDecks;

    // Constructor: Creates a new Blackjack game with a particular player hand and up card.
    public Blackjack(int up, int card1, int card2, int numDecks) {
        this.up = up;
        this.hand = new ArrayList<>(2);
        hand.add(card1);
        hand.add(card2);

        // Set up the deck
        this.numDecks = numDecks;
        deck = new HashMap<>();
        initializeDeck();
        removeFromDeck(up);
        removeFromDeck(card1);
        removeFromDeck(card2);
    }

    /**
     * Adds all cards to the deck, counting Aces as "1" and all 10-valued cards as "10".
     */
    private void initializeDeck() {
        if (this.deck != null) {
            deck.clear();

            for (int i = 1; i <= 9; i++) {
                deck.put(i, 4 * numDecks);
            }

            deck.put(10, 16 * numDecks);
        }
    }

    /**
     * Removes a card with the specified value from the deck.
     * @param value the value of the card to remove
     * @throws InputMismatchException if the card does not exist in the deck (helps with debugging)
     */
    private void removeFromDeck(int value) throws InputMismatchException {
        if (deck.get(value) > 0) {
            deck.put(value, deck.get(value) - 1);
        } else {
            throw new InputMismatchException("Cannot remove " + value + " from the deck");
        }
    }

    /**
     * Adds a card with the specified value to the deck.
     * @param value the value of the card to add
     * @throws InputMismatchException if adding the card makes the deck invalid (helps with debugging)
     */
    private void addToDeck(int value) throws InputMismatchException {
        if ((deck.get(value) < 4 * numDecks) || (value == 10 && (deck.get(value) < 16 * numDecks))) {
            deck.put(value, deck.get(value) + 1);
        } else {
            throw new InputMismatchException("Cannot add " + value + " to the deck");
        }
    }

    /**
     * Gets the current number of cards in the deck.
     * @return the number of cards in the deck
     */
    private int getDeckSize() {
        int size = 0;

        for (Integer key : deck.keySet()) {
            size += deck.get(key);
        }

        return size;
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
     * Calculates the probability of the dealer getting a hand with a certain number of points,
     * accounting for the fact that the dealer must not have a natural.
     * @param points the exact number of points the dealer obtains
     * @param dealerHandValue the current value of the dealer's hand (initially just the up card)
     * @return the probability of reaching the exact number of points
     */
    public double probabilityDealer(int points, int dealerHandValue) {
        double probTotal = 0.0;

        for (int card = 1; card <= 10; card++) {
            if (card == 1 && up == 10 && up == dealerHandValue) continue;
            if (card == 10 && up == 1 && up == dealerHandValue) continue;

            // Stop considering cards when the hand's value exceeds points
            if (dealerHandValue + card > points) break;

            // Skip if card does not exist in deck
            if (deck.get(card) == 0) continue;

            if (dealerHandValue + card == points) {
                // Add the probability of drawing the exact card needed
                probTotal += (probabilityDraw(card));
            } else if (dealerHandValue + card < 17) {
                // Add the probability of getting the point value on further draws => P(a) * P(b|a)
                double probDraw = (probabilityDraw(card));

                removeFromDeck(card);
                dealerHandValue += card;

                probTotal += probDraw * probabilityDealer(points, dealerHandValue);

                dealerHandValue -= card;
                addToDeck(card);
            }
        }

        return probTotal;
    }

    /**
     * Finds the probability of drawing a given card from the deck, accounting
     * for the fact that the dealer must not have a natural.
     * @param card the card to be drawn
     * @return the probability of drawing the card
     */
    private double probabilityDraw(int card) {
        double countCard = (double) deck.get(card);
        double countDeck = getDeckSize();

        // No need to account for natural
        if (up >= 2 && up <= 9) {
            return countCard / countDeck;
        }

        // Account for dealer not having a natural
        if (((up == 1) || (up == 10)) && (card == 11 - up)) {
            return countCard / (countDeck - 1);
        }

        double countUpComplement = deck.get(11 - up);

        return (countCard / (countDeck - 1))
                * ((countDeck - countUpComplement - 1) / (countDeck - countUpComplement));
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
            // Skip cards that cannot be drawn
            if (deck.get(card) == 0) continue;

            removeFromDeck(card);
            hand.addLast(card);

            if (points(hand) + card < 17) {
                // Add card and hit again
                score += expectedValueHit() * probabilityDraw(card);
            } else {
                // Add card and stand afterward
                score += expectedValueStand() * probabilityDraw(card);
            }

            hand.removeLast();
            addToDeck(card);
        }

        return score;
    }
}
