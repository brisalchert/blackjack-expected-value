//----------------------------------------------------------------------------------------------------------------------
//  ExpectedValues.java
//
//  Program for testing the expected value calculations for Blackjack hands against certain dealer up cards.
//----------------------------------------------------------------------------------------------------------------------

public class ExpectedValues {
    public static void main(String[] args) {
        // Initialize new games, with finite decks and infinite decks
        int up = 10;
        int playerCard1 = 9;
        int playerCard2 = 7;
        int numDecks = 6;

        Blackjack game = new Blackjack(up, playerCard1, playerCard2, numDecks);
        BlackjackInfinite gameInfinite = new BlackjackInfinite(up, playerCard1, playerCard2);

        System.out.println("Up Card:\t" + up);
        System.out.println("Hand:\t\t" + playerCard1 + ", " + playerCard2);

        System.out.println();

        System.out.println(numDecks + " DECK GAME:");
        System.out.println("Hit:\t\t" + game.expectedValueHit());
        System.out.println("Stand:\t\t" + game.expectedValueStand());

        System.out.println();

        System.out.println("INFINITE DECK GAME:");
        System.out.println("Hit:\t\t" + gameInfinite.expectedValueHit());
        System.out.println("Stand:\t\t" + gameInfinite.expectedValueStand());
    }
}
