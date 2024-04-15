import Enums.Symbol;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SkipDialog extends JDialog {
    private boolean skip = false;

    public SkipDialog(JFrame parent, ArrayList<Player> players, int currentPlayer, CountDownLatch latch, int skipCounter) {
        super(parent, "Skip Turn", true);
        setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        Font font = new Font("Arial", Font.BOLD, 20);
        JLabel label = new JLabel(STR."Do you want to skip \{skipCounter} turn?");
        label.setFont(font);

        JButton acceptButton = new JButton("Take");
        acceptButton.addActionListener(_ -> {
            skip = false;
            dispose();
            latch.countDown();
        });

        add(label, BorderLayout.NORTH);
        if(canPlayCard(players, currentPlayer)) {
            JButton beatButton = new JButton("Beat");
            beatButton.addActionListener(_ -> {
                skip = true;
                dispose();
                latch.countDown();
            });
            panel.add(beatButton, BorderLayout.EAST);
        }
        panel.add(acceptButton, BorderLayout.WEST);

        add(panel, BorderLayout.SOUTH);
        setModal(true);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public boolean isSkip() {
        return skip;
    }


    private boolean canPlayCard(ArrayList<Player> players, int currentPlayer) {
        for (Card card: players.get(currentPlayer).getCards()){
            if (card.getSymbol() == Symbol.SKIP){
                System.out.println(STR."Can play card: \{card.getSymbol()}");
                return true;
            }
        }
        return false;
    }
}