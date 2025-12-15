class Card {
    private String suit;
    private String rank;
    private int value;
    
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = calculateValue(rank);
    }
    
    private int calculateValue(String rank) {
        switch(rank) {
            case "A": return 11;
            case "J": case "Q": case "K": return 10;
            default: return Integer.parseInt(rank);
        }
    }
    
    public String getSuit() { return suit; }
    public String getRank() { return rank; }
    public int getValue() { return value; }
    
    @Override
    public String toString() {
        return rank + suit;
    }
    
    public Card copy() {
        return new Card(suit, rank);
    }
}