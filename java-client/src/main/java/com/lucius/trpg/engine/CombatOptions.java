package com.lucius.trpg.engine;

public class CombatOptions {
    private boolean spendingLuck = false;
    private boolean aiming = false;
    private boolean surpriseRound = false;
    private boolean fullAuto = true;
    private boolean outnumberedBonus = true;
    private boolean divingForCover = false;
    private boolean majorWound = true;
    private boolean extremeDamage = false;

    public static final String[] NAMES = {
        "消耗幸运 (Spending Luck)",
        "瞄准 (Aiming)",
        "突袭回合 (Surprise Round)",
        "全自动射击 (Full Auto)",
        "以多打少 (Outnumbered Bonus)",
        "扑倒寻求掩护 (Diving for Cover)",
        "重伤检定 (Major Wound)",
        "极难成功额外伤害 (Extreme Damage)"
    };

    public static final String[] DESCRIPTIONS = {
        "PC可消耗幸运值降低骰点，每点幸运减1点骰点",
        "放弃本回合行动，下回合射击获得1个奖励骰",
        "伏击方获得完整行动回合，被伏击方首回合无法反应",
        "冲锋枪等连射武器可一次射击多发，每发独立判定",
        "防御者用完闪避后，后续攻击获得1个奖励骰",
        "射击目标可声明扑倒，成功则射手+1惩罚骰，但自身失去下次攻击",
        "单次伤害≥HP/2时需CON×5检定，失败则昏迷",
        "极难成功时钝器取最大伤害，穿刺武器额外加骰"
    };

    public boolean isSpendingLuck() { return spendingLuck; }
    public void setSpendingLuck(boolean v) { spendingLuck = v; }

    public boolean isAiming() { return aiming; }
    public void setAiming(boolean v) { aiming = v; }

    public boolean isSurpriseRound() { return surpriseRound; }
    public void setSurpriseRound(boolean v) { surpriseRound = v; }

    public boolean isFullAuto() { return fullAuto; }
    public void setFullAuto(boolean v) { fullAuto = v; }

    public boolean isOutnumberedBonus() { return outnumberedBonus; }
    public void setOutnumberedBonus(boolean v) { outnumberedBonus = v; }

    public boolean isDivingForCover() { return divingForCover; }
    public void setDivingForCover(boolean v) { divingForCover = v; }

    public boolean isMajorWound() { return majorWound; }
    public void setMajorWound(boolean v) { majorWound = v; }

    public boolean isExtremeDamage() { return extremeDamage; }
    public void setExtremeDamage(boolean v) { extremeDamage = v; }

    public boolean getByIndex(int i) {
        switch (i) {
            case 0: return spendingLuck;
            case 1: return aiming;
            case 2: return surpriseRound;
            case 3: return fullAuto;
            case 4: return outnumberedBonus;
            case 5: return divingForCover;
            case 6: return majorWound;
            case 7: return extremeDamage;
            default: return false;
        }
    }

    public void setByIndex(int i, boolean v) {
        switch (i) {
            case 0: spendingLuck = v; break;
            case 1: aiming = v; break;
            case 2: surpriseRound = v; break;
            case 3: fullAuto = v; break;
            case 4: outnumberedBonus = v; break;
            case 5: divingForCover = v; break;
            case 6: majorWound = v; break;
            case 7: extremeDamage = v; break;
        }
    }
}
