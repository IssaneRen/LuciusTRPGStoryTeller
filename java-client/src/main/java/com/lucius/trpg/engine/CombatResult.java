package com.lucius.trpg.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗结果
 */
public class CombatResult {
    private String winner;      // "pc" 或 "enemy"
    private int rounds;         // 回合数
    private List<String> logs;  // 战斗日志
    private int pcSurvivors;    // 存活PC数
    private int totalPCs;       // 总PC数

    public CombatResult() {
        this.logs = new ArrayList<>();
    }

    public void addLog(String log) {
        logs.add(log);
    }

    // Getters and Setters
    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public int getPcSurvivors() {
        return pcSurvivors;
    }

    public void setPcSurvivors(int pcSurvivors) {
        this.pcSurvivors = pcSurvivors;
    }

    public int getTotalPCs() {
        return totalPCs;
    }

    public void setTotalPCs(int totalPCs) {
        this.totalPCs = totalPCs;
    }
}
