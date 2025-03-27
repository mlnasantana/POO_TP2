import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

class GamePanel extends JPanel {
    private HangmanGame game;
    private WordBank wordBank;
    private Player player;
    private String currentWord;
    private StringBuilder displayWord;
    private int guessesLeft = 7;
    private Set<Character> usedLetters;
    private JTextField letterInput;
    private JLabel scoreLabel, hitsLabel, failsLabel, guessesLabel;

    public GamePanel(HangmanGame game, WordBank wordBank, Player player) {
        this.game = game;
        this.wordBank = wordBank;
        this.player = player;
        this.usedLetters = new HashSet<>();
        setLayout(new BorderLayout());
        initComponents();
        startNewGame();
    }

    private void initComponents() {
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        scoreLabel = new JLabel("Score: " + player.getTotalScore());
        hitsLabel = new JLabel("Hits: " + player.getHits());
        failsLabel = new JLabel("Fails: " + player.getFails());
        guessesLabel = new JLabel("Guesses: " + guessesLeft);
        infoPanel.add(scoreLabel);
        infoPanel.add(hitsLabel);
        infoPanel.add(failsLabel);
        infoPanel.add(guessesLabel);
        
        JPanel inputPanel = new JPanel();
        letterInput = new JTextField(1);
        JButton guessButton = new JButton("Adivinhar");
        guessButton.addActionListener(e -> processGuess());
        inputPanel.add(letterInput);
        inputPanel.add(guessButton);

        JPanel controlPanel = new JPanel();
        JButton newGameButton = new JButton("Nova Partida");
        newGameButton.addActionListener(e -> {
            if (currentWord != null) player.incrementFails();
            startNewGame();
        });
        JButton exitButton = new JButton("Sair");
        exitButton.addActionListener(e -> exitGame());
        controlPanel.add(newGameButton);
        controlPanel.add(exitButton);

        add(infoPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHangman(g);
        g.drawString(displayWord.toString(), 350, 300);
        // Substituído toList() por Collectors.toList() para compatibilidade
        g.drawString("Letras usadas: " + String.join(", ", usedLetters.stream().map(String::valueOf).collect(Collectors.toList())), 350, 320);
    }

    private void drawHangman(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int errors = 7 - guessesLeft;
        
        g2d.drawLine(100, 400, 200, 400);
        g2d.drawLine(150, 400, 150, 200);
        g2d.drawLine(150, 200, 250, 200);
        g2d.drawLine(250, 200, 250, 250);

        if (errors >= 1) g2d.drawOval(235, 250, 30, 30);
        if (errors >= 2) g2d.drawLine(250, 280, 250, 340);
        if (errors >= 3) g2d.drawLine(250, 300, 230, 320);
        if (errors >= 4) g2d.drawLine(250, 300, 270, 320);
        if (errors >= 5) g2d.drawLine(250, 340, 230, 360);
        if (errors >= 6) g2d.drawLine(250, 340, 270, 360);
        if (errors >= 7) g2d.drawLine(235, 265, 265, 265);
    }

    private void startNewGame() {
        String lengthStr = JOptionPane.showInputDialog("Digite o tamanho da palavra (3-14):");
        int length;
        try {
            length = Integer.parseInt(lengthStr);
            if (length < 3 || length > 14) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tamanho inválido! Use entre 3 e 14.");
            return;
        }
        
        currentWord = wordBank.getRandomWord(length, player.getUsedWords());
        if (currentWord == null) {
            JOptionPane.showMessageDialog(this, "Não há mais palavras disponíveis desse tamanho!");
            return;
        }
        
        player.getUsedWords().add(currentWord);
        displayWord = new StringBuilder("-".repeat(currentWord.length()));
        guessesLeft = 7;
        usedLetters.clear();
        updateLabels();
        repaint();
    }

    private void processGuess() {
        String input = letterInput.getText().toLowerCase().trim();
        if (input.length() != 1 || !Character.isLetter(input.charAt(0)) || usedLetters.contains(input.charAt(0))) {
            letterInput.setText("");
            return;
        }
        
        char letter = input.charAt(0);
        usedLetters.add(letter);
        
        boolean hit = false;
        for (int i = 0; i < currentWord.length(); i++) {
            if (normalizeChar(currentWord.charAt(i)) == letter) {
                displayWord.setCharAt(i, currentWord.charAt(i));
                hit = true;
            }
        }
        
        if (!hit) guessesLeft--;
        
        if (guessesLeft == 0) {
            endGame(false);
        } else if (!displayWord.toString().contains("-")) {
            endGame(true);
        }
        
        updateLabels();
        repaint();
        letterInput.setText("");
    }

    private char normalizeChar(char c) {
        switch (c) {
            case 'á':
            case 'à':
            case 'â':
            case 'ã':
            case 'ä':
                return 'a';
            case 'é':
            case 'è':
            case 'ê':
            case 'ẽ':
                return 'e';
            case 'í':
            case 'ì':
            case 'î':
            case 'ĩ':
                return 'i';
            case 'ó':
            case 'ò':
            case 'ô':
            case 'õ':
            case 'ö':
                return 'o';
            case 'ú':
            case 'ù':
            case 'û':
            case 'ũ':
                return 'u';
            case 'ç':
                return 'c';
            case 'k':
            case 'w':
            case 'y':
                return c;
            default:
                return Character.toLowerCase(c);
        }
    }

    private void updateLabels() {
        scoreLabel.setText("Score: " + player.getTotalScore());
        hitsLabel.setText("Hits: " + player.getHits());
        failsLabel.setText("Fails: " + player.getFails());
        guessesLabel.setText("Guesses: " + guessesLeft);
    }

    private void showGameStats(String title) {
        JOptionPane.showMessageDialog(this,
            title + "\n" +
            "Palavra: " + (currentWord != null ? currentWord : "N/A") + "\n" +
            "Score: " + player.getTotalScore() + "\n" +
            "Hits: " + player.getHits() + "\n" +
            "Fails: " + player.getFails());
    }

    private void endGame(boolean won) {
        if (won) {
            int score = 10 + (currentWord.length() * 2) - (7 - guessesLeft);
            player.addScore(score);
            player.incrementHits();
        } else {
            player.incrementFails();
        }
        game.savePlayer(player);
        showGameStats("Fim de jogo!");
        startNewGame();
    }

    private void exitGame() {
        game.savePlayer(player);
        showGameStats("Fim de jogo!");
        System.exit(0);
    }
}