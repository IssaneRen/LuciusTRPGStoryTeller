package com.lucius.trpg.model;

/**
 * 武器模型
 */
public class Weapon {
    private String name;
    private String skillName;
    private String damage;
    private int range;
    private int rateOfFire;
    private int ammo;
    private int currentAmmo;

    public Weapon(String name, String skillName, String damage, int range, int rateOfFire, int ammo) {
        this.name = name;
        this.skillName = skillName;
        this.damage = damage;
        this.range = range;
        this.rateOfFire = rateOfFire;
        this.ammo = ammo;
        this.currentAmmo = ammo;
    }

    /**
     * 判断是否为近战武器
     */
    public boolean isMelee() {
        return range == 0;
    }

    /**
     * 是否有弹药
     */
    public boolean hasAmmo() {
        return ammo == -1 || currentAmmo > 0;
    }

    /**
     * 消耗弹药
     */
    public void consumeAmmo() {
        if (ammo != -1) {
            currentAmmo--;
        }
    }

    /**
     * 装填弹药
     */
    public void reload() {
        currentAmmo = ammo;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public int getAmmo() {
        return ammo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }
}
