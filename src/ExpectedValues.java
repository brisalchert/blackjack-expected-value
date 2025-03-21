//----------------------------------------------------------------------------------------------------------------------
//  ExpectedValues.java
//
//  Program for testing the expected value calculations for Blackjack hands against certain dealer up cards.
//----------------------------------------------------------------------------------------------------------------------

public class ExpectedValues {
    public static void main(String[] args) {
        // Initialize a new game
        int up = 10;
        int playerCard1 = 9;
        int playerCard2 = 8;

        Blackjack game = new Blackjack(up, playerCard1, playerCard2, 6);

        System.out.println("Up Card:\t" + up);
        System.out.println("Hand:\t\t" + playerCard1 + ", " + playerCard2);
        System.out.println("Hit:\t\t" + game.expectedValueHit());
        System.out.println("Stand:\t\t" + game.expectedValueStand());
    }
}
