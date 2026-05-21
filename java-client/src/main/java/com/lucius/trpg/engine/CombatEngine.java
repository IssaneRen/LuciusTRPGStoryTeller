package com.lucius.trpg.engine;

import com.lucius.trpg.model.Character;
import com.lucius.trpg.model.DiceRoll;
import com.lucius.trpg.model.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 战斗引擎核心
 */
public class CombatEngine {
    private static final int MAX_ROUNDS = 100;

    /**
     * 模拟一场完整战斗
     */
    public CombatResult simulate(List<Character> pcs, List<Character> enemies) {
        CombatResult result = new CombatResult();
        result.setTotalPCs(pcs.size());

        // 所有参战者按DEX排序（先攻）
        List<Character> allCombatants = new ArrayList<>();
        allCombatants.addAll(pcs);
        allCombatants.addAll(enemies);
        allCombatants.sort(Comparator.comparingInt(Character::getDex).reversed());

        result.addLog("=== 战斗开始 ===");
        result.addLog("调查员: " + pcs.stream().map(Character::getName).collect(Collectors.joining(", ")));
        result.addLog("敌人: " + enemies.stream().map(Character::getName).collect(Collectors.joining(", ")));
        result.addLog("");

        int round = 0;

        while (round < MAX_ROUNDS) {
            round++;
            result.addLog("--- 第" + round + "回合 ---");

            // 每个角色行动
            for (Character actor : allCombatants) {
                if (!actor.isAlive()) {
                    continue;
                }

                // 选择目标
                Character target = selectTarget(actor, pcs, enemies);
                if (target == null) {
                    break;
                }

                // 执行攻击
                performAttack(actor, target, pcs.contains(actor), result);
            }

            result.addLog("");

            // 检查战斗是否结束
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

    /**
     * 选择攻击目标
     */
    private Character selectTarget(Character actor, List<Character> pcs, List<Character> enemies) {
        boolean actorIsPC = pcs.contains(actor);
        List<Character> targets = actorIsPC ? enemies : pcs;

        return targets.stream()
                .filter(Character::isAlive)
                .findFirst()
                .orElse(null);
    }

    /**
     * 执行攻击
     */
    private void performAttack(Character attacker, Character defender, boolean attackerIsPC, CombatResult result) {
        // 选择武器
        Weapon weapon = selectWeapon(attacker, attackerIsPC);
        if (weapon == null) {
            result.addLog(attacker.getName() + " 没有可用武器");
            return;
        }

        int skillValue = attacker.getSkill(weapon.getSkillName());
        int attackRoll = DiceRoll.rollD100();
        DiceRoll.CheckResult attackResult = DiceRoll.checkResult(attackRoll, skillValue);

        String attackMsg = String.format("%s 使用 %s 攻击 %s (技能%d, 骰子%d, %s)",
                attacker.getName(),
                weapon.getName(),
                defender.getName(),
                skillValue,
                attackRoll,
                DiceRoll.getResultDescription(attackResult));
        result.addLog(attackMsg);

        // 判断是否命中
        if (attackResult == DiceRoll.CheckResult.FAIL || attackResult == DiceRoll.CheckResult.FUMBLE) {
            result.addLog("  → 攻击未命中");
            return;
        }

        // 防御判定（仅近战可闪避）
        if (weapon.isMelee()) {
            int dodgeValue = defender.getDodge();
            int dodgeRoll = DiceRoll.rollD100();
            DiceRoll.CheckResult dodgeResult = DiceRoll.checkResult(dodgeRoll, dodgeValue);

            result.addLog(String.format("  %s 闪避 (技能%d, 骰子%d, %s)",
                    defender.getName(),
                    dodgeValue,
                    dodgeRoll,
                    DiceRoll.getResultDescription(dodgeResult)));

            if (dodgeResult != DiceRoll.CheckResult.FAIL && dodgeResult != DiceRoll.CheckResult.FUMBLE) {
                result.addLog("  → 闪避成功，攻击未命中");
                return;
            }
        } else {
            result.addLog("  枪械攻击，无法闪避");
            weapon.consumeAmmo();
        }

        // 计算伤害
        int baseDamage = DiceRoll.rollDamage(weapon.getDamage());

        // 添加DB（仅近战）
        if (weapon.isMelee()) {
            String db = attacker.getDb();
            if (!db.equals("0") && !db.equals("-1") && !db.equals("-2")) {
                int dbDamage = DiceRoll.rollDamage(db);
                baseDamage += dbDamage;
            } else if (db.equals("-1")) {
                baseDamage -= 1;
            } else if (db.equals("-2")) {
                baseDamage -= 2;
            }
        }

        int finalDamage = Math.max(0, baseDamage - defender.getArmor());

        result.addLog(String.format("  → 命中！造成 %d 伤害 (基础%d - 护甲%d)",
                finalDamage,
                baseDamage,
                defender.getArmor()));

        // 应用伤害
        defender.takeDamage(finalDamage);
        result.addLog(String.format("  %s HP: %d/%d",
                defender.getName(),
                defender.getHp(),
                defender.getMaxHp()));

        // 检查重伤
        if (defender.isAlive() && defender.isMajorWound(finalDamage)) {
            result.addLog("  重伤！需要CON检定...");
            if (!defender.majorWoundCheck()) {
                defender.setAlive(false);
                result.addLog("  → 检定失败，" + defender.getName() + " 昏迷");
            } else {
                result.addLog("  → 检定成功，坚持战斗");
            }
        }

        // 检查死亡
        if (!defender.isAlive()) {
            result.addLog("  " + defender.getName() + " 已死亡");
        }
    }

    /**
     * 为角色选择合适的武器
     */
    private Weapon selectWeapon(Character character, boolean isPC) {
        List<Weapon> weapons = character.getWeapons();
        if (weapons.isEmpty()) {
            return null;
        }

        if (isPC) {
            // PC优先使用远程武器（如果有弹药）
            for (Weapon weapon : weapons) {
                if (!weapon.isMelee() && weapon.hasAmmo()) {
                    return weapon;
                }
            }
        }

        // 否则使用近战武器
        for (Weapon weapon : weapons) {
            if (weapon.isMelee()) {
                return weapon;
            }
        }

        // 返回第一个可用武器
        return weapons.get(0);
    }
}
