import Enums.CardColors;

import javax.swing.*;
import java.awt.*;

public class ColorDialog extends JDialog {
    private CardColors color;

    public ColorDialog(JFrame parent) {
        super(parent, "Choose color", true);
        Font font = new Font("Arial", Font.BOLD, 20);
        setLayout(new GridLayout(2, 2));
        for (CardColors color: CardColors.values()) {
            if(color != CardColors.BLACK) {
                JButton button = new JButton(color.toString());
                button.setBackground(color.getValue());
                button.setFont(font);
                button.addActionListener(_ -> {
                    this.color = color;
                    dispose();
                });
                add(button);
            }
        }

        setModal(true);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public CardColors getColor() {
        return color;
    }
}
