package com.lucius.trpg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoC 7版角色模型
 */
public class Character {
    private String name;

    // 基础属性
    private int str;
    private int con;
    private int siz;
    private int dex;
    private int intelligence;
    private int pow;
    private int app;
    private int edu;

    // 派生属性
    private int maxHp;
    private int hp;
    private int mp;
    private String db;
    private int dodge;

    // 技能和装备
    private Map<String, Integer> skills;
    private List<Weapon> weapons;
    private int armor;

    private int luck;

    // 状态
    private boolean alive;

    public Character(String name, int str, int con, int siz, int dex, int intelligence, int pow, int app, int edu) {
        this.name = name;
        this.str = str;
        this.con = con;
        this.siz = siz;
        this.dex = dex;
        this.intelligence = intelligence;
        this.pow = pow;
        this.app = app;
        this.edu = edu;

        // 计算派生属性
        this.maxHp = (con + siz) / 10;
        this.hp = maxHp;
        this.mp = pow / 5;
        this.db = calculateDamageBonus(str, siz);
        this.dodge = dex / 2;

        this.luck = pow;

        this.skills = new HashMap<>();
        this.weapons = new ArrayList<>();
        this.armor = 0;
        this.alive = true;
    }

    /**
     * 根据STR+SIZ计算伤害加值
     */
    private String calculateDamageBonus(int str, int siz) {
        int total = str + siz;
        if (total <= 64) return "-2";
        if (total <= 84) return "-1";
        if (total <= 124) return "0";
        if (total <= 164) return "+1D4";
        if (total <= 204) return "+1D6";
        if (total <= 284) return "+2D6";
        if (total <= 364) return "+3D6";
        if (total <= 444) return "+4D6";
        return "+5D6";
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    /**
     * 检查是否重伤（单次伤害>=HP上限一半）
     */
    public boolean isMajorWound(int damage) {
        return damage >= maxHp / 2;
    }

    /**
     * 重伤检定（CON×5）
     */
    public boolean majorWoundCheck() {
        int threshold = con * 5;
        int roll = DiceRoll.rollD100();
        return roll <= threshold;
    }

    public void addSkill(String skillName, int value) {
        skills.put(skillName, value);
    }

    public int getSkill(String skillName) {
        return skills.getOrDefault(skillName, 0);
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getStr() {
        return str;
    }

    public int getCon() {
        return con;
    }

    public int getSiz() {
        return siz;
    }

    public int getDex() {
        return dex;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getPow() {
        return pow;
    }

    public int getApp() {
        return app;
    }

    public int getEdu() {
        return edu;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public String getDb() {
        return db;
    }

    public int getDodge() {
        return dodge;
    }

    public Map<String, Integer> getSkills() {
        return skills;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getLuck() {
        return luck;
    }

    public void spendLuck(int amount) {
        luck = Math.max(0, luck - amount);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
