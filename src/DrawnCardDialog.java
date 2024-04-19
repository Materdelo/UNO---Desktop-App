import Enums.CardColors;
import Enums.Symbol;

import javax.swing.*;
import java.awt.*;

public class DrawnCardDialog extends JDialog {
    boolean play = false;
    boolean mustPlayPlus;

    public boolean isPlay() {
        return play;
    }

    public DrawnCardDialog(JFrame parent, RotatedRectangle card, RotatedRectangle topCard, boolean mustPlayPlus) {
        super(parent, "Draw Card", true);
        this.mustPlayPlus = mustPlayPlus;

        JPanel panel = new JPanel();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 2));
        panel.setLayout(new BorderLayout());
        JPanel cardPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            card.getRectangle().setBounds(getWidth() / 2 - 50, getHeight() / 2 - 65, 100, 130);
            card.draw((Graphics2D) g, card);
            }
        };
        panel.add(cardPanel, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

        if (canPlayCard(card.getCard(), topCard.getCard())){
            JButton playButton = new JButton("Play");
            playButton.setAlignmentX(CENTER_ALIGNMENT);
            playButton.addActionListener(_ -> {
                play = true;
                dispose();
            });
            controlPanel.add(playButton);
        }
        JButton takeButton = new JButton("Take");
        takeButton.setAlignmentX(CENTER_ALIGNMENT);
        takeButton.addActionListener(_ -> {
            play = false;
            dispose();
        });
        controlPanel.add(takeButton);

        add(controlPanel, BorderLayout.SOUTH);
        setModal(true);
        setSize(200, 250);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private boolean canPlayCard(Card playerCard, Card topCard) {
        return isSameColor(playerCard, topCard) || isSameSymbol(playerCard, topCard) || isBlackCard(playerCard) || isDrawTwoCard(playerCard, topCard) || isDrawFourCard(playerCard, topCard);
    }

    private boolean isSameColor(Card playerCard, Card topCard){
        return playerCard.getColor() == topCard.getColor() && !mustPlayPlus;
    }

    private boolean isSameSymbol(Card playerCard, Card topCard){
        return playerCard.getSymbol() == topCard.getSymbol() && !mustPlayPlus;
    }

    private boolean isBlackCard(Card playerCard){
        return playerCard.getColor() == CardColors.BLACK && !mustPlayPlus;
    }

    private boolean isDrawTwoCard(Card playerCard, Card topCard){
        return topCard.getSymbol() == Symbol.DRAW_TWO && (playerCard.getSymbol() == Symbol.DRAW_TWO || playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR) ;
    }
    private boolean isDrawFourCard(Card playerCard, Card topCard){
        return topCard.getSymbol() == Symbol.WILD_DRAW_FOUR && (playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR || (playerCard.getSymbol() == Symbol.DRAW_TWO && playerCard.getColor() == topCard.getColor()));
    }
}
