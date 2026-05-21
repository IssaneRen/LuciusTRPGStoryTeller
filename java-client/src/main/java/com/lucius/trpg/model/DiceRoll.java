package com.lucius.trpg.model;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 骰子工具类
 */
public class DiceRoll {
    private static final Random random = new Random();

    /**
     * 掷骰子结果枚举
     */
    public enum CheckResult {
        CRITICAL,   // 大成功
        EXTREME,    // 极难成功
        HARD,       // 困难成功
        SUCCESS,    // 成功
        FAIL,       // 失败
        FUMBLE      // 大失败
    }

    /**
     * 掷一个指定面数的骰子
     */
    public static int roll(int sides) {
        return random.nextInt(sides) + 1;
    }

    /**
     * 掷D100
     */
    public static int rollD100() {
        return random.nextInt(100) + 1;
    }

    /**
     * 解析并掷伤害骰
     * 支持格式: "1D10+2", "2D6+4", "4D6", "1D4-1"
     */
    public static int rollDamage(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            return 0;
        }

        expr = expr.toUpperCase().replaceAll("\\s+", "");

        // 匹配格式: XDY+Z 或 XDY-Z 或 XDY 或 +Z 或 -Z
        Pattern pattern = Pattern.compile("(\\d+)D(\\d+)([+\\-]\\d+)?");
        Matcher matcher = pattern.matcher(expr);

        int total = 0;

        if (matcher.find()) {
            int count = Integer.parseInt(matcher.group(1));
            int sides = Integer.parseInt(matcher.group(2));

            for (int i = 0; i < count; i++) {
                total += roll(sides);
            }

            if (matcher.group(3) != null) {
                total += Integer.parseInt(matcher.group(3));
            }
        } else {
            // 尝试解析纯数字修正值
            try {
                total = Integer.parseInt(expr);
            } catch (NumberFormatException e) {
                total = 0;
            }
        }

        return Math.max(0, total);
    }

    /**
     * 检查骰子结果（CoC 7版规则）
     * @param roll 骰子点数
     * @param skill 技能值
     * @return 检定结果
     */
    public static CheckResult checkResult(int roll, int skill) {
        // 大成功：01
        if (roll == 1) {
            return CheckResult.CRITICAL;
        }

        // 大失败：技能≤50时只有100，技能>50时96-100
        if (skill <= 50) {
            if (roll == 100) {
                return CheckResult.FUMBLE;
            }
        } else {
            if (roll >= 96) {
                return CheckResult.FUMBLE;
            }
        }

        // 极难成功：≤技能/5
        if (roll <= skill / 5) {
            return CheckResult.EXTREME;
        }

        // 困难成功：≤技能/2
        if (roll <= skill / 2) {
            return CheckResult.HARD;
        }

        // 普通成功：≤技能
        if (roll <= skill) {
            return CheckResult.SUCCESS;
        }

        // 失败
        return CheckResult.FAIL;
    }

    /**
     * 获取检定结果的中文描述
     */
    public static String getResultDescription(CheckResult result) {
        switch (result) {
            case CRITICAL:
                return "大成功";
            case EXTREME:
                return "极难成功";
            case HARD:
                return "困难成功";
            case SUCCESS:
                return "成功";
            case FAIL:
                return "失败";
            case FUMBLE:
                return "大失败";
            default:
                return "未知";
        }
    }
}
