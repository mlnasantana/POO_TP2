import javax.swing.*;
import java.util.*;

public class HangmanGame extends JFrame {
    private WordBank wordBank;
    private GamePanel gamePanel;
    private Player currentPlayer;
    private Map<String, Player> players;

    public HangmanGame() {
        setTitle("Jogo da Forca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        players = loadPlayers();
        showLoginScreen();
    }

    public void startGame(String wordFilePath) {  // Alterado para public
        wordBank = new WordBank(wordFilePath);
        currentPlayer = players.getOrDefault("Default", new Player("Default"));
        gamePanel = new GamePanel(this, wordBank, currentPlayer);
        setContentPane(gamePanel);
        revalidate();
        repaint();
    }

    private void showLoginScreen() {
        LoginPanel loginPanel = new LoginPanel(this);
        setContentPane(loginPanel);
    }

    public void savePlayer(Player player) {
        players.put(player.getName(), player);
        savePlayers();
    }

    private Map<String, Player> loadPlayers() {
        return new HashMap<>();
    }

    private void savePlayers() {
        // Implementar salvamento se necessário
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Por favor, forneça o caminho do arquivo de palavras como argumento");
            System.out.println("Exemplo: java HangmanGame words.txt");
            System.exit(1);
        }
        
        SwingUtilities.invokeLater(() -> {
            HangmanGame game = new HangmanGame();
            game.setVisible(true);
            game.startGame(args[0]);
        });
    }
}