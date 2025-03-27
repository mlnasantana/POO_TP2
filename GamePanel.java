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
    private JLabel scoreLabel, hitsLabel, failsLabel, guessesLabel, wordLabel, usedLettersLabel;

    public GamePanel(HangmanGame game, WordBank wordBank, Player player) {
        this.game = game;
        this.wordBank = wordBank;
        this.player = player;
        this.usedLetters = new HashSet<>();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255));
        initComponents();
        startNewGame();
    }

    private void initComponents() {
        // Painel de informações (topo)
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        infoPanel.setBackground(new Color(173, 216, 230));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        scoreLabel = new JLabel("Score: " + player.getTotalScore());
        hitsLabel = new JLabel("Hits: " + player.getHits());
        failsLabel = new JLabel("Fails: " + player.getFails());
        guessesLabel = new JLabel("Guesses: " + guessesLeft);
        styleLabel(scoreLabel);
        styleLabel(hitsLabel);
        styleLabel(failsLabel);
        styleLabel(guessesLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(hitsLabel);
        infoPanel.add(failsLabel);
        infoPanel.add(guessesLabel);

        // Painel central (forca, palavra e letras usadas)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(getBackground());
        
        // Área da forca (desenhada diretamente no painel central)
        centerPanel.setPreferredSize(new Dimension(300, 400));
        
        // Painel para palavra e letras usadas
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        textPanel.setBackground(getBackground());
        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        wordLabel.setForeground(new Color(25, 25, 112));
        usedLettersLabel = new JLabel("Letras usadas: nenhuma", SwingConstants.CENTER);
        usedLettersLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usedLettersLabel.setForeground(new Color(139, 0, 0));
        textPanel.add(wordLabel);
        textPanel.add(usedLettersLabel);
        
        centerPanel.add(textPanel, BorderLayout.SOUTH);

        // Painel de entrada (baixo)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(getBackground());
        JLabel inputPrompt = new JLabel("Digite uma letra:");
        inputPrompt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        letterInput = new JTextField(2);
        letterInput.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JButton guessButton = new JButton("Adivinhar");
        styleButton(guessButton);
        guessButton.addActionListener(e -> processGuess());
        inputPanel.add(inputPrompt);
        inputPanel.add(letterInput);
        inputPanel.add(guessButton);

        // Painel de controle (direita)
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        controlPanel.setBackground(getBackground());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton newGameButton = new JButton("Nova Partida");
        styleButton(newGameButton);
        newGameButton.addActionListener(e -> {
            if (currentWord != null) player.incrementFails();
            startNewGame();
        });
        JButton exitButton = new JButton("Sair");
        styleButton(exitButton);
        exitButton.addActionListener(e -> exitGame());
        controlPanel.add(newGameButton);
        controlPanel.add(exitButton);

        add(infoPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.EAST);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHangman(g);
    }

    private void drawHangman(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(139, 69, 19)); // Marrom para a forca
        int errors = 7 - guessesLeft;
        
        // Ajustado para aparecer no centro da tela
        int baseX = getWidth() / 4;  // Desloca para a esquerda do centro
        int baseY = getHeight() - 150;  // Ajusta para a parte inferior
        
        g2d.drawLine(baseX - 50, baseY, baseX + 50, baseY); // Base
        g2d.drawLine(baseX, baseY, baseX, baseY - 200);     // Poste
        g2d.drawLine(baseX, baseY - 200, baseX + 100, baseY - 200); // Trave
        g2d.drawLine(baseX + 100, baseY - 200, baseX + 100, baseY - 170); // Corda

        g2d.setColor(Color.BLACK); // Preto para o boneco
        if (errors >= 1) g2d.drawOval(baseX + 85, baseY - 170, 30, 30); // Cabeça
        if (errors >= 2) g2d.drawLine(baseX + 100, baseY - 140, baseX + 100, baseY - 80); // Tronco
        if (errors >= 3) g2d.drawLine(baseX + 100, baseY - 120, baseX + 80, baseY - 100); // Braço esquerdo
        if (errors >= 4) g2d.drawLine(baseX + 100, baseY - 120, baseX + 120, baseY - 100); // Braço direito
        if (errors >= 5) g2d.drawLine(baseX + 100, baseY - 80, baseX + 80, baseY - 60);   // Perna esquerda
        if (errors >= 6) g2d.drawLine(baseX + 100, baseY - 80, baseX + 120, baseY - 60);  // Perna direita
        if (errors >= 7) {
            g2d.setColor(Color.RED);
            g2d.drawLine(baseX + 90, baseY - 160, baseX + 110, baseY - 160); // Risco final
        }
    }

    private void startNewGame() {
        String lengthStr = JOptionPane.showInputDialog(this, "Digite o tamanho da palavra (3-14):", "Nova Partida", JOptionPane.PLAIN_MESSAGE);
        int length;
        try {
            length = Integer.parseInt(lengthStr);
            if (length < 3 || length > 14) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tamanho inválido! Use entre 3 e 14.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentWord = wordBank.getRandomWord(length, player.getUsedWords());
        if (currentWord == null) {
            JOptionPane.showMessageDialog(this, "Não há mais palavras disponíveis desse tamanho!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        player.getUsedWords().add(currentWord);
        displayWord = new StringBuilder("-".repeat(currentWord.length()));
        guessesLeft = 7;
        usedLetters.clear();
        updateDisplay();
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
        
        updateDisplay();
        letterInput.setText("");
    }

    private char normalizeChar(char c) {
        switch (c) {
            case 'á': case 'à': case 'â': case 'ã': case 'ä': return 'a';
            case 'é': case 'è': case 'ê': case 'ẽ': return 'e';
            case 'í': case 'ì': case 'î': case 'ĩ': return 'i';
            case 'ó': case 'ò': case 'ô': case 'õ': case 'ö': return 'o';
            case 'ú': case 'ù': case 'û': case 'ũ': return 'u';
            case 'ç': return 'c';
            case 'k': case 'w': case 'y': return c;
            default: return Character.toLowerCase(c);
        }
    }

    private void updateDisplay() {
        scoreLabel.setText("Score: " + player.getTotalScore());
        hitsLabel.setText("Hits: " + player.getHits());
        failsLabel.setText("Fails: " + player.getFails());
        guessesLabel.setText("Guesses: " + guessesLeft);
        wordLabel.setText(displayWord.toString());
        usedLettersLabel.setText("Letras usadas: " + (usedLetters.isEmpty() ? "nenhuma" : String.join(", ", usedLetters.stream().map(String::valueOf).collect(Collectors.toList()))));
        repaint();
    }

    private void showGameStats(String title) {
        JOptionPane.showMessageDialog(this,
            title + "\n" +
            "Palavra: " + (currentWord != null ? currentWord : "N/A") + "\n" +
            "Score: " + player.getTotalScore() + "\n" +
            "Hits: " + player.getHits() + "\n" +
            "Fails: " + player.getFails(),
            title, JOptionPane.INFORMATION_MESSAGE);
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
        showGameStats(won ? "Vitória!" : "Derrota!");
        startNewGame();
    }

    private void exitGame() {
        game.savePlayer(player);
        showGameStats("Fim de Jogo");
        System.exit(0);
    }
}