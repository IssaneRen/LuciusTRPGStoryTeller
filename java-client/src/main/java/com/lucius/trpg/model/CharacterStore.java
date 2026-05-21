package com.lucius.trpg.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class CharacterStore {
    private static final String DATA_DIR = "data/characters";
    private static final String PC_FILE = DATA_DIR + "/investigators.json";
    private static final String ENEMY_FILE = DATA_DIR + "/enemies.json";
    private static final String WORKSPACE_PC = DATA_DIR + "/workspace_pcs.json";
    private static final String WORKSPACE_ENEMY = DATA_DIR + "/workspace_enemies.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void save(List<Character> pcs, List<Character> enemies) {
        ensureDir();
        writeFile(WORKSPACE_PC, pcs);
        writeFile(WORKSPACE_ENEMY, enemies);
    }

    public static List<Character> loadPCs() {
        List<Character> workspace = readFile(WORKSPACE_PC);
        return workspace.isEmpty() ? readFile(PC_FILE) : workspace;
    }

    public static List<Character> loadEnemies() {
        List<Character> workspace = readFile(WORKSPACE_ENEMY);
        return workspace;
    }

    public static List<Character> loadPresetEnemies() {
        return readFile(ENEMY_FILE);
    }

    public static List<Character> loadPresetPCs() {
        return readFile(PC_FILE);
    }

    private static void ensureDir() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
        } catch (IOException ignored) {}
    }

    private static void writeFile(String path, List<Character> characters) {
        List<CharacterData> dataList = new ArrayList<>();
        for (Character c : characters) {
            dataList.add(CharacterData.from(c));
        }
        try (Writer w = new FileWriter(path)) {
            gson.toJson(dataList, w);
        } catch (IOException e) {
            System.err.println("保存失败: " + e.getMessage());
        }
    }

    private static List<Character> readFile(String path) {
        File file = new File(path);
        if (!file.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(file)) {
            Type type = new TypeToken<List<CharacterData>>(){}.getType();
            List<CharacterData> dataList = gson.fromJson(r, type);
            if (dataList == null) return new ArrayList<>();
            List<Character> result = new ArrayList<>();
            for (CharacterData d : dataList) {
                result.add(d.toCharacter());
            }
            return result;
        } catch (IOException e) {
            System.err.println("读取失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static class CharacterData {
        String name;
        int str, con, siz, dex, intelligence, pow, app, edu;
        Map<String, Integer> skills;
        List<WeaponData> weapons;
        int armor;

        static CharacterData from(Character c) {
            CharacterData d = new CharacterData();
            d.name = c.getName();
            d.str = c.getStr();
            d.con = c.getCon();
            d.siz = c.getSiz();
            d.dex = c.getDex();
            d.intelligence = c.getIntelligence();
            d.pow = c.getPow();
            d.app = c.getApp();
            d.edu = c.getEdu();
            d.skills = new HashMap<>(c.getSkills());
            d.weapons = new ArrayList<>();
            for (Weapon w : c.getWeapons()) {
                d.weapons.add(WeaponData.from(w));
            }
            d.armor = c.getArmor();
            return d;
        }

        Character toCharacter() {
            Character c = new Character(name, str, con, siz, dex, intelligence, pow, app, edu);
            if (skills != null) {
                for (Map.Entry<String, Integer> e : skills.entrySet()) {
                    c.addSkill(e.getKey(), e.getValue());
                }
            }
            if (weapons != null) {
                for (WeaponData wd : weapons) {
                    c.addWeapon(wd.toWeapon());
                }
            }
            c.setArmor(armor);
            return c;
        }
    }

    private static class WeaponData {
        String name, skillName, damage;
        int range, rateOfFire, ammo;

        static WeaponData from(Weapon w) {
            WeaponData d = new WeaponData();
            d.name = w.getName();
            d.skillName = w.getSkillName();
            d.damage = w.getDamage();
            d.range = w.getRange();
            d.rateOfFire = w.getRateOfFire();
            d.ammo = w.getAmmo();
            return d;
        }

        Weapon toWeapon() {
            return new Weapon(name, skillName, damage, range, rateOfFire, ammo);
        }
    }
}
