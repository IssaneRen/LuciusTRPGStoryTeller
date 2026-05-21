package com.lucius.trpg;

import com.formdev.flatlaf.FlatDarkLaf;
import com.lucius.trpg.ui.MainFrame;
import com.lucius.trpg.ui.UIConstants;

import javax.swing.*;

/**
 * CoC 7版战斗模拟器主入口
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 设置 FlatLaf 暗色主题
                UIManager.setLookAndFeel(new FlatDarkLaf());

                // 设置全局 UI 属性
                UIManager.put("Button.arc", UIConstants.RADIUS);
                UIManager.put("Component.arc", UIConstants.RADIUS);
                UIManager.put("TextComponent.arc", UIConstants.RADIUS);
                UIManager.put("ScrollPane.arc", UIConstants.RADIUS);

                // 创建并显示主窗口
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to initialize FlatLaf");
                e.printStackTrace();
            }
        });
    }
}
