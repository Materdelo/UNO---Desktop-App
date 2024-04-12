package Enums;

public enum Symbol {
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    SKIP("Skip"),
    REVERSE("Reverse"),
    DRAW_TWO("+2"),
    WILD(""),
    WILD_DRAW_FOUR("+4");

    private final String value;

    public String getValue() {
        return value;
    }

    Symbol(String value) {
        this.value = value;
    }
}
