package Enums;

import java.awt.*;

public enum CardColors {
    RED(Color.RED),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    BLACK(Color.BLACK);

    private final Color value;

    public Color getValue() {
        return value;
    }

    CardColors(Color value) {
        this.value = value;
    }
}