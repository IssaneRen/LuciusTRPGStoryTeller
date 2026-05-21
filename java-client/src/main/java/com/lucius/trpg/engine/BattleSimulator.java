package com.lucius.trpg.engine;

import com.lucius.trpg.model.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量战斗模拟器
 */
public class BattleSimulator {
    private CombatEngine combatEngine;

    public BattleSimulator() {
        this.combatEngine = new CombatEngine();
    }

    /**
     * 批量模拟战斗
     */
    public SimulationReport simulate(List<Character> pcTemplates, List<Character> enemyTemplates, int times) {
        SimulationReport report = new SimulationReport();
        report.setTotalRuns(times);

        int totalRounds = 0;
        int totalSurvivors = 0;
        int pcWins = 0;
        int enemyWins = 0;
        CombatResult lastResult = null;

        for (int i = 0; i < times; i++) {
            // 为每次模拟创建角色副本
            List<Character> pcs = cloneCharacters(pcTemplates);
            List<Character> enemies = cloneCharacters(enemyTemplates);

            // 运行战斗
            CombatResult result = combatEngine.simulate(pcs, enemies);
            lastResult = result;

            // 统计结果
            totalRounds += result.getRounds();
            totalSurvivors += result.getPcSurvivors();

            if ("pc".equals(result.getWinner())) {
                pcWins++;
            } else {
                enemyWins++;
            }
        }

        // 计算统计数据
        report.setPcWins(pcWins);
        report.setEnemyWins(enemyWins);
        report.setAvgRounds((double) totalRounds / times);
        report.setSurvivalRate((double) totalSurvivors / (times * pcTemplates.size()) * 100);
        report.calculateDifficultyRating();

        // 保存最后一次战斗日志
        if (lastResult != null) {
            report.setSampleLog(lastResult.getLogs());
        }

        return report;
    }

    /**
     * 克隆角色列表（深拷贝）
     */
    private List<Character> cloneCharacters(List<Character> templates) {
        List<Character> clones = new ArrayList<>();

        for (Character template : templates) {
            Character clone = new Character(
                    template.getName(),
                    template.getStr(),
                    template.getCon(),
                    template.getSiz(),
                    template.getDex(),
                    template.getIntelligence(),
                    template.getPow(),
                    template.getApp(),
                    template.getEdu()
            );

            // 复制技能
            for (String skillName : template.getSkills().keySet()) {
                clone.addSkill(skillName, template.getSkill(skillName));
            }

            // 复制武器
            for (com.lucius.trpg.model.Weapon weapon : template.getWeapons()) {
                clone.addWeapon(new com.lucius.trpg.model.Weapon(
                        weapon.getName(),
                        weapon.getSkillName(),
                        weapon.getDamage(),
                        weapon.getRange(),
                        weapon.getRateOfFire(),
                        weapon.getAmmo()
                ));
            }

            // 复制护甲
            clone.setArmor(template.getArmor());

            clones.add(clone);
        }

        return clones;
    }
}
