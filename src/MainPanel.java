import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainPanel extends JPanel {
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 70;
    private int numberOfPlayers = 4;
    private final int RADIUS = 360/numberOfPlayers;
    private int rotationAngle = 360/numberOfPlayers;
    private int semiCircleRotationAngle = 0;
    private ArrayList<RotatedRectangle> mainStack = new ArrayList<>();
    private boolean animating = false;
    private int currentPlayer = 0;
    private ArrayList<Player> players = new ArrayList<>();
    private Image image, bgcImage;
    public MainPanel() {
        Deck deck = new Deck();
        try {
            image = ImageIO.read(getClass().getResource("Images/uno.png"));
            bgcImage = ImageIO.read(getClass().getResource("Images/background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                cards.add(deck.drawCard());
            }
            players.add(new Player(STR."Player \{i + 1}", cards));
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                for (int i = 0; i < players.get(currentPlayer).getPlayerCards().size(); i++) {
                    if (players.get(currentPlayer).getPlayerCards().get(i).contains(evt.getPoint()) && !animating) {
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
                        System.out.println(STR."Card \{card.getCard().getSymbol()} \{card.getCard().getColor()} played");
                        players.get(currentPlayer).getCards().remove(i);
                        currentPlayer = (currentPlayer + 1) % numberOfPlayers;
                        break;
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgcImage, 0, 0, this);
        drawBoard(g);
    }

    private void drawBoard(Graphics graphics) {
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

        players.get(currentPlayer).getPlayerCards().clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            double playerAngle = (Math.toRadians(rotationAngle + (double) (numberOfPlayers - 1 - i) * ((double) 360 / numberOfPlayers))) + Math.toRadians(90);

            int nameX = (int) (centerX + (semiCircleRadiusWidth + getWidth() / 5) * Math.cos(playerAngle) - fm.stringWidth(players.get(i).getName()) / 3);
            int nameY = (int) (centerY + (semiCircleRadiusHeight + getHeight() / 4) * Math.sin(playerAngle));

            int semiCircleCenterX = (int) (centerX + getWidth() / 2 * Math.cos(playerAngle));
            int semiCircleCenterY = (int) (centerY + getHeight() / 2 * Math.sin(playerAngle));

            for (int j = 0; j < players.get(i).getCards().size(); j++) {
                double cardAngle = Math.toRadians(semiCircleRotationAngle + j * ((double) RADIUS / (players.get(i).getCards().size() - 1)) - (RADIUS * i) + (numberOfPlayers * 0.75 - 0.5) * RADIUS);
                int x = (int) (semiCircleCenterX + semiCircleRadiusWidth * Math.cos(cardAngle) - CARD_WIDTH / 2);
                int y = (int) (semiCircleCenterY + semiCircleRadiusHeight * Math.sin(cardAngle) - CARD_HEIGHT / 2);

                if(i != currentPlayer){
                    graphics2D.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
                } else {
                    graphics2D.rotate(playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
                    Rectangle card = new Rectangle(x, y, CARD_WIDTH, CARD_HEIGHT);
                    RotatedRectangle rotatedRectangle = new RotatedRectangle(card, Math.toDegrees(playerAngle), new Card(players.get(i).getCards().get(j).getColor(), players.get(i).getCards().get(j).getSymbol()));
                    graphics2D.setColor(Color.BLACK);
                    graphics2D.drawRect(x - 1, y - 1, CARD_WIDTH + 1, CARD_HEIGHT + 1);
                    graphics2D.setColor(players.get(i).getCards().get(j).getColor().getValue());
                    graphics2D.fill(card);

                    RotatedRectangle.drawEclipse(graphics2D, x, y, CARD_WIDTH, CARD_HEIGHT);

                    graphics2D.setColor(Color.BLACK);
                    graphics2D.drawString(STR." \{rotatedRectangle.getCard().getSymbol().getValue()}", x + 10, y + 20);
                    graphics2D.rotate(-playerAngle, x + (double) CARD_WIDTH / 2, y + (double) CARD_HEIGHT / 2);
                    players.get(i).addPlayerCard(rotatedRectangle);
                }
            }
            String playerName = players.get(i).getName();
            graphics2D.drawString(playerName, nameX - fm.stringWidth(playerName) / 2, nameY + fm.getHeight() / 2);
        }
    }
}