import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class RotatedRectangle {
    private Rectangle rectangle;
    private double rotationAngle;
    private int number;

    public RotatedRectangle(Rectangle rectangle, double rotationAngle, int number) {
        this.rectangle = rectangle;
        this.rotationAngle = rotationAngle;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void draw(Graphics2D graphics2D) {
        AffineTransform oldTransform = graphics2D.getTransform();
        graphics2D.rotate(Math.toRadians(rotationAngle), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        graphics2D.draw(rectangle);
        graphics2D.setTransform(oldTransform);
    }
    public boolean contains(Point2D point) {
        AffineTransform at = new AffineTransform();
        at.rotate(-Math.toRadians(rotationAngle), rectangle.x + (double) rectangle.width / 2, rectangle.y + (double) rectangle.height / 2);
        Point2D rotatedPoint = at.transform(point, null);
        return rectangle.contains(rotatedPoint);
    }
}