package com.lucius.trpg.ui;

import com.lucius.trpg.engine.BattleSimulator;
import com.lucius.trpg.engine.CombatOptions;
import com.lucius.trpg.engine.SimulationReport;
import com.lucius.trpg.model.Character;
import com.lucius.trpg.model.CharacterStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 主窗口 - Claude 风格暗色主题
 */
public class MainFrame extends JFrame {
    private final List<Character> pcList = new ArrayList<>();
    private final List<Character> enemyList = new ArrayList<>();
    private final CombatOptions combatOptions = new CombatOptions();
    private final JCheckBox[] optionCheckboxes = new JCheckBox[CombatOptions.NAMES.length];

    // 左侧面板组件
    private final DefaultTableModel pcTableModel;
    private final DefaultTableModel enemyTableModel;
    private final JTable pcTable;
    private final JTable enemyTable;
    private final JSpinner simulationSpinner;
    private final JButton startButton;

    // 右侧面板组件
    private final JLabel survivalLabel;
    private final JLabel roundsLabel;
    private final JLabel difficultyLabel;
    private final JTextPane logPane;
    private final StyledDocument logDoc;

    // 日志样式
    private SimpleAttributeSet defaultStyle;
    private SimpleAttributeSet successStyle;
    private SimpleAttributeSet dangerStyle;
    private SimpleAttributeSet warningStyle;

    public MainFrame() {
        super("Lucius TRPG - CoC 7th 战斗模拟器");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // 主容器
        JPanel mainPanel = new JPanel(new BorderLayout(UIConstants.PADDING, 0));
        mainPanel.setBackground(UIConstants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                UIConstants.PADDING, UIConstants.PADDING));

        // 左侧面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(UIConstants.BG_DARK);
        leftPanel.setPreferredSize(new Dimension(400, 0));

        // PC 配置区
        JPanel pcPanel = createCharacterPanel("调查员配置", true);
        pcTableModel = new DefaultTableModel(new String[]{"名称", "HP", "主技能"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pcTable = createStyledTable(pcTableModel);
        JScrollPane pcScrollPane = new JScrollPane(pcTable);
        pcScrollPane.setPreferredSize(new Dimension(380, 180));
        styleScrollPane(pcScrollPane);

        JPanel pcButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.PADDING_SMALL, 0));
        pcButtonPanel.setBackground(UIConstants.BG_CARD);
        JButton addPcButton = createButton("添加调查员");
        addPcButton.addActionListener(e -> addCharacter(true));
        JButton randomPcButton = createButton("随机生成");
        randomPcButton.addActionListener(e -> addRandomCharacter(true));
        JButton removePcButton = createButton("移除选中");
        removePcButton.addActionListener(e -> removeCharacter(true));
        JButton editPcButton = createButton("编辑");
        editPcButton.addActionListener(e -> editCharacter(true));
        pcButtonPanel.add(addPcButton);
        pcButtonPanel.add(randomPcButton);
        pcButtonPanel.add(editPcButton);
        pcButtonPanel.add(removePcButton);

        pcPanel.add(pcScrollPane);
        pcPanel.add(Box.createVerticalStrut(UIConstants.PADDING_SMALL));
        pcPanel.add(pcButtonPanel);

        // 敌人配置区
        JPanel enemyPanel = createCharacterPanel("敌人配置", false);
        enemyTableModel = new DefaultTableModel(new String[]{"名称", "HP", "主技能"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enemyTable = createStyledTable(enemyTableModel);
        JScrollPane enemyScrollPane = new JScrollPane(enemyTable);
        enemyScrollPane.setPreferredSize(new Dimension(380, 180));
        styleScrollPane(enemyScrollPane);

        JPanel enemyButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.PADDING_SMALL, 0));
        enemyButtonPanel.setBackground(UIConstants.BG_CARD);
        JButton addEnemyButton = createButton("添加怪物");
        addEnemyButton.addActionListener(e -> addCharacter(false));
        JButton presetButton = createButton("预设怪物");
        presetButton.addActionListener(e -> showPresetMenu(presetButton));
        JButton removeEnemyButton = createButton("移除选中");
        removeEnemyButton.addActionListener(e -> removeCharacter(false));
        JButton editEnemyButton = createButton("编辑");
        editEnemyButton.addActionListener(e -> editCharacter(false));
        enemyButtonPanel.add(addEnemyButton);
        enemyButtonPanel.add(presetButton);
        enemyButtonPanel.add(editEnemyButton);
        enemyButtonPanel.add(removeEnemyButton);

        enemyPanel.add(enemyScrollPane);
        enemyPanel.add(Box.createVerticalStrut(UIConstants.PADDING_SMALL));
        enemyPanel.add(enemyButtonPanel);

        // 模拟控制区
        JPanel controlPanel = createCardPanel("模拟控制");
        JPanel controlContent = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.PADDING_SMALL, UIConstants.PADDING_SMALL));
        controlContent.setBackground(UIConstants.BG_CARD);

        JLabel simLabel = new JLabel("模拟次数:");
        simLabel.setForeground(UIConstants.TEXT_PRIMARY);
        simLabel.setFont(UIConstants.BODY_FONT);
        simulationSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 100));
        simulationSpinner.setPreferredSize(new Dimension(100, 32));
        styleSpinner(simulationSpinner);

        startButton = createPrimaryButton("开始模拟");
        startButton.setPreferredSize(UIConstants.LARGE_BUTTON_SIZE);
        startButton.addActionListener(e -> startSimulation());

        JButton saveButton = createButton("保存配置");
        saveButton.addActionListener(e -> saveCharacters());

        controlContent.add(simLabel);
        controlContent.add(simulationSpinner);
        controlContent.add(Box.createHorizontalStrut(UIConstants.PADDING));
        controlContent.add(startButton);
        controlContent.add(Box.createHorizontalStrut(UIConstants.PADDING_SMALL));
        controlContent.add(saveButton);

        controlPanel.add(controlContent);

        // 可选规则面板
        JPanel rulesPanel = createCardPanel("可选规则");
        JPanel rulesGrid = new JPanel(new GridLayout(0, 1, 0, 2));
        rulesGrid.setBackground(UIConstants.BG_CARD);
        for (int i = 0; i < CombatOptions.NAMES.length; i++) {
            final int idx = i;
            JCheckBox cb = new JCheckBox(CombatOptions.NAMES[i]);
            cb.setSelected(combatOptions.getByIndex(i));
            cb.setFont(UIConstants.SMALL_FONT);
            cb.setForeground(UIConstants.TEXT_PRIMARY);
            cb.setBackground(UIConstants.BG_CARD);
            cb.setToolTipText(CombatOptions.DESCRIPTIONS[i]);
            cb.addActionListener(e -> combatOptions.setByIndex(idx, cb.isSelected()));
            optionCheckboxes[i] = cb;
            rulesGrid.add(cb);
        }
        rulesPanel.add(rulesGrid);

        leftPanel.add(pcPanel);
        leftPanel.add(Box.createVerticalStrut(UIConstants.PADDING));
        leftPanel.add(enemyPanel);
        leftPanel.add(Box.createVerticalStrut(UIConstants.PADDING));
        leftPanel.add(rulesPanel);
        leftPanel.add(Box.createVerticalStrut(UIConstants.PADDING));
        leftPanel.add(controlPanel);
        leftPanel.add(Box.createVerticalGlue());

        // 右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout(0, UIConstants.PADDING));
        rightPanel.setBackground(UIConstants.BG_DARK);

        // 结果统计卡片
        JPanel resultPanel = createCardPanel("结果统计");
        resultPanel.setPreferredSize(new Dimension(0, 200));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, UIConstants.PADDING, 0));
        statsPanel.setBackground(UIConstants.BG_CARD);
        statsPanel.setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                UIConstants.PADDING, UIConstants.PADDING));

        survivalLabel = createStatLabel("存活率", "--");
        roundsLabel = createStatLabel("平均回合", "--");
        difficultyLabel = createStatLabel("难度评级", "--");

        statsPanel.add(survivalLabel);
        statsPanel.add(roundsLabel);
        statsPanel.add(difficultyLabel);

        resultPanel.add(statsPanel);

        // 战斗日志区
        JPanel logPanel = createCardPanel("战斗日志");

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(UIConstants.BG_DARK);
        logPane.setForeground(UIConstants.TEXT_PRIMARY);
        logPane.setFont(UIConstants.SMALL_FONT);
        logPane.setCaretColor(UIConstants.TEXT_PRIMARY);

        logDoc = logPane.getStyledDocument();
        initLogStyles();

        JScrollPane logScrollPane = new JScrollPane(logPane);
        styleScrollPane(logScrollPane);

        JPanel logButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.PADDING_SMALL, UIConstants.PADDING_SMALL));
        logButtonPanel.setBackground(UIConstants.BG_CARD);
        JButton exportButton = createButton("导出日志");
        exportButton.addActionListener(e -> exportLog());
        JButton clearButton = createButton("清空日志");
        clearButton.addActionListener(e -> clearLog());
        logButtonPanel.add(clearButton);
        logButtonPanel.add(exportButton);

        logPanel.add(logScrollPane, BorderLayout.CENTER);
        logPanel.add(logButtonPanel, BorderLayout.SOUTH);

        rightPanel.add(resultPanel, BorderLayout.NORTH);
        rightPanel.add(logPanel, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // 启动时加载持久化角色
        loadSavedCharacters();
    }

    private void loadSavedCharacters() {
        List<Character> savedPCs = CharacterStore.loadPCs();
        for (Character pc : savedPCs) {
            pcList.add(pc);
            pcTableModel.addRow(new Object[]{
                    pc.getName(),
                    pc.getMaxHp(),
                    getMainSkillDisplay(pc)
            });
        }
        List<Character> savedEnemies = CharacterStore.loadEnemies();
        for (Character enemy : savedEnemies) {
            enemyList.add(enemy);
            enemyTableModel.addRow(new Object[]{
                    enemy.getName(),
                    enemy.getMaxHp(),
                    getMainSkillDisplay(enemy)
            });
        }
    }

    private void saveCharacters() {
        CharacterStore.save(pcList, enemyList);
        JOptionPane.showMessageDialog(this, "配置已保存", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editCharacter(boolean isPc) {
        JTable table = isPc ? pcTable : enemyTable;
        List<Character> list = isPc ? pcList : enemyList;
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要编辑的角色", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Character existing = list.get(row);
        Character edited = CharacterDialog.showEditDialog(this, existing, !isPc);
        if (edited != null) {
            list.set(row, edited);
            DefaultTableModel model = isPc ? pcTableModel : enemyTableModel;
            model.setValueAt(edited.getName(), row, 0);
            model.setValueAt(edited.getMaxHp(), row, 1);
            model.setValueAt(getMainSkillDisplay(edited), row, 2);
        }
    }

    private String getMainSkillDisplay(Character c) {
        int fighting = c.getSkill("格斗");
        int dodge = c.getDodge();
        return "格斗" + fighting + "/闪避" + dodge;
    }

    private JPanel createCharacterPanel(String title, boolean isPc) {
        JPanel panel = createCardPanel(title);
        panel.setMaximumSize(new Dimension(400, 280));
        return panel;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_CARD);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UIConstants.TITLE_FONT,
                UIConstants.TEXT_PRIMARY
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(UIConstants.PADDING_SMALL, UIConstants.PADDING_SMALL,
                        UIConstants.PADDING_SMALL, UIConstants.PADDING_SMALL)
        ));

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(UIConstants.BG_DARK);
        table.setForeground(UIConstants.TEXT_PRIMARY);
        table.setFont(UIConstants.BODY_FONT);
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(UIConstants.BORDER);
        table.setSelectionBackground(UIConstants.PRIMARY);
        table.setSelectionForeground(Color.WHITE);

        // 表头样式
        table.getTableHeader().setBackground(UIConstants.BG_CARD);
        table.getTableHeader().setForeground(UIConstants.TEXT_PRIMARY);
        table.getTableHeader().setFont(UIConstants.BODY_FONT);

        // 单元格居中
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(UIConstants.BG_DARK);
        centerRenderer.setForeground(UIConstants.TEXT_PRIMARY);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(UIConstants.BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);
    }

    private void styleSpinner(JSpinner spinner) {
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
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setForeground(UIConstants.TEXT_PRIMARY);
        button.setBackground(UIConstants.BG_DARK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER, 1),
                new EmptyBorder(UIConstants.PADDING_SMALL, UIConstants.PADDING, UIConstants.PADDING_SMALL, UIConstants.PADDING)
        ));
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.PRIMARY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(UIConstants.PADDING_SMALL, UIConstants.PADDING,
                UIConstants.PADDING_SMALL, UIConstants.PADDING));
        return button;
    }

    private JLabel createStatLabel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setFont(UIConstants.SMALL_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        valueLabel.setFont(UIConstants.LARGE_FONT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(UIConstants.PADDING_SMALL));
        panel.add(valueLabel);

        return valueLabel; // 返回 value label 以便后续更新
    }

    private void initLogStyles() {
        defaultStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultStyle, UIConstants.TEXT_PRIMARY);

        successStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(successStyle, UIConstants.SUCCESS);

        dangerStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(dangerStyle, UIConstants.DANGER);

        warningStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(warningStyle, UIConstants.WARNING);
    }

    private void addCharacter(boolean isPc) {
        Character character = CharacterDialog.showDialog(this, !isPc);
        if (character != null) {
            if (isPc) {
                pcList.add(character);
                refreshPcTable();
            } else {
                enemyList.add(character);
                refreshEnemyTable();
            }
        }
    }

    private void addRandomCharacter(boolean isPc) {
        // 创建一个随机角色
        CharacterDialog dialog = new CharacterDialog(this, !isPc);
        // 自动随机化（需要修改 CharacterDialog 支持自动随机）
        // 这里简化处理：弹出对话框让用户点击随机按钮
        addCharacter(isPc);
    }

    private void removeCharacter(boolean isPc) {
        if (isPc) {
            int selectedRow = pcTable.getSelectedRow();
            if (selectedRow >= 0) {
                pcList.remove(selectedRow);
                refreshPcTable();
            }
        } else {
            int selectedRow = enemyTable.getSelectedRow();
            if (selectedRow >= 0) {
                enemyList.remove(selectedRow);
                refreshEnemyTable();
            }
        }
    }

    private void showPresetMenu(JButton button) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(UIConstants.BG_CARD);
        menu.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));

        String[] presets = {"深潜者", "修格斯幼体", "食尸鬼"};
        for (String preset : presets) {
            JMenuItem item = new JMenuItem(preset);
            item.setBackground(UIConstants.BG_CARD);
            item.setForeground(UIConstants.TEXT_PRIMARY);
            item.setFont(UIConstants.BODY_FONT);
            item.addActionListener(e -> addPresetEnemy(preset));
            menu.add(item);
        }

        menu.show(button, 0, button.getHeight());
    }

    private void addPresetEnemy(String type) {
        Character enemy = null;
        switch (type) {
            case "深潜者":
                enemy = new Character("深潜者", 65, 65, 70, 50, 50, 50, 50, 50);
                enemy.addSkill("格斗", 60);
                enemy.addWeapon(new com.lucius.trpg.model.Weapon("爪抓", "格斗", "1D6+DB", 0, 1, -1));
                enemy.setArmor(2);
                break;
            case "修格斯幼体":
                enemy = new Character("修格斯幼体", 80, 90, 100, 30, 25, 40, 30, 30);
                enemy.addSkill("格斗", 70);
                enemy.addWeapon(new com.lucius.trpg.model.Weapon("吞噬", "格斗", "2D6", 0, 1, -1));
                enemy.setArmor(3);
                break;
            case "食尸鬼":
                enemy = new Character("食尸鬼", 55, 55, 60, 65, 40, 45, 40, 40);
                enemy.addSkill("格斗", 55);
                enemy.addWeapon(new com.lucius.trpg.model.Weapon("爪抓", "格斗", "1D6", 0, 1, -1));
                enemy.setArmor(1);
                break;
        }

        if (enemy != null) {
            enemyList.add(enemy);
            refreshEnemyTable();
        }
    }

    private void refreshPcTable() {
        pcTableModel.setRowCount(0);
        for (Character pc : pcList) {
            int fighting = pc.getSkill("格斗");
            int dodge = pc.getDodge();
            pcTableModel.addRow(new Object[]{
                    pc.getName(),
                    pc.getMaxHp(),
                    String.format("格斗%d/闪避%d", fighting, dodge)
            });
        }
    }

    private void refreshEnemyTable() {
        enemyTableModel.setRowCount(0);
        for (Character enemy : enemyList) {
            int fighting = enemy.getSkill("格斗");
            int dodge = enemy.getDodge();
            enemyTableModel.addRow(new Object[]{
                    enemy.getName(),
                    enemy.getMaxHp(),
                    String.format("格斗%d/闪避%d", fighting, dodge)
            });
        }
    }

    private void startSimulation() {
        if (pcList.isEmpty() || enemyList.isEmpty()) {
            appendLog("错误: 请至少添加一个调查员和一个敌人\n", dangerStyle);
            return;
        }

        int times = (Integer) simulationSpinner.getValue();

        // 禁用开始按钮
        startButton.setEnabled(false);
        startButton.setText("模拟中...");

        // 使用 SwingWorker 在后台运行模拟
        SwingWorker<SimulationReport, String> worker = new SwingWorker<>() {
            @Override
            protected SimulationReport doInBackground() throws Exception {
                BattleSimulator simulator = new BattleSimulator(combatOptions);
                return simulator.simulate(new ArrayList<>(pcList), new ArrayList<>(enemyList), times);
            }

            @Override
            protected void done() {
                try {
                    SimulationReport report = get();
                    updateResults(report);
                    if (report.getSampleLog() != null && !report.getSampleLog().isEmpty()) {
                        appendLog(report.getSampleLog(), defaultStyle);
                    }
                    appendLog("\n模拟完成!\n", successStyle);
                } catch (Exception e) {
                    appendLog("模拟出错: " + e.getMessage() + "\n", dangerStyle);
                    e.printStackTrace();
                } finally {
                    startButton.setEnabled(true);
                    startButton.setText("开始模拟");
                }
            }
        };

        worker.execute();
    }

    private void updateResults(SimulationReport report) {
        survivalLabel.setText(String.format("%.1f%%", report.getSurvivalRate() * 100));
        roundsLabel.setText(String.format("%.1f", report.getAvgRounds()));
        difficultyLabel.setText(report.getDifficultyDescription());
    }

    private void appendLog(String text, AttributeSet style) {
        try {
            logDoc.insertString(logDoc.getLength(), text, style);
            logPane.setCaretPosition(logDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendLog(List<String> lines, AttributeSet style) {
        for (String line : lines) {
            appendLog(line + "\n", style);
        }
    }

    private void clearLog() {
        logPane.setText("");
    }

    private void exportLog() {
        String log = logPane.getText();
        if (log.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "日志为空，无法导出",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = String.format("logs/battle_log_%s.txt", timestamp);

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(log);
            }

            appendLog(String.format("\n日志已导出到: %s\n", filename), successStyle);
        } catch (IOException e) {
            appendLog("导出日志失败: " + e.getMessage() + "\n", dangerStyle);
            e.printStackTrace();
        }
    }
}
