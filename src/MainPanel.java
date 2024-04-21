import Enums.CardColors;
import Enums.Symbol;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class MainPanel extends JPanel {
    private final int CARD_WIDTH = 70;
    private final int CARD_HEIGHT = 100;
    private final int numberOfPlayers;
    private double rotationAngle;
    private int semiCircleRotationAngle = 0;
    private ArrayList<RotatedRectangle> mainStack = new ArrayList<>();
    private ArrayList<RotatedRectangle> deckStack = new ArrayList<>();
    private ArrayList<Integer> skipTurns = new ArrayList<>();
    private boolean animating = false;
    private int currentPlayer = 0;
    private ArrayList<Player> players = new ArrayList<>();
    private Image image, bgcImage;
    private boolean reverse = false;
    private boolean mustPlaySkip = false;
    private boolean mustPlayPlus = false;
    private boolean coverCards = false;
    private int cardToDraw = 0;
    private int skipCounter = 1;
    public MainPanel(DefaultTableModel model){
        this.numberOfPlayers = model.getRowCount();
        rotationAngle = (double) 360 / numberOfPlayers;
        Deck deck = new Deck();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player((String) model.getValueAt(i, 0), new ArrayList<>()));
            skipTurns.add(0);
        }
        try {
            image = ImageIO.read(getClass().getResource("Images/uno.png"));
            bgcImage = ImageIO.read(getClass().getResource("Images/background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < numberOfPlayers; j++) {
                players.get(j).getCards().add(deck.drawCard());
            }
        }
        for (Card card: deck.getDeck()){
            deckStack.add(new RotatedRectangle(new Rectangle(getWidth() / 2 - CARD_WIDTH / 2 + 130, getHeight() / 2 - CARD_HEIGHT / 2, CARD_WIDTH, CARD_HEIGHT), card));
        }
        do{
            mainStack.add(new RotatedRectangle(new Rectangle(0, 0, CARD_WIDTH + 30, CARD_HEIGHT + 30), deckStack.removeLast().getCard()));
        } while (mainStack.getLast().getCard().getColor() == CardColors.BLACK || mainStack.getLast().getCard().getSymbol() == Symbol.DRAW_TWO || mainStack.getLast().getCard().getSymbol() == Symbol.SKIP || mainStack.getLast().getCard().getSymbol() == Symbol.REVERSE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
            for (int i = players.get(currentPlayer).getPlayerCards().size() - 1; i >= 0; i--) {
                RotatedRectangle card = players.get(currentPlayer).getPlayerCards().get(i);
                if (card.getRectangle().contains(evt.getPoint()) && !animating) {
                    Card topCard = mainStack.getLast().getCard();
                    Card playerCard = card.getCard();
                    if (canPlayCard(playerCard, topCard)) {
                        if (playerCard.getSymbol() == Symbol.SKIP && !mustPlayPlus && !animating) {
                            players.get(currentPlayer).getCards().remove(i);
                            skipPlayedCard(card);
                            break;
                        } else if (playerCard.getSymbol() == Symbol.DRAW_TWO && !mustPlaySkip) {
                            cardToDraw += 2;
                            mustPlayPlus = true;
                            players.get(currentPlayer).getCards().remove(i);
                            mainStack.add(card);
                            playedCard();
                            break;
                        } else if (playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR && !mustPlaySkip) {
                            playedBlackCard(playerCard, i);
                            mustPlayPlus = true;
                            cardToDraw += 4;
                            players.get(currentPlayer).getCards().remove(i);
                            mainStack.add(card);
                            playedCard();
                            break;
                        } else if (!mustPlaySkip && !mustPlayPlus) {
                            playedBlackCard(playerCard, i);
                            if (playerCard.getSymbol() == Symbol.REVERSE) {
                                reverse = !reverse;
                            }
                            players.get(currentPlayer).getCards().remove(i);
                            mainStack.add(card);
                            playedCard();
                            mustPlaySkip = false;
                            mustPlayPlus = false;
                            break;
                        }
                    }
                }
                repaint();
            }
            if (!mustPlaySkip) {
                for (int i = 0; i < deckStack.size(); i++) {
                    RotatedRectangle card = deckStack.get(i);
                    card.getRectangle().setLocation(getWidth() / 2 - CARD_WIDTH / 2 + 130, getHeight() / 2 - CARD_HEIGHT / 2);
                    if (card.getRectangle().contains(evt.getPoint()) && !animating && !mustPlaySkip) {
                        deckStack.remove(i);
                        DrawnCardDialog drawCardDialog = new DrawnCardDialog((JFrame) SwingUtilities.getWindowAncestor(MainPanel.this), card, mainStack.getLast(), mustPlayPlus);
                        if (drawCardDialog.isPlay()) {
                            card.getRectangle().setBounds(new Rectangle(0, 0, CARD_WIDTH + 30, CARD_HEIGHT + 30));
                            if (card.getCard().getSymbol() == Symbol.SKIP && !animating) {
                                players.get(currentPlayer).getCards().remove(i);
                                skipPlayedCard(card);
                                break;
                            } else if (card.getCard().getSymbol() == Symbol.DRAW_TWO) {
                                if (mustPlayPlus) {
                                    cardToDraw += 1;
                                } else {
                                    cardToDraw += 2;
                                    mustPlayPlus = true;
                                }
                                mainStack.add(card);
                                playedCard();
                                break;
                            } else if (card.getCard().getColor() == CardColors.BLACK) {
                                ColorDialog colorDialog = new ColorDialog((JFrame) SwingUtilities.getWindowAncestor(MainPanel.this));
                                card.getCard().setColor(colorDialog.getColor());
                                if (card.getCard().getSymbol() == Symbol.WILD_DRAW_FOUR) {
                                    if (mustPlayPlus) {
                                        cardToDraw += 3;
                                    } else {
                                        cardToDraw += 4;
                                        mustPlayPlus = true;
                                    }
                                }
                                mainStack.add(card);
                                playedCard();
                                break;
                            } else if (card.getCard().getSymbol() == Symbol.REVERSE) {
                                reverse = !reverse;
                                mainStack.add(card);
                                playedCard();
                                break;
                            } else {
                                mainStack.add(card);
                                playedCard();
                                break;
                            }
                        } else {
                            if (mustPlayPlus) {
                                players.get(currentPlayer).getCards().add(card.getCard());
                                for (int j = 0; j < cardToDraw - 1; j++) {
                                    players.get(currentPlayer).getCards().add(deckStack.getFirst().getCard());
                                    deckStack.removeFirst();
                                }
                                cardToDraw = 0;
                                mustPlayPlus = false;
                            } else {
                                players.get(currentPlayer).getCards().add(card.getCard());
                            }
                            playedCard();
                            mustPlaySkip = false;
                        }
                    }
                }
            }
            if(deckStack.isEmpty()){
                while (mainStack.size() > 1){
                    RotatedRectangle card = mainStack.removeFirst();
                    card.getCard().resetColor();
                    deckStack.add(card);
                }
                Collections.shuffle(deckStack);
            }
            }
        });
    }

    private void playedCard(){
        coverCards = true;
        repaint();

        if (players.get(currentPlayer).getCards().isEmpty()) {
            JOptionPane.showMessageDialog(MainPanel.this, STR."Congratulations \{players.get(currentPlayer).getName()}! You won the game", "End Game", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.getWindowAncestor(MainPanel.this).dispose();
            SwingUtilities.invokeLater(App::new);
        } else {
            if(players.get(currentPlayer).getCards().size() == 1){
                JOptionPane.showMessageDialog(this, "You must said UNO", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            int nextPlayerIndex = reverse ? (currentPlayer - 1 + numberOfPlayers) % numberOfPlayers : (currentPlayer + 1) % numberOfPlayers;
            while (skipTurns.get(nextPlayerIndex) > 0) {
                nextPlayerIndex = reverse ? (nextPlayerIndex - 1 + numberOfPlayers) % numberOfPlayers : (nextPlayerIndex + 1) % numberOfPlayers;
            }
            JOptionPane.showMessageDialog(this, STR."\{players.get(nextPlayerIndex).getName()} turn", "Info", JOptionPane.INFORMATION_MESSAGE);
            coverCards = false;
            changePlayer();
            animation();
            skipTurns();
        }
    }

    private void skipPlayedCard(RotatedRectangle card){
        mainStack.add(card);
        playedCard();
        mustPlaySkip = true;
        SkipDialog skipDialog = new SkipDialog((JFrame) SwingUtilities.getWindowAncestor(MainPanel.this), players, currentPlayer, new CountDownLatch(1), skipCounter);
        if (skipDialog.isSkip()){
            skipCounter++;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                for (int i = players.get(currentPlayer).getPlayerCards().size() - 1; i >= 0; i--) {
                    RotatedRectangle card = players.get(currentPlayer).getPlayerCards().get(i);
                    if (card.getRectangle().contains(evt.getPoint()) && !animating) {
                        Card playerCard = card.getCard();
                        if (playerCard.getSymbol() == Symbol.SKIP) {
                            break;
                        }
                    }
                }
                }
            });
        } else {
            coverCards = true;
            repaint();
            JOptionPane.showMessageDialog(this, STR."\{players.get(reverse ? (currentPlayer - 1 + numberOfPlayers) % numberOfPlayers : (currentPlayer + 1) % numberOfPlayers).getName()} turn", "Info", JOptionPane.INFORMATION_MESSAGE);
            coverCards = false;
            skipTurns.set(currentPlayer, skipTurns.get(currentPlayer) + skipCounter);
            skipCounter = 1;
            mustPlayPlus = false;
            mustPlaySkip = false;
        }
        skipTurns();
    }

    private void playedBlackCard(Card playerCard, int i){
        if (playerCard.getColor() == CardColors.BLACK) {
            ColorDialog colorDialog = new ColorDialog((JFrame) SwingUtilities.getWindowAncestor(MainPanel.this));
            playerCard.setColor(colorDialog.getColor());
            players.get(currentPlayer).getPlayerCards().get(i).getCard().setColor(colorDialog.getColor());
        }
    }

    private void changePlayer(){
        if (reverse) {
            currentPlayer = (currentPlayer - 1 + numberOfPlayers) % numberOfPlayers;
        } else {
            currentPlayer = (currentPlayer + 1) % numberOfPlayers;
        }
    }

    private void skipTurns(){
        while (skipTurns.get(currentPlayer) > 0){
            skipTurns.set(currentPlayer, skipTurns.get(currentPlayer) - 1);
            animation();
            changePlayer();
        }
    }

    private void animation (){
        animating = true;
        Timer timer = new Timer(10, e -> {
            if (reverse){
                rotationAngle--;
                semiCircleRotationAngle--;
            } else {
                rotationAngle++;
                semiCircleRotationAngle++;
            }
            if (Math.abs(rotationAngle) % (360.0 / numberOfPlayers) < 1) {
                ((Timer) e.getSource()).stop();
                animating = false;
            }
            repaint();
        });
        timer.start();
    }

    private boolean canPlayCard(Card playerCard, Card topCard){
        return isSameColor(playerCard, topCard) || isSameSymbol(playerCard, topCard ) || isBlackCard(playerCard) || isDrawTwoCard(playerCard, topCard) || isDrawFourCard(playerCard, topCard);
    }

    private boolean isSameColor(Card playerCard, Card topCard){
        return playerCard.getColor() == topCard.getColor();
    }

    private boolean isSameSymbol(Card playerCard, Card topCard){
        return playerCard.getSymbol() == topCard.getSymbol();
    }

    private boolean isBlackCard(Card playerCard){
        return playerCard.getColor() == CardColors.BLACK;
    }

    private boolean isDrawTwoCard(Card playerCard, Card topCard){
        return topCard.getSymbol() == Symbol.DRAW_TWO && (playerCard.getSymbol() == Symbol.DRAW_TWO || playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR) ;
    }
    private boolean isDrawFourCard(Card playerCard, Card topCard){
        return topCard.getSymbol() == Symbol.WILD_DRAW_FOUR && (playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR || (playerCard.getSymbol() == Symbol.DRAW_TWO && playerCard.getColor() == topCard.getColor()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgcImage, 0, 0, this);
        drawBoard(g);
    }

    private void drawBoard(Graphics graphics) {
        ArrayList<RotatedRectangle> currentPlayerCards = new ArrayList<>();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int semiCircleRadiusWidth = getWidth() / 4;
        int semiCircleRadiusHeight = getHeight() / 5;

        Graphics2D graphics2D = (Graphics2D) graphics;

        FontMetrics fm = graphics2D.getFontMetrics();

        for (RotatedRectangle card: mainStack){
            card.getRectangle().setLocation(centerX - card.getRectangle().width / 2, centerY - card.getRectangle().height / 2);
            card.draw(graphics2D, card);
        }
        for (RotatedRectangle _ : deckStack){
            graphics2D.drawImage(image, centerX - CARD_WIDTH / 2 + 130, centerY - CARD_HEIGHT / 2, CARD_WIDTH, CARD_HEIGHT, null);
            if(cardToDraw > 0) {
                graphics2D.drawString(STR."+\{cardToDraw}", centerX - CARD_WIDTH / 2 + 150, centerY - CARD_HEIGHT/2 - 20);
            }
        }

        players.get(currentPlayer).getPlayerCards().clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            double playerAngle = (Math.toRadians(rotationAngle + (double) (numberOfPlayers - 1 - i) * ((double) 360 / numberOfPlayers))) + Math.toRadians(90);

            int nameX = (int) (centerX + (semiCircleRadiusWidth + getWidth() / 5) * Math.cos(playerAngle) - fm.stringWidth(players.get(i).getName()) / 3);
            int nameY = (int) (centerY + (semiCircleRadiusHeight + getHeight() / 4) * Math.sin(playerAngle));

            int semiCircleCenterX = (int) (centerX + getWidth() / 2 * Math.cos(playerAngle));
            int semiCircleCenterY = (int) (centerY + getHeight() / 2 * Math.sin(playerAngle));

            for (int j = 0; j < players.get(i).getCards().size(); j++) {
                int RADIUS = 360 / numberOfPlayers;
                double cardAngle = Math.toRadians(semiCircleRotationAngle + j * ((double) 90 / (players.get(i).getCards().size() - 1)) - (RADIUS * i) + (0.625 * numberOfPlayers) * RADIUS);
                if (players.get(i).getCards().size() == 1) {
                    cardAngle = Math.toRadians(semiCircleRotationAngle - (RADIUS * i)) - Math.toRadians(90);
                } else if (currentPlayer == i){
                    semiCircleRadiusWidth = getWidth() * 2 / 5;
                } else {
                    semiCircleRadiusWidth = getWidth() / 5;
                }
                int x = (int) (semiCircleCenterX + semiCircleRadiusWidth * Math.cos(cardAngle) - CARD_WIDTH / 2);
                int y = (int) (semiCircleCenterY + semiCircleRadiusHeight * Math.sin(cardAngle) - CARD_HEIGHT / 2);

                if(i != currentPlayer || coverCards){
                    graphics2D.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
                } else {
                    Rectangle card = new Rectangle(x, y, CARD_WIDTH + 30, CARD_HEIGHT + 30);
                    RotatedRectangle rotatedRectangle = new RotatedRectangle(card, new Card(players.get(i).getCards().get(j).getColor(), players.get(i).getCards().get(j).getSymbol()));
                    currentPlayerCards.add(rotatedRectangle);
                    players.get(i).addPlayerCard(rotatedRectangle);
                }
            }
            semiCircleRadiusWidth = getWidth() / 5;
            String playerName = players.get(i).getName();
            graphics2D.setColor(Color.BLACK);
            graphics2D.setFont(new Font("Arial", Font.BOLD, 20));
            graphics2D.drawString(playerName, nameX - fm.stringWidth(playerName) / 2, nameY + fm.getHeight() / 2);
            if(skipTurns.get(i) > 0){
                graphics2D.setFont(new Font("Arial", Font.PLAIN, 15));
                graphics2D.drawString(STR."Has been blocked on \{skipTurns.get(i)} turns", nameX - fm.stringWidth(playerName) / 2 - 50, nameY + fm.getHeight() / 2 + 20);
            }
        }
        for (RotatedRectangle card: currentPlayerCards){
            card.draw(graphics2D, card);
        }
    }
}