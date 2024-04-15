import Enums.CardColors;
import Enums.Symbol;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class MainPanel extends JPanel {
    private final int CARD_WIDTH = 70;
    private final int CARD_HEIGHT = 100;
    private final int numberOfPlayers = 4;
    private double rotationAngle = (double) 360 / numberOfPlayers;
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
    private int skipCounter = 1;
    public MainPanel() {
        Deck deck = new Deck();
        try {
            image = ImageIO.read(getClass().getResource("Images/uno.png"));
            bgcImage = ImageIO.read(getClass().getResource("Images/background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            skipTurns.add(0);
            ArrayList<Card> cards = new ArrayList<>();
            for (int j = 0; j < 16; j++) {
                cards.add(deck.drawCard());
            }
            players.add(new Player(STR."Player \{i + 1}", cards));
        }
        for (Card card: deck.getDeck()){
            RotatedRectangle rotatedRectangle = new RotatedRectangle(new Rectangle(getWidth() / 2 - CARD_WIDTH / 2 + 130, getHeight() / 2 - CARD_HEIGHT / 2, CARD_WIDTH, CARD_HEIGHT), card);
            deckStack.add(rotatedRectangle);
        }
        mainStack.add(new RotatedRectangle(new Rectangle(0, 0, CARD_WIDTH + 30, CARD_HEIGHT + 30), deck.drawCard()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                for (int i = players.get(currentPlayer).getPlayerCards().size() - 1; i >= 0; i--) {
                    RotatedRectangle card = players.get(currentPlayer).getPlayerCards().get(i);
                    if (card.getRectangle().contains(evt.getPoint()) && !animating) {
                        Card topCard = mainStack.getLast().getCard();
                        Card playerCard = card.getCard();
                        if (canPlayCard(playerCard, topCard)) {
//                            if (playerCard.getColor() == CardColors.BLACK) {
//                                  WYbieranie koloru                        players.get(currentPlayer).getPlayerCards().get(i).getCard().setColor());
//                            }
                            if (playerCard.getSymbol() == Symbol.SKIP) {
                                mainStack.add(card);
                                players.get(currentPlayer).getCards().remove(i);
                                animation();
                                changePlayer();
                                skipTurns();
                                mustPlaySkip = true;
                                CountDownLatch latch = new CountDownLatch(1);
                                System.out.println(skipCounter);
                                SkipDialog skipDialog = new SkipDialog((JFrame) SwingUtilities.getWindowAncestor(MainPanel.this), players, currentPlayer, latch, skipCounter);
                                try {
                                    latch.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
                                                        mainStack.add(card);
                                                        players.get(currentPlayer).getCards().remove(i);
                                                        animation();
                                                        changePlayer();
                                                        skipTurns();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    skipTurns.set(currentPlayer, skipTurns.get(currentPlayer) + skipCounter + 1);
                                    skipCounter = 1;
                                    mustPlaySkip = false;
                                }
                                skipTurns();
                                break;
                            } else if(!mustPlaySkip) {
                                if (playerCard.getSymbol() == Symbol.REVERSE) {
                                    reverse = !reverse;
                                    animation();
                                    mainStack.add(card);
                                    System.out.println(STR."Card \{card.getCard().getSymbol()} \{card.getCard().getColor()} played");
                                } else {
                                    animation();
                                    mainStack.add(card);
                                    System.out.println(STR."Card \{card.getCard().getSymbol()} \{card.getCard().getColor()} played");
                                }
                                players.get(currentPlayer).getCards().remove(i);
                                changePlayer();
                                skipTurns();
                                mustPlaySkip = false;
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
                            players.get(currentPlayer).getCards().add(card.getCard());
                            deckStack.remove(i);
                            animation();
                            changePlayer();
                            skipTurns();
                            mustPlaySkip = false;
                            break;
                        }
                    }
                }
                if (isEndGame()){
                    System.out.println("End game");
                    System.exit(0);
                }
            }
        });
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

    private boolean isEndGame(){
        for (Player player: players){
            if (player.getCards().isEmpty()){
                return true;
            }
        }
        return false;
    }

    private boolean canPlayCard(Card playerCard, Card topCard){
        return isSameColor(playerCard, topCard) || isSameSymbol(playerCard, topCard ) || isBlackCard(playerCard)/* || isDrawTwoCard(playerCard, topCard) || isDrawFourCard(playerCard, topCard)*/;
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

//    private boolean isDrawTwoCard(Card playerCard, Card topCard){
//        return topCard.getSymbol() == Symbol.DRAW_TWO && (playerCard.getSymbol() == Symbol.DRAW_TWO || playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR);
//    }
//    private boolean isDrawFourCard(Card playerCard, Card topCard){
//        return topCard.getSymbol() == Symbol.WILD_DRAW_FOUR && playerCard.getSymbol() == Symbol.WILD_DRAW_FOUR;
//    }

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

                if(i != currentPlayer){
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