import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

public class MainPanel extends JPanel {
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 70;
    private int numberOfPlayers = 4;
    private final int RADIUS = 360/numberOfPlayers;
    private int rotationAngle = 360/numberOfPlayers;
    private int semiCircleRotationAngle = 0;
    private ArrayList<RotatedRectangle> cards = new ArrayList<>();
    private ArrayList<RotatedRectangle> mainStack = new ArrayList<>();
//    private Stack<RotatedRectangle> stack = new Stack<>();
    private boolean animating = false;
    private int currentPlayer = 0;
    private ArrayList<Player> players = new ArrayList<>();
    public MainPanel() {
//        for (int i = 0; i < 28; i++) {
//            stack.push(new RotatedRectangle(new Rectangle(0, 0, CARD_WIDTH, CARD_HEIGHT), 0, i));
//        }
        players.add(new Player("Player 0", new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6)),7));
        players.add(new Player("Player 1", new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12, 13)), 7));
        players.add(new Player("Player 2", new ArrayList<>(Arrays.asList(14, 15, 16, 17, 18, 19, 20)), 7));
        players.add(new Player("Player 3", new ArrayList<>(Arrays.asList(21, 22, 23, 24, 25, 26, 27)), 7));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                for (int i = 0; i < players.get(currentPlayer).getPlayerCards().size(); i++) {
                    if (players.get(currentPlayer).getPlayerCards().get(i).contains(evt.getPoint())) {
                        if (!animating) {
                            animating = true;
                            Timer timer = new Timer(10, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    rotationAngle++;
                                    semiCircleRotationAngle++;
                                    repaint();
                                    if (rotationAngle % (360.0 / numberOfPlayers) < 1) {
                                        ((Timer) e.getSource()).stop();
                                        animating = false;
                                    }
                                }
                            });
                            timer.start();
                            RotatedRectangle card = players.get(currentPlayer).getPlayerCards().get(i);
                            mainStack.add(card);
                            System.out.println( players.get(currentPlayer).getName() + ": Card " + i + " clicked");
                            players.get(currentPlayer).getCards().remove(i);
                            players.get(currentPlayer).setNumberOfCards(players.get(currentPlayer).getNumberOfCards() - 1);
                            cards.remove(card);
                            currentPlayer = (currentPlayer + 1) % numberOfPlayers;
                            break;
                        }
                    }
                    repaint();
                }
//                for (int i = 0; i < stack.size(); i++) {
//                    if (stack.get(i).contains(evt.getPoint())) {
//                        System.out.println("Main stack: Card " + i + " clicked");
//                    }
//                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics graphics) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int semiCircleRadiusWidth = getWidth() / 4;
        int semiCircleRadiusHeight = getHeight() / 5;

        Graphics2D graphics2D = (Graphics2D) graphics;

        for (RotatedRectangle card: mainStack){
            Rectangle rectangle = card.getRectangle();
            rectangle.setLocation(centerX - rectangle.width / 2, centerY - rectangle.height / 2);
            card.draw(graphics2D);graphics2D.drawString(" " + card.getNumber(), rectangle.x + 30, rectangle.y + 20);
        }

        FontMetrics fm = graphics2D.getFontMetrics();
        cards.clear();
        players.get(currentPlayer).getPlayerCards().clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            double playerAngle = (Math.toRadians(rotationAngle + (double) (-i) * ((double) 360 / numberOfPlayers)));

            int nameX = (int) (centerX + (semiCircleRadiusWidth + getWidth() / 5) * Math.cos(playerAngle) - fm.stringWidth(players.get(i).getName()) / 3);
            int nameY = (int) (centerY + (semiCircleRadiusHeight + getHeight() / 4) * Math.sin(playerAngle));

            int semiCircleCenterX = (int) (centerX + getWidth() / 2 * Math.cos(playerAngle));
            int semiCircleCenterY = (int) (centerY + getHeight() / 2 * Math.sin(playerAngle));

            for (int j = 0; j < players.get(i).getNumberOfCards(); j++) {
                double cardAngle = Math.toRadians(semiCircleRotationAngle + j * ((double) RADIUS / (players.get(i).getNumberOfCards() - 1)) - (RADIUS * i) + ((1 + numberOfPlayers) * 0.5) * RADIUS);
                int x = (int) (semiCircleCenterX + semiCircleRadiusWidth * Math.cos(cardAngle) - CARD_WIDTH / 2);
                int y = (int) (semiCircleCenterY + semiCircleRadiusHeight * Math.sin(cardAngle) - CARD_HEIGHT / 2);

                graphics2D.rotate(playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
                Rectangle card = new Rectangle(x, y, CARD_WIDTH, CARD_HEIGHT);
                RotatedRectangle rotatedRectangle = new RotatedRectangle(card, Math.toDegrees(playerAngle), players.get(i).getCards().get(j));
                cards.add(rotatedRectangle);
                graphics2D.draw(card);
                graphics2D.drawString(" " + rotatedRectangle.getNumber(), x + 10, y + 20);
                graphics2D.rotate(-playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
                players.get(i).addPlayerCard(rotatedRectangle);
            }
            String playerName = players.get(i).getName();
            graphics2D.drawString(playerName, nameX - fm.stringWidth(playerName) / 2, nameY + fm.getHeight() / 2);
        }
    }
}