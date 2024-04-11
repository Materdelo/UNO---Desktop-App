import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Integer> cards;
    private int numberOfCards;
    private ArrayList<RotatedRectangle> playerCards = new ArrayList<>();

    public Player(String name, ArrayList<Integer> cards, int numberOfCards) {
        this.name = name;
        this.cards = cards;
        this.numberOfCards = numberOfCards;
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

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }
}