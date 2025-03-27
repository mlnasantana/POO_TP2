import javax.swing.*;
import java.awt.*;  // Adicionado para GridLayout

class LoginPanel extends JPanel {
    private HangmanGame game;

    public LoginPanel(HangmanGame game) {
        this.game = game;
        setLayout(new GridLayout(3, 1));
        
        JTextField nameField = new JTextField(20);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                game.startGame("words.txt");
            }
        });
        
        add(new JLabel("Digite seu nome:"));
        add(nameField);
        add(loginButton);
    }
}