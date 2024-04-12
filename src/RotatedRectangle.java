import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class RotatedRectangle {
    private Rectangle rectangle;
    private double rotationAngle;
    private Card card;

    public RotatedRectangle(Rectangle rectangle, double rotationAngle, Card card){
        this.rectangle = rectangle;
        this.rotationAngle = rotationAngle;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void draw(Graphics2D graphics2D, RotatedRectangle card) {
        AffineTransform oldTransform = graphics2D.getTransform();
        graphics2D.rotate(Math.toRadians(rotationAngle), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        graphics2D.draw(rectangle);
        graphics2D.setColor(card.getCard().getColor().getValue());
        graphics2D.fill(rectangle);
        drawEclipse(graphics2D, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        graphics2D.setColor(Color.BLACK);
        graphics2D.rotate(Math.toRadians(-90), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        graphics2D.drawString(" " + card.getCard().getSymbol().getValue(), rectangle.x + 30, rectangle.y + 20);
        graphics2D.rotate(Math.toRadians(90), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        graphics2D.setTransform(oldTransform);
    }
    public boolean contains(Point2D point) {
        AffineTransform at = new AffineTransform();
        at.rotate(-Math.toRadians(rotationAngle), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        Point2D rotatedPoint = at.transform(point, null);
        return rectangle.contains(rotatedPoint);
    }
    public static void drawEclipse(Graphics2D graphics2D, int x, int y, int CARD_WIDTH, int CARD_HEIGHT){
        Ellipse2D ellipse = new Ellipse2D.Double(x + (double) CARD_WIDTH / 3, y + (double) CARD_HEIGHT / 8, (double) CARD_WIDTH / 3, (double) (CARD_HEIGHT * 6) / 8);
        graphics2D.setColor(Color.WHITE);
        graphics2D.draw(ellipse);
        graphics2D.fill(ellipse);
    }
}