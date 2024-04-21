import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NumberOfPlayerDialog extends JDialog {
    DefaultTableModel model;

    public NumberOfPlayerDialog(JFrame parent) {
        super(parent, "Number of players", true);
        model = new DefaultTableModel(null, new String[]{"Player Name"});
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton addPlayerButton = new JButton("Add Player");
        addPlayerButton.addActionListener(_ -> {
            String playerName = JOptionPane.showInputDialog(this, "Enter Player Name:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                model.addRow(new Object[]{playerName});
            }
        });

        JButton startButton = getStartButton();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(addPlayerButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        setSize(350, 400);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JButton getStartButton() {
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(_ -> {
            if (model.getRowCount() < 2) {
                JOptionPane.showMessageDialog(this, "Minimum 2 players required to start the game");
            } else if (model.getRowCount() > 10){
                JOptionPane.showMessageDialog(this, "Maximum 10 players allowed");
            } else {
                JFrame frame = new JFrame();
                frame.add(new MainPanel(model), BorderLayout.CENTER);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
                frame.setTitle("UNO");
                dispose();
            }
        });
        return startButton;
    }
}