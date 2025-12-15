import java.util.*;

// Player class
class Player {
    private String name;
    private List<Card> hand;
    private int totalScore;
    private List<Integer> roundScores;
    private boolean isComputer;
    private List<RoundReplay> roundReplays;
    
    public Player(String name, boolean isComputer) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.totalScore = 0;
        this.roundScores = new ArrayList<>();
        this.isComputer = isComputer;
        this.roundReplays = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public List<Card> getHand() { return hand; }
    public int getTotalScore() { return totalScore; }
    public List<Integer> getRoundScores() { return roundScores; }
    public boolean isComputer() { return isComputer; }
    public List<RoundReplay> getRoundReplays() { return roundReplays; }
    
    public void addCard(Card card) {
        hand.add(card);
    }
    
    public void clearHand() {
        hand.clear();
    }
    
    public void addRoundScore(int score) {
        roundScores.add(score);
        totalScore += score;
    }
    
    public void addRoundReplay(RoundReplay replay) {
        roundReplays.add(replay);
    }
    
    public void displayHand() {
        System.out.print(name + "'s hand: ");
        for (int i = 0; i < hand.size(); i++) {
            System.out.print((i + 1) + ":" + hand.get(i) + " ");
        }
        System.out.println();
    }
    
    public Map<String, Integer> calculateSuitScores() {
        Map<String, Integer> suitScores = new HashMap<>();
        suitScores.put("Spades", 0);
        suitScores.put("Hearts", 0);
        suitScores.put("Diamonds", 0);
        suitScores.put("Clubs", 0);
        
        for (Card card : hand) {
            String suit = card.getSuit();
            suitScores.put(suit, suitScores.get(suit) + card.getValue());
        }
        
        return suitScores;
    }
    
    public String getHighestScoringSuit() {
        Map<String, Integer> suitScores = calculateSuitScores();
        String highestSuit = "Spades";
        int highestScore = 0;
        
        for (Map.Entry<String, Integer> entry : suitScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                highestSuit = entry.getKey();
            }
        }
        
        return highestSuit;
    }
    
    public int getHighestSuitScore() {
        Map<String, Integer> suitScores = calculateSuitScores();
        return Collections.max(suitScores.values());
    }
    
    // Computer AI: Choose bonus suit
    public String computerChooseBonusSuit() {
        Map<String, Integer> suitScores = calculateSuitScores();
        
        // Strategy: Choose the suit with highest current score
        // This maximizes chance of getting the +5 bonus
        String bestSuit = "Spades";
        int bestScore = 0;
        
        for (Map.Entry<String, Integer> entry : suitScores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestSuit = entry.getKey();
            }
        }
        
        return bestSuit;
    }
    
    // Computer AI: Choose cards to drop
    public List<Integer> computerChooseCardsToSwap(String bonusSuit) {
        List<Integer> cardsToSwap = new ArrayList<>();
        Map<String, Integer> suitScores = calculateSuitScores();
        
        // Strategy: Keep cards from bonus suit and highest-value cards
        // Drop low-value cards from other suits
        
        // First, identify which suit has the most potential
        String targetSuit = bonusSuit;
        
        // Count cards in each suit
        Map<String, List<Integer>> suitPositions = new HashMap<>();
        for (int i = 0; i < hand.size(); i++) {
            String suit = hand.get(i).getSuit();
            suitPositions.putIfAbsent(suit, new ArrayList<>());
            suitPositions.get(suit).add(i);
        }
        
        // Drop cards that are:
        // 1. Not from the bonus suit
        // 2. Low value (< 8)
        // 3. From suits with only 1 card
        
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            
            // Never drop high-value cards from bonus suit
            if (card.getSuit().equals(bonusSuit) && card.getValue() >= 8) {
                continue;
            }
            
            // Drop low-value cards not from bonus suit
            if (!card.getSuit().equals(bonusSuit) && card.getValue() <= 6) {
                cardsToSwap.add(i + 1); // +1 for 1-indexed position
            }
            
            // Limit to max 4 cards
            if (cardsToSwap.size() >= 4) {
                break;
            }
        }
        
        return cardsToSwap;
    }
}