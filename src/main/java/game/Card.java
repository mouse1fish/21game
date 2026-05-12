package game;

public abstract class Card {
    protected String name;
    protected String description;
    protected String suit;
    protected String rank;
    protected boolean faceUp;

    public Card(String name, String suit, String rank) {
        this.name = name;
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
        this.description = "";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public abstract String getType();

    public abstract int getPointValue();

    public String getDisplayText() {
        return suit + rank;
    }
}
