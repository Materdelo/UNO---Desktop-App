import Enums.CardColors;
import Enums.Symbol;

public class Card {
    private CardColors color;
    private Symbol symbol;

    public Card(CardColors color, Symbol symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public void setColor(CardColors color) {
        this.color = color;
    }

    public CardColors getColor() {
        return color;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
