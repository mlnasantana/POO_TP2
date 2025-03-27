import java.util.*;

class Player {
    private String name;
    private int totalScore;
    private int hits;
    private int fails;
    private Set<String> usedWords;

    public Player(String name) {
        this.name = name;
        this.totalScore = 0;
        this.hits = 0;
        this.fails = 0;
        this.usedWords = new HashSet<>();
    }

    public String getName() { return name; }
    public int getTotalScore() { return totalScore; }
    public int getHits() { return hits; }
    public int getFails() { return fails; }
    public Set<String> getUsedWords() { return usedWords; }

    public void addScore(int score) { totalScore += score; }
    public void incrementHits() { hits++; }
    public void incrementFails() { fails++; }
}