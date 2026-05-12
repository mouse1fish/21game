package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private Stack<NumberCard> pointDeck;
    private Stack<SpecialCard> specialDeck;

    public Deck() {
        pointDeck = new Stack<>();
        specialDeck = new Stack<>();
        initializePointDeck();
        initializeSpecialDeck();
        shuffle();
    }

    private void initializePointDeck() {
        for (int value = 1; value <= 10; value++) {
            int count = (value == 1 || value == 10) ? 4 : 6;
            for (int i = 0; i < count; i++) {
                pointDeck.push(new NumberCard(value));
            }
        }
    }

    private void initializeSpecialDeck() {
        for (int value = 2; value <= 7; value++) {
            addSpecialCard("♠" + value, SpecialCard.Effect.DRAW_MATCH, "♠", String.valueOf(value), value);
            addSpecialCard("♣" + value, SpecialCard.Effect.DRAW_MATCH, "♣", String.valueOf(value), value);
        }

        addSpecialCard("♥2", SpecialCard.Effect.SOUTHERN_INVASION, "♥", "2");
        addSpecialCard("♦2", SpecialCard.Effect.SOUTHERN_INVASION, "♦", "2");
        addSpecialCard("♥3", SpecialCard.Effect.ARROW_BARRAGE, "♥", "3");
        addSpecialCard("♦3", SpecialCard.Effect.ARROW_BARRAGE, "♦", "3");
        addSpecialCard("♥4", SpecialCard.Effect.DESTROY, "♥", "4");
        addSpecialCard("♦4", SpecialCard.Effect.DESTROY, "♦", "4");
        addSpecialCard("♥5", SpecialCard.Effect.INVINCIBLE, "♥", "5");
        addSpecialCard("♦5", SpecialCard.Effect.INVINCIBLE, "♦", "5");
        addSpecialCard("♥6", SpecialCard.Effect.DESIRE, "♥", "6");
        addSpecialCard("♦6", SpecialCard.Effect.DESIRE, "♦", "6");
        addSpecialCard("♥7", SpecialCard.Effect.ADD_ONE, "♥", "7");
        addSpecialCard("♦7", SpecialCard.Effect.ADD_ONE, "♦", "7");

        addSpecialCard("♠8", SpecialCard.Effect.TWENTY_FOUR_RULE, "♠", "8");
        addSpecialCard("♣8", SpecialCard.Effect.TWENTY_FOUR_RULE, "♣", "8");
        addSpecialCard("♥8", SpecialCard.Effect.TWENTY_SEVEN_RULE, "♥", "8");
        addSpecialCard("♦8", SpecialCard.Effect.TWENTY_SEVEN_RULE, "♦", "8");
        addSpecialCard("♠9", SpecialCard.Effect.RETURN_SELF, "♠", "9");
        addSpecialCard("♣9", SpecialCard.Effect.RETURN_SELF, "♣", "9");
        addSpecialCard("♥9", SpecialCard.Effect.HARVEST, "♥", "9");
        addSpecialCard("♦9", SpecialCard.Effect.HARVEST, "♦", "9");
        addSpecialCard("♠10", SpecialCard.Effect.SPECIAL_TRANSFORM, "♠", "10");
        addSpecialCard("♣10", SpecialCard.Effect.SPECIAL_TRANSFORM, "♣", "10");
        addSpecialCard("♥10", SpecialCard.Effect.SWAP, "♥", "10");
        addSpecialCard("♦10", SpecialCard.Effect.SWAP, "♦", "10");

        addSpecialCard("♠J", SpecialCard.Effect.REMOVE, "♠", "J");
        addSpecialCard("♣J", SpecialCard.Effect.REMOVE, "♣", "J");
        addSpecialCard("♥J", SpecialCard.Effect.ALL_OR_NOTHING, "♥", "J");
        addSpecialCard("♦J", SpecialCard.Effect.ALL_OR_NOTHING, "♦", "J");
        addSpecialCard("♠Q", SpecialCard.Effect.EVERYONE_HAPPY, "♠", "Q");
        addSpecialCard("♣Q", SpecialCard.Effect.EVERYONE_HAPPY, "♣", "Q");
        addSpecialCard("♥Q", SpecialCard.Effect.ADD_TWO, "♥", "Q");
        addSpecialCard("♦Q", SpecialCard.Effect.ADD_TWO, "♦", "Q");
        addSpecialCard("♠K", SpecialCard.Effect.RETURN_OPPONENT, "♠", "K");
        addSpecialCard("♣K", SpecialCard.Effect.RETURN_OPPONENT, "♣", "K");
        addSpecialCard("♥K", SpecialCard.Effect.LOVE_ENEMY, "♥", "K");
        addSpecialCard("♦K", SpecialCard.Effect.LOVE_ENEMY, "♦", "K");

        addSpecialCard("♠A", SpecialCard.Effect.SHIELD, "♠", "A");
        addSpecialCard("♣A", SpecialCard.Effect.SHIELD, "♣", "A");
        addSpecialCard("♥A", SpecialCard.Effect.CURSE, "♥", "A");
        addSpecialCard("♦A", SpecialCard.Effect.CURSE, "♦", "A");

        addSpecialCard("大王", SpecialCard.Effect.PERFECT_DRAW, "🃏", "大王");
        addSpecialCard("小王", SpecialCard.Effect.MAGIC_DRAW, "🃏", "小王");
    }

    private void addSpecialCard(String name, SpecialCard.Effect effect, String suit, String rank) {
        SpecialCard card = new SpecialCard(name, effect, suit, rank);
        specialDeck.push(card);
    }

    private void addSpecialCard(String name, SpecialCard.Effect effect, String suit, String rank, int drawValue) {
        SpecialCard card = new SpecialCard(name, effect, suit, rank);
        card.setDrawValue(drawValue);
        specialDeck.push(card);
    }

    public void shuffle() {
        Collections.shuffle(pointDeck);
        Collections.shuffle(specialDeck);
    }

    public NumberCard drawPointCard() {
        if (pointDeck.isEmpty()) {
            return null;
        }
        return pointDeck.pop();
    }

    public SpecialCard drawSpecialCard() {
        if (specialDeck.isEmpty()) {
            return null;
        }
        return specialDeck.pop();
    }

    public void returnPointCard(NumberCard card) {
        pointDeck.push(card);
        Collections.shuffle(pointDeck);
    }

    public void returnSpecialCard(SpecialCard card) {
        specialDeck.push(card);
        Collections.shuffle(specialDeck);
    }

    public List<NumberCard> drawMultiplePointCards(int count) {
        List<NumberCard> cards = new ArrayList<>();
        for (int i = 0; i < count && !pointDeck.isEmpty(); i++) {
            cards.add(pointDeck.pop());
        }
        return cards;
    }

    public List<SpecialCard> drawMultipleSpecialCards(int count) {
        List<SpecialCard> cards = new ArrayList<>();
        for (int i = 0; i < count && !specialDeck.isEmpty(); i++) {
            cards.add(specialDeck.pop());
        }
        return cards;
    }

    public void returnMultiplePointCards(List<NumberCard> cards) {
        pointDeck.addAll(cards);
        Collections.shuffle(pointDeck);
    }

    public int pointDeckSize() {
        return pointDeck.size();
    }

    public int specialDeckSize() {
        return specialDeck.size();
    }
}
