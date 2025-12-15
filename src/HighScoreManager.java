import java.util.*;
import java.io.*;



class HighScoreManager {
    private static final String FILE_NAME = "highscores.txt";
    private List<ScoreEntry> highScores;
    
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public void addScore(String playerName, int totalScore, int numRounds) {
        double avgScore = (double) totalScore / numRounds;
        highScores.add(new ScoreEntry(playerName, avgScore, totalScore, numRounds));
        Collections.sort(highScores);
        
        // Keep only top 5
        if (highScores.size() > 5) {
            highScores = new ArrayList<>(highScores.subList(0, 5));
        }
        
        saveHighScores();
    }
    
    public void displayHighScores() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║            TOP 5 HIGH SCORES (AVG/ROUND)           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        if (highScores.isEmpty()) {
            System.out.println("No high scores yet!");
        } else {
            for (int i = 0; i < highScores.size(); i++) {
                ScoreEntry entry = highScores.get(i);
                System.out.printf("%d. %-20s %.2f (Total: %d in %d rounds)%n", 
                    i + 1, entry.getName(), entry.getAvgScore(), 
                    entry.getTotalScore(), entry.getNumRounds());
            }
        }
        System.out.println();
    }
    
    private void loadHighScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int totalScore = Integer.parseInt(parts[1]);
                    int numRounds = Integer.parseInt(parts[2]);
                    double avgScore = (double) totalScore / numRounds;
                    highScores.add(new ScoreEntry(name, avgScore, totalScore, numRounds));
                }
            }
            Collections.sort(highScores);
        } catch (IOException e) {
            // File doesn't exist yet, that's okay
        }
    }
    
    private void saveHighScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry entry : highScores) {
                pw.println(entry.getName() + "," + entry.getTotalScore() + "," + entry.getNumRounds());
            }
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
        }
    }
    
    static class ScoreEntry implements Comparable<ScoreEntry> {
        private String name;
        private double avgScore;
        private int totalScore;
        private int numRounds;
        
        public ScoreEntry(String name, double avgScore, int totalScore, int numRounds) {
            this.name = name;
            this.avgScore = avgScore;
            this.totalScore = totalScore;
            this.numRounds = numRounds;
        }
        
        public String getName() { return name; }
        public double getAvgScore() { return avgScore; }
        public int getTotalScore() { return totalScore; }
        public int getNumRounds() { return numRounds; }
        
        @Override
        public int compareTo(ScoreEntry other) {
            return Double.compare(other.avgScore, this.avgScore); // Descending order
        }
    }
}