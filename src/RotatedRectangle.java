import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RotatedRectangle {
    private Rectangle rectangle;
    private Card card;

    public RotatedRectangle(Rectangle rectangle, Card card){
        this.rectangle = rectangle;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void draw(Graphics2D graphics2D, RotatedRectangle card) {
        graphics2D.drawRect(rectangle.x - 1, rectangle.y - 1, rectangle.width + 1, rectangle.height + 1);
        graphics2D.setColor(card.getCard().getColor().getValue());
        graphics2D.fill(rectangle);
        drawEclipse(graphics2D, new Ellipse2D.Double(rectangle.x + (double) rectangle.width / 3, rectangle.y + (double) rectangle.height / 8, (double) rectangle.width / 3, (double) (rectangle.height * 6) / 8));
        drawSymbol(graphics2D, card, rectangle, 20);
    }
    public static void drawEclipse(Graphics2D graphics2D, Ellipse2D ellipse){
        graphics2D.rotate(Math.toRadians(90), ellipse.getCenterX(), ellipse.getCenterY());
        graphics2D.setColor(Color.WHITE);
        graphics2D.draw(ellipse);
        graphics2D.fill(ellipse);
        graphics2D.setColor(Color.BLACK);
        graphics2D.rotate(Math.toRadians(-90), ellipse.getCenterX(), ellipse.getCenterY());
    }
    public static void drawSymbol(Graphics2D graphics2D, RotatedRectangle card, Rectangle rectangle, int sizeFont){
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("Arial", Font.BOLD, sizeFont));
        switch (card.getCard().getSymbol().getValue()) {
            case "Reverse" ->
                    graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 6, rectangle.y + 73);
            case "Skip" ->
                    graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 26, rectangle.y + 73);
            case "+2", "+4" ->
                    graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 32, rectangle.y + 73);
            case null, default ->
                    graphics2D.drawString(STR." \{card.getCard().getSymbol().getValue()}", rectangle.x + 38, rectangle.y + 73);
        }
    }
}