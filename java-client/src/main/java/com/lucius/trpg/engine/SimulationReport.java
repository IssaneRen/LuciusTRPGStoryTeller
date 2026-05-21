package com.lucius.trpg.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量模拟报告
 */
public class SimulationReport {
    private int totalRuns;
    private int pcWins;
    private int enemyWins;
    private double avgRounds;
    private double survivalRate;
    private int difficultyRating;
    private List<String> sampleLog;

    // 每次战斗的标签化日志: "团灭" / "全员存活" / "部分死亡"
    private List<TaggedLog> allLogs = new ArrayList<>();

    public SimulationReport() {
        this.sampleLog = new ArrayList<>();
    }

    public void addTaggedLog(String tag, List<String> log) {
        allLogs.add(new TaggedLog(tag, log));
    }

    public List<TaggedLog> getAllLogs() { return allLogs; }

    public List<TaggedLog> getLogsByTag(String tag) {
        List<TaggedLog> result = new ArrayList<>();
        for (TaggedLog tl : allLogs) {
            if (tl.tag.equals(tag)) result.add(tl);
        }
        return result;
    }

    public static class TaggedLog {
        public final String tag;
        public final List<String> lines;
        public TaggedLog(String tag, List<String> lines) {
            this.tag = tag;
            this.lines = lines;
        }
    }

    /**
     * 计算难度评级
     */
    public void calculateDifficultyRating() {
        double winRate = (double) pcWins / totalRuns;

        if (winRate >= 0.9) {
            difficultyRating = 1;
        } else if (winRate >= 0.7) {
            difficultyRating = 2;
        } else if (winRate >= 0.5) {
            difficultyRating = 3;
        } else if (winRate >= 0.3) {
            difficultyRating = 4;
        } else {
            difficultyRating = 5;
        }
    }

    /**
     * 获取难度描述
     */
    public String getDifficultyDescription() {
        switch (difficultyRating) {
            case 1:
                return "简单 ★";
            case 2:
                return "普通 ★★";
            case 3:
                return "困难 ★★★";
            case 4:
                return "极难 ★★★★";
            case 5:
                return "致命 ★★★★★";
            default:
                return "未知";
        }
    }

    // Getters and Setters
    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getPcWins() {
        return pcWins;
    }

    public void setPcWins(int pcWins) {
        this.pcWins = pcWins;
    }

    public int getEnemyWins() {
        return enemyWins;
    }

    public void setEnemyWins(int enemyWins) {
        this.enemyWins = enemyWins;
    }

    public double getAvgRounds() {
        return avgRounds;
    }

    public void setAvgRounds(double avgRounds) {
        this.avgRounds = avgRounds;
    }

    public double getSurvivalRate() {
        return survivalRate;
    }

    public void setSurvivalRate(double survivalRate) {
        this.survivalRate = survivalRate;
    }

    public int getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(int difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public List<String> getSampleLog() {
        return sampleLog;
    }

    public void setSampleLog(List<String> sampleLog) {
        this.sampleLog = sampleLog;
    }
}
