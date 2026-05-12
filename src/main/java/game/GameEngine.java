package game;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameActive;
    private String lastMessage;
    private int roundCount;
    private boolean waitingForCardSelection;
    private List<NumberCard> selectionCards;
    private boolean waitingForTrumpDiscard;
    private static final int INITIAL_TRUMP_CARDS = 3;

    public GameEngine() {
        players = new ArrayList<>();
        deck = new Deck();
        lastMessage = "";
        roundCount = 0;
        waitingForCardSelection = false;
        selectionCards = new ArrayList<>();
        waitingForTrumpDiscard = false;
    }

    public void startGame() {
        for (Player p : players) {
            p.clearState();
        }
        players.clear();
        deck = new Deck();
        players.add(new Player("玩家", true));
        players.add(new Player("AI", false));
        dealInitialCards();
        dealInitialTrumpCards();
        gameActive = true;
        currentPlayerIndex = 0;
        roundCount = 1;
        lastMessage = "游戏开始！你的底牌已发，轮到你操作";
    }

    private void dealInitialCards() {
        for (Player player : players) {
            NumberCard card = deck.drawPointCard();
            if (card != null) {
                card.setFaceUp(false);
                player.addPointCard(card);
                player.setHoleCardIndex(0);
            }
        }
    }

    private void dealInitialTrumpCards() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_TRUMP_CARDS; i++) {
                SpecialCard card = deck.drawSpecialCard();
                if (card != null) {
                    player.addTrumpCard(card);
                }
            }
        }
    }

    public void playerHit() {
        if (!gameActive) return;
        Player current = getCurrentPlayer();
        if (current.hasStood()) return;
        if (current.isDrawBlocked()) {
            lastMessage = "你被封锁了抽点牌能力！";
            return;
        }
        NumberCard card = deck.drawPointCard();
        if (card != null) {
            card.setFaceUp(true);
            current.addPointCard(card);
            lastMessage = current.getName() + " 抽到点数牌: " + card.getPointValue();
            if (current.getTotalPoints() > current.getTargetScore()) {
                current.setBusted(true);
                lastMessage += " → 爆牌了！(点数: " + current.getTotalPoints() + ")";
            } else {
                lastMessage += " (当前点数: " + current.getTotalPoints() + ")";
            }
        }
    }

    public void playerStand() {
        if (!gameActive) return;
        Player current = getCurrentPlayer();
        current.setStood(true);
        lastMessage = current.getName() + " 选择停牌";
        nextTurn();
    }

    public void endTurn() {
        if (!gameActive) return;
        Player current = getCurrentPlayer();
        current.resetTurnFlags();
        nextTurn();
    }

    private void nextTurn() {
        int nextIndex = (currentPlayerIndex + 1) % players.size();
        if (allPlayersStood()) {
            endGame();
            return;
        }
        while (players.get(nextIndex).hasStood() || players.get(nextIndex).isBusted()) {
            nextIndex = (nextIndex + 1) % players.size();
            if (nextIndex == currentPlayerIndex) {
                endGame();
                return;
            }
        }
        currentPlayerIndex = nextIndex;
        roundCount++;
        Player next = getCurrentPlayer();
        next.resetTurnFlags();
        if (next.hasActiveEffect(SpecialCard.Effect.DESIRE)) {
            Player opponent = getOpponent(next);
            int desireAdd = opponent.getTrumpCardCount() / 2;
            next.addBet(desireAdd);
        }
        if (!next.isHuman()) {
            aiTurn(next);
        } else {
            lastMessage = "轮到你了！当前点数: " + next.getTotalPoints();
        }
    }

    private void aiTurn(Player ai) {
        if (ai.isBusted()) {
            nextTurn();
            return;
        }
        if (ai.isDrawBlocked()) {
            if (ai.getTotalPoints() < 12) {
                playerHitFor(ai);
                if (ai.isBusted()) {
                    nextTurn();
                    return;
                }
            }
            ai.setStood(true);
            lastMessage = "AI 选择停牌";
            nextTurn();
            return;
        }
        if (ai.getTotalPoints() < 14) {
            playerHitFor(ai);
            if (ai.isBusted()) {
                nextTurn();
                return;
            }
            if (ai.getTotalPoints() >= 17 && ai.getTotalPoints() <= ai.getTargetScore()) {
                ai.setStood(true);
                lastMessage = "AI 选择停牌 (点数: " + ai.getTotalPoints() + ")";
                nextTurn();
                return;
            }
            ai.setStood(true);
            lastMessage = "AI 结束回合 (点数: " + ai.getTotalPoints() + ")";
            nextTurn();
        } else if (ai.getTotalPoints() <= 17) {
            if (Math.random() < 0.3 && !ai.getTrumpCards().isEmpty()) {
                int idx = (int) (Math.random() * ai.getTrumpCards().size());
                playSpecialCardFor(ai, idx);
            }
            if (Math.random() < 0.5) {
                playerHitFor(ai);
                if (ai.isBusted()) {
                    nextTurn();
                    return;
                }
            }
            ai.setStood(true);
            lastMessage = "AI 选择停牌 (点数: " + ai.getTotalPoints() + ")";
            nextTurn();
        } else if (ai.getTotalPoints() <= ai.getTargetScore()) {
            ai.setStood(true);
            lastMessage = "AI 选择停牌 (点数: " + ai.getTotalPoints() + ")";
            nextTurn();
        } else {
            ai.setBusted(true);
            lastMessage = "AI 爆牌了！ (点数: " + ai.getTotalPoints() + ")";
            nextTurn();
        }
    }

    private void playerHitFor(Player player) {
        NumberCard card = deck.drawPointCard();
        if (card != null) {
            card.setFaceUp(true);
            player.addPointCard(card);
            lastMessage = player.getName() + " 抽到点数牌: " + card.getPointValue();
            if (player.getTotalPoints() > player.getTargetScore()) {
                player.setBusted(true);
                lastMessage += " → 爆牌了！";
            }
        }
    }

    public void playSpecialCard(int cardIndex) {
        if (!gameActive) return;
        Player current = getCurrentPlayer();
        if (current.isTrumpBlocked()) {
            lastMessage = "你被封锁了使用王牌的能力！";
            return;
        }
        if (cardIndex < 0 || cardIndex >= current.getTrumpCards().size()) return;

        if (current.isInvincibleActive() && !current.isFirstTrumpPlayed()) {
            current.setFirstTrumpPlayed(true);
            SpecialCard nullified = current.removeTrumpCard(cardIndex);
            deck.returnSpecialCard(nullified);
            lastMessage = "你的王牌被【无懈可击】抵消了！" + nullified.getName() + " 无效";
            return;
        }
        current.setFirstTrumpPlayed(true);

        SpecialCard card = current.getTrumpCard(cardIndex);
        if (card == null) return;
        current.removeTrumpCard(cardIndex);
        executeSpecialEffect(current, card);
        deck.returnSpecialCard(card);

        if (current.hasActiveEffect(SpecialCard.Effect.HARVEST)) {
            SpecialCard newCard = deck.drawSpecialCard();
            if (newCard != null) {
                current.addTrumpCard(newCard);
            }
        }
    }

    private void playSpecialCardFor(Player player, int cardIndex) {
        if (cardIndex < 0 || cardIndex >= player.getTrumpCards().size()) return;
        SpecialCard card = player.getTrumpCard(cardIndex);
        if (card == null) return;
        player.removeTrumpCard(cardIndex);
        executeSpecialEffect(player, card);
        deck.returnSpecialCard(card);
    }

    private void executeSpecialEffect(Player player, SpecialCard card) {
        Player opponent = getOpponent(player);
        SpecialCard.Effect effect = card.getEffect();
        lastMessage = player.getName() + " 使用了【" + card.getName() + "】";

        switch (effect) {
            case DRAW_MATCH:
                effectDrawMatch(player, card);
                break;
            case REMOVE:
                effectRemove(player, opponent);
                break;
            case ADD_TWO:
                effectAddTwo(player, opponent);
                break;
            case RETURN_OPPONENT:
                effectReturnOpponent(player, opponent);
                break;
            case SWAP:
                effectSwap(player, opponent);
                break;
            case PERFECT_DRAW:
                effectPerfectDraw(player, opponent);
                break;
            case MAGIC_DRAW:
                effectMagicDraw(player, opponent);
                break;
            case SHIELD:
                effectShield(player);
                break;
            case CURSE:
                effectCurse(player, opponent);
                break;
            case TWENTY_FOUR_RULE:
                effectChangeTarget(player, 24);
                break;
            case TWENTY_SEVEN_RULE:
                effectChangeTarget(player, 27);
                break;
            case ALL_OR_NOTHING:
                effectAllOrNothing(player, opponent);
                break;
            case RETURN_SELF:
                effectReturnSelf(player);
                break;
            case LOVE_ENEMY:
                effectLoveEnemy(player, opponent);
                break;
            case SPECIAL_TRANSFORM:
                effectSpecialTransform(player);
                break;
            case HARVEST:
                effectHarvest(player);
                break;
            case EVERYONE_HAPPY:
                effectEveryoneHappy();
                break;
            case ADD_ONE:
                effectAddOne(player, opponent);
                break;
            case DESTROY:
                effectDestroy(player, opponent);
                break;
            case INVINCIBLE:
                effectInvincible(player);
                break;
            case ARROW_BARRAGE:
                effectArrowBarrage();
                break;
            case SOUTHERN_INVASION:
                effectSouthernInvasion();
                break;
            case DESIRE:
                effectDesire(player, opponent);
                break;
        }
    }

    private void effectDrawMatch(Player player, SpecialCard card) {
        int targetValue = card.getDrawValue();
        List<NumberCard> drawn = deck.drawMultiplePointCards(5);
        NumberCard matched = null;
        for (NumberCard nc : drawn) {
            if (nc.getPointValue() == targetValue && matched == null) {
                matched = nc;
            }
        }
        if (matched != null) {
            matched.setFaceUp(true);
            player.addPointCard(matched);
            drawn.remove(matched);
            lastMessage += " → 从5张牌中找到点数" + targetValue + "的牌！加入点牌区";
        } else {
            lastMessage += " → 5张牌中没有点数" + targetValue + "的牌，无效";
        }
        deck.returnMultiplePointCards(drawn);
    }

    private void effectRemove(Player player, Player opponent) {
        if (!opponent.getActiveEffects().isEmpty()) {
            SpecialCard removed = opponent.getActiveEffects().remove(0);
            lastMessage += " → 解除了对手的【" + removed.getName() + "】效果";
            deck.returnSpecialCard(removed);
        } else {
            lastMessage += " → 对手没有可解除的效果";
        }
    }

    private void effectAddTwo(Player player, Player opponent) {
        opponent.addBet(2);
        NumberCard returned = opponent.removeLastPointCard();
        if (returned != null) {
            deck.returnPointCard(returned);
            lastMessage += " → 对手赌注+2，退回最后一张明牌(点数" + returned.getPointValue() + ")";
        } else {
            lastMessage += " → 对手赌注+2，但对手没有明牌可退";
        }
    }

    private void effectReturnOpponent(Player player, Player opponent) {
        NumberCard returned = opponent.removeLastPointCard();
        if (returned != null) {
            deck.returnPointCard(returned);
            lastMessage += " → 退回对手最后一张明牌(点数" + returned.getPointValue() + ")";
        } else {
            lastMessage += " → 对手没有明牌可退";
        }
    }

    private void effectSwap(Player player, Player opponent) {
        NumberCard playerCard = player.removeLastPointCard();
        NumberCard opponentCard = opponent.removeLastPointCard();
        if (playerCard != null && opponentCard != null) {
            opponentCard.setFaceUp(true);
            playerCard.setFaceUp(true);
            player.addPointCard(opponentCard);
            opponent.addPointCard(playerCard);
            lastMessage += " → 交换了双方最后一张明牌！";
        } else {
            if (playerCard != null) player.addPointCard(playerCard);
            if (opponentCard != null) opponent.addPointCard(opponentCard);
            lastMessage += " → 交换失败，某方没有明牌";
        }
    }

    private void effectPerfectDraw(Player player, Player opponent) {
        List<NumberCard> drawn = deck.drawMultiplePointCards(5);
        if (!drawn.isEmpty()) {
            NumberCard best = drawn.get(0);
            for (NumberCard nc : drawn) {
                int newTotal = player.getTotalPoints() + nc.getPointValue();
                int bestTotal = player.getTotalPoints() + best.getPointValue();
                if (newTotal <= player.getTargetScore() && nc.getPointValue() > best.getPointValue()) {
                    best = nc;
                }
            }
            best.setFaceUp(true);
            player.addPointCard(best);
            drawn.remove(best);
            lastMessage += " → 完美抽牌！选择点数" + best.getPointValue() + "的牌";
        }
        deck.returnMultiplePointCards(drawn);
        opponent.addBet(1);
        lastMessage += "，对手赌注+1";
    }

    private void effectMagicDraw(Player player, Player opponent) {
        List<SpecialCard> drawn = deck.drawMultipleSpecialCards(3);
        for (SpecialCard sc : drawn) {
            player.addTrumpCard(sc);
        }
        player.addActiveEffect(new SpecialCard("魔抽", SpecialCard.Effect.MAGIC_DRAW, "🃏", "小王"));
        lastMessage += " → 抽了" + drawn.size() + "张王牌，在场时对手赌注+1";
        opponent.addBet(1);
    }

    private void effectShield(Player player) {
        player.addShield();
        player.addActiveEffect(new SpecialCard("护盾+", SpecialCard.Effect.SHIELD, "♠", "A"));
        lastMessage += " → 护盾激活！减少1点赌注伤害";
    }

    private void effectCurse(Player player, Player opponent) {
        SpecialCard discarded = player.removeRandomTrumpCard();
        if (discarded != null) {
            deck.returnSpecialCard(discarded);
            lastMessage += " → 弃掉1张王牌";
        }
        List<NumberCard> drawn = deck.drawMultiplePointCards(5);
        if (!drawn.isEmpty()) {
            NumberCard worst = drawn.get(0);
            for (NumberCard nc : drawn) {
                if (nc.getPointValue() > worst.getPointValue()) {
                    worst = nc;
                }
            }
            worst.setFaceUp(true);
            opponent.addPointCard(worst);
            drawn.remove(worst);
            lastMessage += "，对手被迫抽最大点数牌: " + worst.getPointValue();
        }
        deck.returnMultiplePointCards(drawn);
    }

    private void effectChangeTarget(Player player, int newTarget) {
        player.setTargetScore(newTarget);
        lastMessage += " → 目标改为" + newTarget + "点！";
    }

    private void effectAllOrNothing(Player player, Player opponent) {
        player.addBet(player.getBet());
        opponent.addBet(opponent.getBet());
        opponent.setDrawBlocked(true);
        lastMessage += " → 双方赌注翻倍！对手被封锁抽点牌能力";
    }

    private void effectReturnSelf(Player player) {
        NumberCard returned = player.removeLastPointCard();
        if (returned != null) {
            deck.returnPointCard(returned);
            lastMessage += " → 退回自己最后一张明牌(点数" + returned.getPointValue() + ")";
        } else {
            lastMessage += " → 没有明牌可退";
        }
    }

    private void effectLoveEnemy(Player player, Player opponent) {
        List<NumberCard> drawn = deck.drawMultiplePointCards(5);
        if (!drawn.isEmpty()) {
            NumberCard bestForOpponent = drawn.get(0);
            for (NumberCard nc : drawn) {
                int oppTotal = opponent.getTotalPoints() + nc.getPointValue();
                int bestTotal = opponent.getTotalPoints() + bestForOpponent.getPointValue();
                if (oppTotal > bestTotal && oppTotal <= opponent.getTargetScore()) {
                    bestForOpponent = nc;
                } else if (oppTotal > opponent.getTargetScore() && bestForOpponent.getPointValue() < nc.getPointValue()) {
                    bestForOpponent = nc;
                }
            }
            bestForOpponent.setFaceUp(true);
            opponent.addPointCard(bestForOpponent);
            drawn.remove(bestForOpponent);
            lastMessage += " → 给对手最有利的牌: 点数" + bestForOpponent.getPointValue();
        }
        deck.returnMultiplePointCards(drawn);
    }

    private void effectSpecialTransform(Player player) {
        if (player.getTrumpCardCount() >= 2) {
            SpecialCard c1 = player.removeTrumpCard(player.getTrumpCardCount() - 1);
            SpecialCard c2 = player.removeTrumpCard(player.getTrumpCardCount() - 1);
            deck.returnSpecialCard(c1);
            deck.returnSpecialCard(c2);
            List<SpecialCard> newCards = deck.drawMultipleSpecialCards(3);
            for (SpecialCard sc : newCards) {
                player.addTrumpCard(sc);
            }
            lastMessage += " → 弃2张王牌，抽到" + newCards.size() + "张新王牌";
        } else {
            lastMessage += " → 王牌不足2张，无法变换";
        }
    }

    private void effectHarvest(Player player) {
        player.addActiveEffect(new SpecialCard("收割", SpecialCard.Effect.HARVEST, "♥", "9"));
        lastMessage += " → 收割激活！每使用1张王牌抽1张新王牌";
    }

    private void effectEveryoneHappy() {
        for (Player p : players) {
            SpecialCard card = deck.drawSpecialCard();
            if (card != null) {
                p.addTrumpCard(card);
            }
        }
        lastMessage += " → 双方各抽1张新王牌";
    }

    private void effectAddOne(Player player, Player opponent) {
        SpecialCard card = deck.drawSpecialCard();
        if (card != null) {
            player.addTrumpCard(card);
        }
        player.addAddOne();
        player.addActiveEffect(new SpecialCard("加一", SpecialCard.Effect.ADD_ONE, "♥", "7"));
        lastMessage += " → 抽1张王牌，对手赌注永久+1";
    }

    private void effectDestroy(Player player, Player opponent) {
        int removed = opponent.removeHalfTrumpCards();
        opponent.setTrumpBlocked(true);
        lastMessage += " → 摧毁对手" + removed + "张王牌，封锁1回合";
    }

    private void effectInvincible(Player player) {
        player.setInvincibleActive(true);
        lastMessage += " → 无懈可击！对手本回合第一张王牌将无效";
    }

    private void effectArrowBarrage() {
        for (Player p : players) {
            p.addBet(1);
        }
        lastMessage += " → 万箭齐发！所有玩家赌注+1";
    }

    private void effectSouthernInvasion() {
        for (Player p : players) {
            if (!p.getPointCards().isEmpty()) {
                NumberCard last = p.removeLastPointCard();
                if (last != null) {
                    deck.returnPointCard(last);
                }
            }
        }
        lastMessage += " → 南蛮入侵！所有玩家打出最后一张明牌";
    }

    private void effectDesire(Player player, Player opponent) {
        player.addDesire();
        player.addActiveEffect(new SpecialCard("欲望", SpecialCard.Effect.DESIRE, "♥", "6"));
        int desireAdd = opponent.getTrumpCardCount() / 2;
        opponent.addBet(desireAdd);
        lastMessage += " → 欲望！对手赌注增加" + desireAdd;
    }

    private boolean allPlayersStood() {
        for (Player p : players) {
            if (!p.hasStood() && !p.isBusted()) return false;
        }
        return true;
    }

    private void endGame() {
        gameActive = false;
        Player winner = determineWinner();
        if (winner != null) {
            lastMessage = "游戏结束！" + winner.getName() + " 获胜！";
        } else {
            lastMessage = "游戏结束！平局！";
        }
    }

    private Player determineWinner() {
        Player best = null;
        int bestDiff = Integer.MAX_VALUE;
        boolean allBusted = true;

        for (Player p : players) {
            if (!p.isBusted()) {
                allBusted = false;
            }
        }

        for (Player p : players) {
            int total = p.getTotalPoints();
            int target = p.getTargetScore();
            if (allBusted) {
                int diff = Math.abs(total - target);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    best = p;
                }
            } else {
                if (total <= target) {
                    int diff = target - total;
                    if (diff < bestDiff) {
                        bestDiff = diff;
                        best = p;
                    }
                }
            }
        }
        return best;
    }

    public Player getOpponent(Player player) {
        for (Player p : players) {
            if (p != player) return p;
        }
        return null;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public boolean isWaitingForCardSelection() {
        return waitingForCardSelection;
    }

    public List<NumberCard> getSelectionCards() {
        return selectionCards;
    }

    public void selectCardFromSelection(int index) {
        if (!waitingForCardSelection || index < 0 || index >= selectionCards.size()) return;
        NumberCard selected = selectionCards.remove(index);
        selected.setFaceUp(true);
        getCurrentPlayer().addPointCard(selected);
        deck.returnMultiplePointCards(selectionCards);
        selectionCards.clear();
        waitingForCardSelection = false;
        lastMessage = "选择了点数" + selected.getPointValue() + "的牌";
    }

    public void skipCardSelection() {
        if (!waitingForCardSelection) return;
        deck.returnMultiplePointCards(selectionCards);
        selectionCards.clear();
        waitingForCardSelection = false;
        lastMessage = "跳过了选牌";
    }

    public boolean isWaitingForTrumpDiscard() {
        return waitingForTrumpDiscard;
    }

    public void confirmTrumpDiscard(int card1Index, int card2Index) {
        if (!waitingForTrumpDiscard) return;
        Player current = getCurrentPlayer();
        if (card1Index == card2Index) return;
        int high = Math.max(card1Index, card2Index);
        int low = Math.min(card1Index, card2Index);
        SpecialCard c1 = current.removeTrumpCard(high);
        SpecialCard c2 = current.removeTrumpCard(low);
        if (c1 != null) deck.returnSpecialCard(c1);
        if (c2 != null) deck.returnSpecialCard(c2);
        List<SpecialCard> newCards = deck.drawMultipleSpecialCards(3);
        for (SpecialCard sc : newCards) {
            current.addTrumpCard(sc);
        }
        waitingForTrumpDiscard = false;
        lastMessage = "王牌变换完成！抽到" + newCards.size() + "张新王牌";
    }

    public Player getWinner() {
        if (gameActive) return null;
        return determineWinner();
    }
}
