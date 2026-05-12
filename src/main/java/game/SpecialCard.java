package game;

public class SpecialCard extends Card {
    public enum Effect {
        DRAW_MATCH("抽2~抽7", "从牌库选5张牌→若有对应点数牌则选1张加入己方点牌区;否则无效退回王牌组"),
        REMOVE("解除牌", "拆除对手单张挑战牌(护盾/加注牌等)"),
        ADD_TWO("加二+", "提升对手赌注+退回其上一张明牌"),
        RETURN_OPPONENT("退牌(对方)", "将对手上一张明牌退回牌库"),
        SWAP("交换", "互换双方上一张明牌"),
        PERFECT_DRAW("完美抽牌+", "抽5张选最优牌;增加对手赌注"),
        MAGIC_DRAW("魔抽", "抽3张王牌;在场时对方赌注+1"),
        SHIELD("护盾+", "持续减少1点所受赌注伤害"),
        CURSE("诅咒", "随机弃己方1王牌;迫使对手抽5张选最大点数牌"),
        TWENTY_FOUR_RULE("24点规则", "胜利目标改为接近24点"),
        TWENTY_SEVEN_RULE("27点规则", "胜利目标改为接近27点"),
        ALL_OR_NOTHING("生死一搏", "双方赌注+100%;封锁对手抽点牌能力"),
        RETURN_SELF("退回(己方)", "将自己上一张明牌退回牌库"),
        LOVE_ENEMY("爱你的敌人", "从牌库抽5张选1张最有利牌加入对手点牌区"),
        SPECIAL_TRANSFORM("王牌变换+", "弃2张王牌→抽3张新王牌"),
        HARVEST("收割", "每使用1张王牌→抽1张新王牌(持续生效)"),
        EVERYONE_HAPPY("皆大欢喜", "双方各从王牌组抽1张新王牌"),
        ADD_ONE("加一", "抽1张王牌;在场时对方赌注永久+1"),
        DESTROY("摧毁", "移除对手半数王牌(向上取整)+封锁其使用1回合"),
        INVINCIBLE("无懈可击", "使对手本回合打出的第一张王牌无效"),
        ARROW_BARRAGE("万箭齐发", "所有玩家赌注+1(包括自己)"),
        SOUTHERN_INVASION("南蛮入侵", "强制所有玩家打出最后一张明牌"),
        DESIRE("欲望", "对方每回合赌注+=其王牌数量/2(向下取整)");

        private final String displayName;
        private final String desc;

        Effect(String displayName, String desc) {
            this.displayName = displayName;
            this.desc = desc;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDesc() {
            return desc;
        }
    }

    private Effect effect;
    private int drawValue;

    public SpecialCard(String name, Effect effect, String suit, String rank) {
        super(name, suit, rank);
        this.effect = effect;
        this.description = effect.getDesc();
        this.drawValue = 0;
    }

    public Effect getEffect() {
        return effect;
    }

    public int getDrawValue() {
        return drawValue;
    }

    public void setDrawValue(int drawValue) {
        this.drawValue = drawValue;
    }

    @Override
    public String getType() {
        return "SPECIAL";
    }

    @Override
    public int getPointValue() {
        return 0;
    }

    @Override
    public String getDisplayText() {
        return name;
    }
}
