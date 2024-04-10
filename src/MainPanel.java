import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainPanel extends JPanel {
    private final int CARD_WIDTH = 80;
    private final int CARD_HEIGHT = 50;
    private int numberOfPlayers = 6;
    private final int RADIUS = 360/numberOfPlayers;
    private int rotationAngle = 360/numberOfPlayers;
    private int semiCircleRotationAngle = 0;
    private int[] cardsPerPlayer = {8, 8, 8, 8, 8, 8};
    private ArrayList<Rectangle> cards = new ArrayList<>();
    private boolean animating = false;
    private int currentPlayer = 0;
    private String[] playerNames = {"Player 0", "Player 1", "Player 2", "Player 3"};
    public MainPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).contains(evt.getPoint())) {
                    if (!animating ){
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
                                    currentPlayer = (currentPlayer + 1) % numberOfPlayers;
                                }
                            }
                        });
                        timer.start();
                    }
                    System.out.println("Card " + i + " clicked");
                    break;
                }
            }
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
        cards.clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            double playerAngle = Math.toRadians(rotationAngle + (double) i * ((double) 360 / numberOfPlayers)) + Math.PI / 2;

            int semiCircleCenterX = (int) (centerX + getWidth() / 2 * Math.cos(playerAngle));
            int semiCircleCenterY = (int) (centerY + getHeight() / 2 * Math.sin(playerAngle));

            for (int j = 0; j < cardsPerPlayer[i]; j++) {
                double cardAngle = Math.toRadians(semiCircleRotationAngle + j * (RADIUS / (cardsPerPlayer[i] - 1)) + (RADIUS * i) + (0.5 + numberOfPlayers * 0.75) * RADIUS);
                int x = (int) (semiCircleCenterX + semiCircleRadiusWidth * Math.cos(cardAngle) - CARD_WIDTH / 2);
                int y = (int) (semiCircleCenterY + semiCircleRadiusHeight * Math.sin(cardAngle) - CARD_HEIGHT / 2);

                graphics2D.rotate(playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
                Rectangle card = new Rectangle(x, y, CARD_WIDTH, CARD_HEIGHT);
                cards.add(card);
                graphics2D.draw(card);
                graphics2D.rotate(-playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
            }
        }
    }
}
