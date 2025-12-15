import java.util.*;

class RoundReplay {
    private List<Card> initialHand;
    private String bonusSuit;
    private List<Integer> droppedPositions;
    private List<Card> finalHand;
    private int score;
    private String playerName;
    private boolean bonusEarned;
    private String highestSuit;
    
    public RoundReplay(String playerName) {
        this.playerName = playerName;
        this.initialHand = new ArrayList<>();
        this.droppedPositions = new ArrayList<>();
        this.finalHand = new ArrayList<>();
    }
    
    public void setInitialHand(List<Card> hand) {
        initialHand.clear();
        for (Card card : hand) {
            initialHand.add(card.copy());
        }
    }
    
    public void setBonusSuit(String suit) {
        this.bonusSuit = suit;
    }
    
    public void setDroppedPositions(List<Integer> positions) {
        this.droppedPositions = new ArrayList<>(positions);
    }
    
    public void setFinalHand(List<Card> hand) {
        finalHand.clear();
        for (Card card : hand) {
            finalHand.add(card.copy());
        }
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void setBonusEarned(boolean earned) {
        this.bonusEarned = earned;
    }
    
    public void setHighestSuit(String suit) {
        this.highestSuit = suit;
    }
    
    public void display(int roundNumber) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║  " + playerName + " - Round " + roundNumber + " Replay");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        System.out.print("\n[Initial Hand]  ");
        for (Card card : initialHand) {
            System.out.print(card + " ");
        }
        System.out.println();
        
        System.out.println("\n[Bonus Suit]    " + bonusSuit);
        
        System.out.print("\n[Cards Dropped] ");
        if (droppedPositions.isEmpty()) {
            System.out.println("None");
        } else {
            System.out.print("Positions: ");
            for (int pos : droppedPositions) {
                System.out.print(pos + " ");
            }
            System.out.println();
        }
        
        System.out.print("\n[Final Hand]    ");
        for (Card card : finalHand) {
            System.out.print(card + " ");
        }
        System.out.println();
        
        System.out.println("\n[Result]        Highest suit: " + highestSuit);
        if (bonusEarned) {
            System.out.println("                BONUS EARNED! (+5 points)");
        }
        System.out.println("                Round Score: " + score + " points");
        System.out.println();
    }
}