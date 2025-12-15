import java.util.*;
import java.io.*;


public class CardSuitGame {
    private Scanner scanner;
    private List<Player> players;
    private Deck deck;
    private int numRounds;
    private HighScoreManager highScoreManager;
    
    public CardSuitGame() {
        scanner = new Scanner(System.in);
        players = new ArrayList<>();
        highScoreManager = new HighScoreManager();
    }
    
    public void start() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║          CARD SUIT GAME WELCOME!       ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        setupGame();
        playGame();
        displayResults();
        offerRoundReplay();
        saveHighScores();
        
        System.out.print("\nWould you like to see the high scores? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y")) {
            highScoreManager.displayHighScores();
        }
        
        System.out.println("\nThanks for playing!");
    }
    
    private void setupGame() {
        // Get number of players
        int numPlayers = 0;
        while (numPlayers < 1 || numPlayers > 2) {
            System.out.print("Enter number of players (1 or 2): ");
            try {
                numPlayers = Integer.parseInt(scanner.nextLine().trim());
                if (numPlayers < 1 || numPlayers > 2) {
                    System.out.println("Please enter 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        // Get player names and types
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                name = "Player " + i;
            }
            
            boolean isComputer = false;
            if (numPlayers == 2 && i == 2) {
                System.out.print("Should " + name + " be computer-controlled? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                isComputer = response.equals("y");
                if (isComputer) {
                    name = name + " (CPU)";
                }
            }
            
            players.add(new Player(name, isComputer));
        }
        
        // Get number of rounds
        numRounds = 0;
        while (numRounds < 1 || numRounds > 3) {
            System.out.print("Enter number of rounds (1-3): ");
            try {
                numRounds = Integer.parseInt(scanner.nextLine().trim());
                if (numRounds < 1 || numRounds > 3) {
                    System.out.println("Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        System.out.println();
    }
    
    private void playGame() {
        for (int round = 1; round <= numRounds; round++) {
            System.out.println("╔════════════════════════════════════════════════════╗");
            System.out.println("║         ROUND " + round + " of " + numRounds + "                                  ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");
            
            deck = new Deck();
            playRound();
            
            if (round < numRounds) {
                System.out.println("\n--- Round " + round + " Complete ---");
                displayRoundScores();
                System.out.println("\nPress Enter to continue to next round...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }
    
    private void playRound() {
        for (Player player : players) {
            player.clearHand();
            RoundReplay replay = new RoundReplay(player.getName());
            
            // Step 1: Deal 5 cards
            System.out.println("\n--- " + player.getName() + "'s Turn ---");
            for (int i = 0; i < 5; i++) {
                player.addCard(deck.drawCard());
            }
            player.displayHand();
            replay.setInitialHand(player.getHand());
            
            // Step 2: Calculate initial suit scores
            Map<String, Integer> suitScores = player.calculateSuitScores();
            System.out.println("\nSuit scores:");
            System.out.println("  Spades: " + suitScores.get("Spades"));
            System.out.println("  Hearts: " + suitScores.get("Hearts"));
            System.out.println("  Diamonds: " + suitScores.get("Diamonds"));
            System.out.println("  Clubs: " + suitScores.get("Clubs"));
            
            String highestSuit = player.getHighestScoringSuit();
            int highestScore = player.getHighestSuitScore();
            System.out.println("Highest scoring suit: " + highestSuit + " (" + highestScore + " points)");
            
            // Step 3: Choose bonus suit
            String bonusSuit;
            if (player.isComputer()) {
                bonusSuit = player.computerChooseBonusSuit();
                System.out.println("\n[CPU choosing bonus suit...]");
                System.out.println("CPU chose: " + bonusSuit);
            } else {
                bonusSuit = chooseBonusSuit(player);
            }
            System.out.println("Bonus suit chosen: " + bonusSuit);
            replay.setBonusSuit(bonusSuit);
            
            // Step 4: Choose cards to swap
            List<Integer> droppedPositions;
            if (player.isComputer()) {
                droppedPositions = swapCardsComputer(player, bonusSuit);
            } else {
                droppedPositions = swapCards(player);
            }
            replay.setDroppedPositions(droppedPositions);
            
            // Step 5: Recalculate suit scores
            player.displayHand();
            replay.setFinalHand(player.getHand());
            
            suitScores = player.calculateSuitScores();
            System.out.println("\nFinal suit scores:");
            System.out.println("  Spades: " + suitScores.get("Spades"));
            System.out.println("  Hearts: " + suitScores.get("Hearts"));
            System.out.println("  Diamonds: " + suitScores.get("Diamonds"));
            System.out.println("  Clubs: " + suitScores.get("Clubs"));
            
            // Step 6 & 7: Calculate final score with bonus
            highestSuit = player.getHighestScoringSuit();
            highestScore = player.getHighestSuitScore();
            System.out.println("Highest scoring suit: " + highestSuit + " (" + highestScore + " points)");
            replay.setHighestSuit(highestSuit);
            
            int roundScore = highestScore;
            boolean bonusEarned = false;
            if (highestSuit.equals(bonusSuit)) {
                roundScore += 5;
                bonusEarned = true;
                System.out.println("BONUS! +5 points for matching bonus suit!");
            }
            replay.setBonusEarned(bonusEarned);
            replay.setScore(roundScore);
            
            player.addRoundScore(roundScore);
            player.addRoundReplay(replay);
            System.out.println("Round score: " + roundScore);
        }
    }
    
    private String chooseBonusSuit(Player player) {
        System.out.print("\nChoose a bonus suit (1:Spades 2:Hearts 3:Diamonds 4:Clubs): ");
        int choice = 0;
        while (choice < 1 || choice > 4) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > 4) {
                    System.out.print("Invalid choice. Enter 1-4: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter 1-4: ");
            }
        }
        
        String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
        return suits[choice - 1];
    }
    
    private List<Integer> swapCards(Player player) {
        System.out.print("\nEnter positions to drop (1-5, separated by spaces, or 0 to keep all): ");
        String input = scanner.nextLine().trim();
        
        List<Integer> droppedPositions = new ArrayList<>();
        
        if (input.equals("0") || input.isEmpty()) {
            System.out.println("No cards dropped.");
            return droppedPositions;
        }
        
        try {
            String[] positions = input.split("\\s+");
            Set<Integer> positionsSet = new TreeSet<>(Collections.reverseOrder());
            
            for (String pos : positions) {
                int position = Integer.parseInt(pos);
                if (position >= 1 && position <= 5) {
                    positionsSet.add(position);
                    droppedPositions.add(position);
                }
            }
            
            if (positionsSet.size() >= 5) {
                System.out.println("Cannot drop all 5 cards. Keeping all cards.");
                droppedPositions.clear();
                return droppedPositions;
            }
            
            List<Card> droppedCards = new ArrayList<>();
            for (int pos : positionsSet) {
                droppedCards.add(player.getHand().remove(pos - 1));
            }
            
            System.out.print("Dropped: ");
            for (Card card : droppedCards) {
                System.out.print(card + " ");
            }
            System.out.println();
            
            // Draw new cards
            for (int i = 0; i < droppedCards.size(); i++) {
                player.addCard(deck.drawCard());
            }
            
            System.out.println("Drew " + droppedCards.size() + " new card(s).");
            
        } catch (Exception e) {
            System.out.println("Invalid input. Keeping all cards.");
            droppedPositions.clear();
        }
        
        return droppedPositions;
    }
    
    private List<Integer> swapCardsComputer(Player player, String bonusSuit) {
        System.out.println("\n[CPU deciding which cards to swap...]");
        
        List<Integer> positionsToSwap = player.computerChooseCardsToSwap(bonusSuit);
        
        if (positionsToSwap.isEmpty()) {
            System.out.println("CPU kept all cards.");
            return positionsToSwap;
        }
        
        // Sort in descending order to remove from back to front
        List<Integer> sortedPositions = new ArrayList<>(positionsToSwap);
        Collections.sort(sortedPositions, Collections.reverseOrder());
        
        List<Card> droppedCards = new ArrayList<>();
        for (int pos : sortedPositions) {
            droppedCards.add(player.getHand().remove(pos - 1));
        }
        
        System.out.print("CPU dropped positions: ");
        for (int pos : positionsToSwap) {
            System.out.print(pos + " ");
        }
        System.out.print(" (Cards: ");
        for (Card card : droppedCards) {
            System.out.print(card + " ");
        }
        System.out.println(")");
        
        // Draw new cards
        for (int i = 0; i < droppedCards.size(); i++) {
            player.addCard(deck.drawCard());
        }
        
        System.out.println("CPU drew " + droppedCards.size() + " new card(s).");
        
        return positionsToSwap;
    }
    
    private void displayRoundScores() {
        for (Player player : players) {
            List<Integer> scores = player.getRoundScores();
            System.out.print(player.getName() + "'s scores: ");
            for (int i = 0; i < scores.size(); i++) {
                System.out.print("Round " + (i + 1) + ": " + scores.get(i) + "  ");
            }
            System.out.println();
        }
    }
    
    private void offerRoundReplay() {
        System.out.print("\nWould you like to see round replays? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        if (!choice.equals("y")) {
            return;
        }
        
        for (Player player : players) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("REPLAY FOR: " + player.getName());
            System.out.println("=".repeat(60));
            
            List<RoundReplay> replays = player.getRoundReplays();
            for (int i = 0; i < replays.size(); i++) {
                replays.get(i).display(i + 1);
                
                if (i < replays.size() - 1) {
                    System.out.print("Press Enter to see next round...");
                    scanner.nextLine();
                }
            }
        }
    }
    
    private void displayResults() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║                    GAME OVER!                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Sort players by total score (descending)
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getTotalScore(), p1.getTotalScore()));
        
        System.out.println("=== FINAL RESULTS ===");
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            System.out.println((i + 1) + ". " + player.getName() + " - " + player.getTotalScore() + " points");
            System.out.print("   Round scores: ");
            for (int j = 0; j < player.getRoundScores().size(); j++) {
                System.out.print(player.getRoundScores().get(j));
                if (j < player.getRoundScores().size() - 1) {
                    System.out.print(" + ");
                }
            }
            System.out.println(" = " + player.getTotalScore());
            System.out.printf("   Average per round: %.2f%n", (double) player.getTotalScore() / numRounds);
        }
        
        System.out.println("\n🏆 WINNER: " + sortedPlayers.get(0).getName() + " 🏆");
    }
    
    private void saveHighScores() {
        for (Player player : players) {
            highScoreManager.addScore(player.getName(), player.getTotalScore(), numRounds);
        }
    }
    
    public static void main(String[] args) {
        CardSuitGame game = new CardSuitGame();
        game.start();
    }
}