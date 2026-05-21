package com.lucius.trpg.ui;

import com.lucius.trpg.model.Character;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/**
 * 角色配置对话框
 */
public class CharacterDialog extends JDialog {
    private final JTextField nameField;
    private final JSpinner strSpinner;
    private final JSpinner conSpinner;
    private final JSpinner sizSpinner;
    private final JSpinner dexSpinner;
    private final JSpinner intSpinner;
    private final JSpinner powSpinner;
    private final JSpinner fightingSpinner;
    private final JSpinner pistolSpinner;
    private final JSpinner rifleSpinner;
    private final JComboBox<String> weaponCombo;
    private final JSpinner armorSpinner;

    private Character result = null;
    private final boolean isEnemy;

    public CharacterDialog(Frame parent, boolean isEnemy) {
        super(parent, isEnemy ? "添加怪物" : "添加调查员", true);
        this.isEnemy = isEnemy;

        setSize(600, 700);
        setLocationRelativeTo(parent);
        setBackground(UIConstants.BG_DARK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                UIConstants.PADDING, UIConstants.PADDING));

        // 标题
        JLabel titleLabel = new JLabel(isEnemy ? "怪物配置" : "调查员配置");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, UIConstants.PADDING, 0));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.BG_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER, 1),
                new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                        UIConstants.PADDING, UIConstants.PADDING)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // 名称
        nameField = createTextField();
        addFormRow(formPanel, gbc, 0, "名称", nameField);

        // 属性
        strSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 1, "力量 (STR)", strSpinner);

        conSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 2, "体质 (CON)", conSpinner);

        sizSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 3, "体型 (SIZ)", sizSpinner);

        dexSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 4, "敏捷 (DEX)", dexSpinner);

        intSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 5, "智力 (INT)", intSpinner);

        powSpinner = createSpinner(50, 15, 99);
        addFormRow(formPanel, gbc, 6, "意志 (POW)", powSpinner);

        // 技能
        fightingSpinner = createSpinner(50, 0, 99);
        addFormRow(formPanel, gbc, 7, "格斗", fightingSpinner);

        pistolSpinner = createSpinner(50, 0, 99);
        addFormRow(formPanel, gbc, 8, "手枪", pistolSpinner);

        rifleSpinner = createSpinner(50, 0, 99);
        addFormRow(formPanel, gbc, 9, "步枪", rifleSpinner);

        // 武器
        weaponCombo = new JComboBox<>(new String[]{
                "拳头 (1D3+DB)", "匕首 (1D4+DB)", "棍棒 (1D6+DB)",
                "手枪 (1D10)", "步枪 (2D6+4)", "霰弹枪 (4D6/2D6/1D6)"
        });
        weaponCombo.setBackground(UIConstants.BG_DARK);
        weaponCombo.setForeground(UIConstants.TEXT_PRIMARY);
        addFormRow(formPanel, gbc, 10, "武器", weaponCombo);

        // 护甲
        armorSpinner = createSpinner(0, 0, 10);
        addFormRow(formPanel, gbc, 11, "护甲", armorSpinner);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.PADDING_SMALL, 0));
        buttonPanel.setBackground(UIConstants.BG_DARK);
        buttonPanel.setBorder(new EmptyBorder(UIConstants.PADDING, 0, 0, 0));

        JButton randomButton = createButton("随机生成属性");
        randomButton.addActionListener(e -> randomizeAttributes());

        JButton confirmButton = createPrimaryButton("确认");
        confirmButton.addActionListener(e -> onConfirm());

        JButton cancelButton = createButton("取消");
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(randomButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel jLabel = new JLabel(label + ":");
        jLabel.setForeground(UIConstants.TEXT_PRIMARY);
        jLabel.setFont(UIConstants.BODY_FONT);
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setBackground(UIConstants.BG_DARK);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setCaretColor(UIConstants.TEXT_PRIMARY);
        field.setFont(UIConstants.BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    private JSpinner createSpinner(int initial, int min, int max) {
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setBackground(UIConstants.BG_DARK);
        spinner.setForeground(UIConstants.TEXT_PRIMARY);
        spinner.setFont(UIConstants.BODY_FONT);

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) editor;
            defEditor.getTextField().setBackground(UIConstants.BG_DARK);
            defEditor.getTextField().setForeground(UIConstants.TEXT_PRIMARY);
            defEditor.getTextField().setCaretColor(UIConstants.TEXT_PRIMARY);
        }

        return spinner;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setForeground(UIConstants.TEXT_PRIMARY);
        button.setBackground(UIConstants.BG_CARD);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(UIConstants.BUTTON_SIZE);
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.PRIMARY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(UIConstants.BUTTON_SIZE);
        return button;
    }

    private void randomizeAttributes() {
        Random rand = new Random();

        // 3D6 * 5 规则生成属性
        strSpinner.setValue(rollAttribute(rand));
        conSpinner.setValue(rollAttribute(rand));
        sizSpinner.setValue(rollAttribute(rand));
        dexSpinner.setValue(rollAttribute(rand));
        intSpinner.setValue(rollAttribute(rand));
        powSpinner.setValue(rollAttribute(rand));

        // 技能基础值 + 一些随机加成
        fightingSpinner.setValue(25 + rand.nextInt(41)); // 25-65
        pistolSpinner.setValue(20 + rand.nextInt(51)); // 20-70
        rifleSpinner.setValue(25 + rand.nextInt(46)); // 25-70
    }

    private int rollAttribute(Random rand) {
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += rand.nextInt(6) + 1;
        }
        return sum * 5;
    }

    private void onConfirm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入角色名称",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 构建角色对象
        result = new Character(
                name,
                (Integer) strSpinner.getValue(),
                (Integer) conSpinner.getValue(),
                (Integer) sizSpinner.getValue(),
                (Integer) dexSpinner.getValue(),
                (Integer) intSpinner.getValue(),
                (Integer) powSpinner.getValue(),
                50, // APP 默认值
                50  // EDU 默认值
        );

        // 添加技能 (闪避已经在Character构造函数中自动计算)
        result.addSkill("格斗", (Integer) fightingSpinner.getValue());
        result.addSkill("手枪", (Integer) pistolSpinner.getValue());
        result.addSkill("步枪", (Integer) rifleSpinner.getValue());

        // 添加武器
        String weaponStr = (String) weaponCombo.getSelectedItem();
        com.lucius.trpg.model.Weapon weapon = parseWeapon(weaponStr);
        if (weapon != null) {
            result.addWeapon(weapon);
        }

        // 设置护甲
        result.setArmor((Integer) armorSpinner.getValue());

        dispose();
    }

    private com.lucius.trpg.model.Weapon parseWeapon(String weaponStr) {
        if (weaponStr == null) return null;

        if (weaponStr.startsWith("拳头")) {
            return new com.lucius.trpg.model.Weapon("拳头", "格斗", "1D3+DB", 0, 1, -1);
        } else if (weaponStr.startsWith("匕首")) {
            return new com.lucius.trpg.model.Weapon("匕首", "格斗", "1D4+DB", 0, 1, -1);
        } else if (weaponStr.startsWith("棍棒")) {
            return new com.lucius.trpg.model.Weapon("棍棒", "格斗", "1D6+DB", 0, 1, -1);
        } else if (weaponStr.startsWith("手枪")) {
            return new com.lucius.trpg.model.Weapon("手枪", "手枪", "1D10", 15, 3, 6);
        } else if (weaponStr.startsWith("步枪")) {
            return new com.lucius.trpg.model.Weapon("步枪", "步枪", "2D6+4", 90, 1, 5);
        } else if (weaponStr.startsWith("霰弹枪")) {
            return new com.lucius.trpg.model.Weapon("霰弹枪", "步枪", "4D6/2D6/1D6", 10, 1, 2);
        }

        return null;
    }

    private void onCancel() {
        result = null;
        dispose();
    }

    public Character getResult() {
        return result;
    }

    public static Character showDialog(Frame parent, boolean isEnemy) {
        CharacterDialog dialog = new CharacterDialog(parent, isEnemy);
        dialog.setVisible(true);
        return dialog.getResult();
    }

    public static Character showEditDialog(Frame parent, Character existing, boolean isEnemy) {
        CharacterDialog dialog = new CharacterDialog(parent, isEnemy);
        dialog.nameField.setText(existing.getName());
        dialog.strSpinner.setValue(existing.getStr());
        dialog.conSpinner.setValue(existing.getCon());
        dialog.sizSpinner.setValue(existing.getSiz());
        dialog.dexSpinner.setValue(existing.getDex());
        dialog.intSpinner.setValue(existing.getIntelligence());
        dialog.powSpinner.setValue(existing.getPow());
        dialog.fightingSpinner.setValue(existing.getSkill("格斗"));
        dialog.pistolSpinner.setValue(existing.getSkill("手枪"));
        dialog.rifleSpinner.setValue(existing.getSkill("步枪"));
        dialog.armorSpinner.setValue(existing.getArmor());
        dialog.setVisible(true);
        return dialog.getResult();
    }
}
