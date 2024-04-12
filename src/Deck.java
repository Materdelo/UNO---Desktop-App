import Enums.CardColors;
import Enums.Symbol;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    private Stack<Card> deck = new Stack<>();

    public Deck() {
        for (CardColors color : CardColors.values()) {
            if(color == CardColors.BLACK){
                for (int i = 0; i < 4; i++) {
                    deck.add(new Card(color, Symbol.WILD));
                    deck.add(new Card(color, Symbol.WILD_DRAW_FOUR));
                }
            } else {
                for (Symbol symbol : Symbol.values()) {
                    if(symbol == Symbol.ZERO){
                        deck.add(new Card(color, symbol));
                    } else if (symbol != Symbol.WILD && symbol != Symbol.WILD_DRAW_FOUR){
                        for (int i = 0; i < 2; i++) {
                            deck.add(new Card(color, symbol));
                        }
                    }
                }
            }
        }
        Collections.shuffle(deck);
    }

    public Card drawCard() {
        return deck.pop();
    }
}