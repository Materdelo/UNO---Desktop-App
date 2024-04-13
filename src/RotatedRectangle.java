import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

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
        graphics2D.drawRect(rectangle.x - 1, rectangle.y, rectangle.width + 1, rectangle.height + 1);
        graphics2D.setColor(card.getCard().getColor().getValue());
        graphics2D.fill(rectangle);
        drawEclipse(graphics2D, new Ellipse2D.Double(rectangle.x + (double) rectangle.width / 3, rectangle.y + (double) rectangle.height / 8, (double) rectangle.width / 3, (double) (rectangle.height * 6) / 8));
        drawSymbol(graphics2D, card, rectangle, 20, rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        graphics2D.setTransform(oldTransform);
    }
    public boolean contains(Point2D point) {
        AffineTransform at = new AffineTransform();
        at.rotate(-Math.toRadians(rotationAngle), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        Point2D rotatedPoint = at.transform(point, null);
        return rectangle.contains(rotatedPoint);
    }
    public static void drawEclipse(Graphics2D graphics2D, Ellipse2D ellipse){
        graphics2D.setColor(Color.WHITE);
        graphics2D.draw(ellipse);
        graphics2D.fill(ellipse);
        graphics2D.setColor(Color.BLACK);
    }
    public static void drawSymbol(Graphics2D graphics2D, RotatedRectangle card, Rectangle rectangle, int sizeFont, double width, double height){
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("Arial", Font.BOLD, sizeFont));
        graphics2D.rotate(Math.toRadians(-90), width, height);
        if(Objects.equals(card.getCard().getSymbol().getValue(), "Reverse")){
            graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 24, rectangle.y + 57);
        } else if (Objects.equals(card.getCard().getSymbol().getValue(), "Skip")) {
            graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 40, rectangle.y + 57);
        } else if (Objects.equals(card.getCard().getSymbol().getValue(), "+2") || Objects.equals(card.getCard().getSymbol().getValue(), "+4")) {
            graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 50, rectangle.y + 57);
        } else {
            graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 56, rectangle.y + 57);
        }
        graphics2D.rotate(Math.toRadians(90), width, height);

    }
}