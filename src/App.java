import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class App extends JFrame{
    public App() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new EmptyBorder(100, 10, 50, 10));
        JLabel label = new JLabel("UNO");
        label.setFont(new Font("Arial", Font.BOLD, 50));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(_ -> {
            new NumberOfPlayerDialog(this);
            dispose();
        });
        panel.add(startButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(_ -> System.exit(0));
        panel.add(exitButton);

        add(panel, BorderLayout.CENTER);
        setTitle("UNO");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new App();
    }
}