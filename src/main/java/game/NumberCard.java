package game;

public class NumberCard extends Card {
    private int pointValue;

    public NumberCard(int pointValue) {
        super("点数牌", "", String.valueOf(pointValue));
        this.pointValue = pointValue;
        this.description = "点数: " + pointValue;
    }

    @Override
    public String getType() {
        return "NUMBER";
    }

    @Override
    public int getPointValue() {
        return pointValue;
    }

    @Override
    public String getDisplayText() {
        return String.valueOf(pointValue);
    }
}
