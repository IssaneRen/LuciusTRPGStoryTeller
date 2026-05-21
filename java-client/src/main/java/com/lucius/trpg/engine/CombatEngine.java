package com.lucius.trpg.engine;

import com.lucius.trpg.model.Character;
import com.lucius.trpg.model.DiceRoll;
import com.lucius.trpg.model.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombatEngine {
    private static final int MAX_ROUNDS = 100;
    private CombatOptions options;

    public CombatEngine() {
        this.options = new CombatOptions();
    }

    public CombatEngine(CombatOptions options) {
        this.options = options;
    }

    public void setOptions(CombatOptions options) {
        this.options = options;
    }

    public CombatResult simulate(List<Character> pcs, List<Character> enemies) {
        CombatResult result = new CombatResult();
        result.setTotalPCs(pcs.size());

        List<Character> allCombatants = new ArrayList<>();
        allCombatants.addAll(pcs);
        allCombatants.addAll(enemies);
        allCombatants.sort(Comparator.comparingInt(Character::getDex).reversed());

        result.addLog("=== 战斗开始 ===");
        result.addLog("调查员: " + pcs.stream().map(Character::getName).collect(Collectors.joining(", ")));
        result.addLog("敌人: " + enemies.stream().map(Character::getName).collect(Collectors.joining(", ")));
        result.addLog("启用规则: " + getEnabledRulesDisplay());
        result.addLog("");

        int round = 0;
        boolean[] hasAimed = new boolean[allCombatants.size()];

        // 突袭回合：PC方先行动一轮，敌人无法反应
        if (options.isSurpriseRound()) {
            round++;
            result.addLog("--- 突袭回合 ---");
            for (int i = 0; i < allCombatants.size(); i++) {
                Character actor = allCombatants.get(i);
                if (!actor.isAlive() || !pcs.contains(actor)) continue;
                Character target = selectTarget(actor, pcs, enemies);
                if (target == null) break;
                performAttack(actor, target, true, false, result);
            }
            result.addLog("");
        }

        while (round < MAX_ROUNDS) {
            round++;
            result.addLog("--- 第" + round + "回合 ---");

            // 统计每方存活数（用于以多打少判定）
            int pcAliveCount = (int) pcs.stream().filter(Character::isAlive).count();
            int enemyAliveCount = (int) enemies.stream().filter(Character::isAlive).count();
            int[] dodgeUsed = new int[allCombatants.size()];

            for (int i = 0; i < allCombatants.size(); i++) {
                Character actor = allCombatants.get(i);
                if (!actor.isAlive()) continue;

                Character target = selectTarget(actor, pcs, enemies);
                if (target == null) break;

                boolean actorIsPC = pcs.contains(actor);

                // 瞄准规则：如果上回合瞄准了，本回合获得奖励
                boolean aimBonus = hasAimed[i];
                hasAimed[i] = false;

                // AI决策：是否本回合瞄准（PC有枪且技能<50时考虑瞄准）
                if (options.isAiming() && actorIsPC && shouldAim(actor)) {
                    hasAimed[i] = true;
                    result.addLog(actor.getName() + " 选择瞄准，放弃本回合攻击");
                    continue;
                }

                // 以多打少判定
                boolean outnumbered = false;
                if (options.isOutnumberedBonus()) {
                    int targetIdx = allCombatants.indexOf(target);
                    dodgeUsed[targetIdx]++;
                    outnumbered = dodgeUsed[targetIdx] > 1;
                }

                performAttack(actor, target, actorIsPC, aimBonus, outnumbered, result);
            }

            result.addLog("");

            boolean pcAlive = pcs.stream().anyMatch(Character::isAlive);
            boolean enemyAlive = enemies.stream().anyMatch(Character::isAlive);

            if (!pcAlive) {
                result.setWinner("enemy");
                result.addLog("=== 调查员全灭，战斗结束 ===");
                break;
            }
            if (!enemyAlive) {
                result.setWinner("pc");
                result.addLog("=== 敌人全灭，调查员获胜 ===");
                break;
            }
        }

        result.setRounds(round);
        result.setPcSurvivors((int) pcs.stream().filter(Character::isAlive).count());
        return result;
    }

    private void performAttack(Character attacker, Character defender, boolean attackerIsPC, boolean aimBonus, CombatResult result) {
        performAttack(attacker, defender, attackerIsPC, aimBonus, false, result);
    }

    private void performAttack(Character attacker, Character defender, boolean attackerIsPC, boolean aimBonus, boolean outnumbered, CombatResult result) {
        Weapon weapon = selectWeapon(attacker, attackerIsPC);
        if (weapon == null) {
            result.addLog(attacker.getName() + " 没有可用武器");
            return;
        }

        int skillValue = attacker.getSkill(weapon.getSkillName());

        // 瞄准奖励骰：取两次骰中较低值
        int attackRoll;
        if (aimBonus) {
            int r1 = DiceRoll.rollD100();
            int r2 = DiceRoll.rollD100();
            attackRoll = Math.min(r1, r2);
            result.addLog(String.format("%s [瞄准奖励] 骰了 %d 和 %d，取 %d", attacker.getName(), r1, r2, attackRoll));
        } else if (outnumbered) {
            int r1 = DiceRoll.rollD100();
            int r2 = DiceRoll.rollD100();
            attackRoll = Math.min(r1, r2);
            result.addLog(String.format("%s [以多打少] 骰了 %d 和 %d，取 %d", attacker.getName(), r1, r2, attackRoll));
        } else {
            attackRoll = DiceRoll.rollD100();
        }

        DiceRoll.CheckResult attackResult = DiceRoll.checkResult(attackRoll, skillValue);

        result.addLog(String.format("%s 使用 %s 攻击 %s (技能%d, 骰子%d, %s)",
                attacker.getName(), weapon.getName(), defender.getName(),
                skillValue, attackRoll, DiceRoll.getResultDescription(attackResult)));

        if (attackResult == DiceRoll.CheckResult.FAIL || attackResult == DiceRoll.CheckResult.FUMBLE) {
            result.addLog("  → 攻击未命中");

            // 消耗幸运（PC尝试将失败变为成功）
            if (options.isSpendingLuck() && attackerIsPC) {
                int needed = attackRoll - skillValue;
                int luck = attacker.getLuck();
                if (needed <= luck && needed > 0) {
                    attacker.spendLuck(needed);
                    result.addLog(String.format("  [消耗幸运] 花费 %d 点幸运将骰点从 %d 降至 %d，变为成功！(剩余幸运: %d)",
                            needed, attackRoll, skillValue, attacker.getLuck()));
                    attackResult = DiceRoll.CheckResult.SUCCESS;
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        // 防御判定（仅近战可闪避）
        if (weapon.isMelee()) {
            // 扑倒寻求掩护不适用于近战
            int dodgeValue = defender.getDodge();
            int dodgeRoll = DiceRoll.rollD100();
            DiceRoll.CheckResult dodgeResult = DiceRoll.checkResult(dodgeRoll, dodgeValue);

            result.addLog(String.format("  %s 闪避 (技能%d, 骰子%d, %s)",
                    defender.getName(), dodgeValue, dodgeRoll,
                    DiceRoll.getResultDescription(dodgeResult)));

            if (dodgeResult != DiceRoll.CheckResult.FAIL && dodgeResult != DiceRoll.CheckResult.FUMBLE) {
                result.addLog("  → 闪避成功，攻击未命中");
                return;
            }
        } else {
            // 枪械：Diving for Cover 规则
            if (options.isDivingForCover() && defender.getDodge() > 0) {
                int dodgeRoll = DiceRoll.rollD100();
                DiceRoll.CheckResult dodgeResult = DiceRoll.checkResult(dodgeRoll, defender.getDodge());
                if (dodgeResult != DiceRoll.CheckResult.FAIL && dodgeResult != DiceRoll.CheckResult.FUMBLE) {
                    result.addLog(String.format("  %s 扑倒寻求掩护成功！(骰%d vs 闪避%d)", defender.getName(), dodgeRoll, defender.getDodge()));
                    return;
                }
            }
            result.addLog("  枪械攻击，无法闪避");
            weapon.consumeAmmo();
        }

        // 计算伤害
        int baseDamage = DiceRoll.rollDamage(weapon.getDamage());

        // 极难成功额外伤害
        if (options.isExtremeDamage() && attackResult == DiceRoll.CheckResult.EXTREME) {
            int extraDamage = DiceRoll.rollDamage(weapon.getDamage());
            baseDamage += extraDamage;
            result.addLog(String.format("  [极难成功] 额外造成 %d 点伤害！", extraDamage));
        }

        // 添加DB（仅近战）
        if (weapon.isMelee()) {
            String db = attacker.getDb();
            if (!db.equals("0") && !db.equals("-1") && !db.equals("-2")) {
                baseDamage += DiceRoll.rollDamage(db);
            } else if (db.equals("-1")) {
                baseDamage -= 1;
            } else if (db.equals("-2")) {
                baseDamage -= 2;
            }
        }

        int finalDamage = Math.max(0, baseDamage - defender.getArmor());
        result.addLog(String.format("  → 命中！造成 %d 伤害 (基础%d - 护甲%d)", finalDamage, baseDamage, defender.getArmor()));

        defender.takeDamage(finalDamage);
        result.addLog(String.format("  %s HP: %d/%d", defender.getName(), defender.getHp(), defender.getMaxHp()));

        // 重伤检定
        if (options.isMajorWound() && defender.isAlive() && defender.isMajorWound(finalDamage)) {
            result.addLog("  重伤！需要CON检定...");
            if (!defender.majorWoundCheck()) {
                defender.setAlive(false);
                result.addLog("  → 检定失败，" + defender.getName() + " 昏迷");
            } else {
                result.addLog("  → 检定成功，坚持战斗");
            }
        }

        if (!defender.isAlive()) {
            result.addLog("  " + defender.getName() + " 已死亡");
        }
    }

    private boolean shouldAim(Character actor) {
        for (Weapon w : actor.getWeapons()) {
            if (!w.isMelee() && w.hasAmmo()) {
                int skill = actor.getSkill(w.getSkillName());
                return skill < 50 && DiceRoll.rollD100() > 70;
            }
        }
        return false;
    }

    private Character selectTarget(Character actor, List<Character> pcs, List<Character> enemies) {
        boolean actorIsPC = pcs.contains(actor);
        List<Character> targets = actorIsPC ? enemies : pcs;
        return targets.stream().filter(Character::isAlive).findFirst().orElse(null);
    }

    private Weapon selectWeapon(Character character, boolean isPC) {
        List<Weapon> weapons = character.getWeapons();
        if (weapons.isEmpty()) return null;
        if (isPC) {
            for (Weapon weapon : weapons) {
                if (!weapon.isMelee() && weapon.hasAmmo()) return weapon;
            }
        }
        for (Weapon weapon : weapons) {
            if (weapon.isMelee()) return weapon;
        }
        return weapons.get(0);
    }

    private String getEnabledRulesDisplay() {
        List<String> enabled = new ArrayList<>();
        if (options.isSpendingLuck()) enabled.add("消耗幸运");
        if (options.isAiming()) enabled.add("瞄准");
        if (options.isSurpriseRound()) enabled.add("突袭");
        if (options.isFullAuto()) enabled.add("全自动射击");
        if (options.isOutnumberedBonus()) enabled.add("以多打少");
        if (options.isDivingForCover()) enabled.add("扑倒掩护");
        if (options.isMajorWound()) enabled.add("重伤检定");
        if (options.isExtremeDamage()) enabled.add("极难额外伤害");
        return enabled.isEmpty() ? "无" : String.join(", ", enabled);
    }
}
