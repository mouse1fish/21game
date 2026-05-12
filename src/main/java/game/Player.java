package game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private boolean isHuman;
    private List<NumberCard> pointCards;
    private List<SpecialCard> trumpCards;
    private List<SpecialCard> activeEffects;
    private int bet;
    private boolean stood;
    private boolean busted;
    private boolean drawBlocked;
    private boolean trumpBlocked;
    private int targetScore;
    private int shieldCount;
    private int harvestCount;
    private int desireCount;
    private int addOneCount;
    private boolean invincibleActive;
    private boolean firstTrumpPlayed;
    private int holeCardIndex;

    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.pointCards = new ArrayList<>();
        this.trumpCards = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
        this.bet = 1;
        this.stood = false;
        this.busted = false;
        this.drawBlocked = false;
        this.trumpBlocked = false;
        this.targetScore = 21;
        this.shieldCount = 0;
        this.harvestCount = 0;
        this.desireCount = 0;
        this.addOneCount = 0;
        this.invincibleActive = false;
        this.firstTrumpPlayed = false;
        this.holeCardIndex = -1;
    }

    public void addPointCard(NumberCard card) {
        pointCards.add(card);
    }

    public void addTrumpCard(SpecialCard card) {
        trumpCards.add(card);
    }

    public NumberCard removeLastPointCard() {
        if (pointCards.isEmpty()) return null;
        return pointCards.remove(pointCards.size() - 1);
    }

    public NumberCard removePointCard(int index) {
        if (index < 0 || index >= pointCards.size()) return null;
        return pointCards.remove(index);
    }

    public SpecialCard getTrumpCard(int index) {
        if (index < 0 || index >= trumpCards.size()) return null;
        return trumpCards.get(index);
    }

    public SpecialCard removeTrumpCard(int index) {
        if (index < 0 || index >= trumpCards.size()) return null;
        return trumpCards.remove(index);
    }

    public int removeHalfTrumpCards() {
        int count = (trumpCards.size() + 1) / 2;
        int removed = 0;
        for (int i = 0; i < count && !trumpCards.isEmpty(); i++) {
            trumpCards.remove(trumpCards.size() - 1);
            removed++;
        }
        return removed;
    }

    public SpecialCard removeRandomTrumpCard() {
        if (trumpCards.isEmpty()) return null;
        int index = (int) (Math.random() * trumpCards.size());
        return trumpCards.remove(index);
    }

    public void addActiveEffect(SpecialCard card) {
        activeEffects.add(card);
    }

    public void removeActiveEffect(SpecialCard.Effect effectType) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            if (activeEffects.get(i).getEffect() == effectType) {
                activeEffects.remove(i);
                return;
            }
        }
    }

    public boolean hasActiveEffect(SpecialCard.Effect effectType) {
        for (SpecialCard card : activeEffects) {
            if (card.getEffect() == effectType) return true;
        }
        return false;
    }

    public int getTotalPoints() {
        int total = 0;
        for (NumberCard card : pointCards) {
            total += card.getPointValue();
        }
        return total;
    }

    public int getEffectiveBet() {
        int effectiveBet = bet;
        effectiveBet = Math.max(0, effectiveBet - shieldCount);
        effectiveBet += addOneCount;
        effectiveBet += desireCount;
        return Math.max(0, effectiveBet);
    }

    public void resetTurnFlags() {
        firstTrumpPlayed = false;
        invincibleActive = false;
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public List<NumberCard> getPointCards() {
        return pointCards;
    }

    public List<SpecialCard> getTrumpCards() {
        return trumpCards;
    }

    public List<SpecialCard> getActiveEffects() {
        return activeEffects;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void addBet(int amount) {
        this.bet += amount;
    }

    public boolean hasStood() {
        return stood;
    }

    public void setStood(boolean stood) {
        this.stood = stood;
    }

    public boolean isBusted() {
        return busted;
    }

    public void setBusted(boolean busted) {
        this.busted = busted;
    }

    public boolean isDrawBlocked() {
        return drawBlocked;
    }

    public void setDrawBlocked(boolean drawBlocked) {
        this.drawBlocked = drawBlocked;
    }

    public boolean isTrumpBlocked() {
        return trumpBlocked;
    }

    public void setTrumpBlocked(boolean trumpBlocked) {
        this.trumpBlocked = trumpBlocked;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }

    public int getShieldCount() {
        return shieldCount;
    }

    public void addShield() {
        shieldCount++;
    }

    public int getHarvestCount() {
        return harvestCount;
    }

    public void setHarvestCount(int count) {
        this.harvestCount = count;
    }

    public int getDesireCount() {
        return desireCount;
    }

    public void addDesire() {
        desireCount++;
    }

    public int getAddOneCount() {
        return addOneCount;
    }

    public void addAddOne() {
        addOneCount++;
    }

    public boolean isInvincibleActive() {
        return invincibleActive;
    }

    public void setInvincibleActive(boolean invincibleActive) {
        this.invincibleActive = invincibleActive;
    }

    public boolean isFirstTrumpPlayed() {
        return firstTrumpPlayed;
    }

    public void setFirstTrumpPlayed(boolean firstTrumpPlayed) {
        this.firstTrumpPlayed = firstTrumpPlayed;
    }

    public NumberCard getLastPointCard() {
        if (pointCards.isEmpty()) return null;
        return pointCards.get(pointCards.size() - 1);
    }

    public int getTrumpCardCount() {
        return trumpCards.size();
    }

    public void clearState() {
        pointCards.clear();
        trumpCards.clear();
        activeEffects.clear();
        bet = 1;
        stood = false;
        busted = false;
        drawBlocked = false;
        trumpBlocked = false;
        targetScore = 21;
        shieldCount = 0;
        harvestCount = 0;
        desireCount = 0;
        addOneCount = 0;
        invincibleActive = false;
        firstTrumpPlayed = false;
        holeCardIndex = -1;
    }

    public int getHoleCardIndex() {
        return holeCardIndex;
    }

    public void setHoleCardIndex(int holeCardIndex) {
        this.holeCardIndex = holeCardIndex;
    }

    public boolean isHoleCard(int index) {
        return index == holeCardIndex;
    }
}
