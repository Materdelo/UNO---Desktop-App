import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Card> cards;
    private ArrayList<RotatedRectangle> playerCards = new ArrayList<>();

    public Player(String name, ArrayList<Card> cards) {
        this.name = name;
        this.cards = cards;
    }

    public ArrayList<RotatedRectangle> getPlayerCards() {
        return playerCards;
    }

    public void addPlayerCard(RotatedRectangle card) {
        this.playerCards.add(card);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}