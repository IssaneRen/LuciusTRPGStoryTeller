package com.lucius.trpg.ui;

import java.awt.*;

/**
 * Claude 风格暗色主题 UI 常量
 */
public class UIConstants {
    // 颜色
    public static final Color BG_DARK = new Color(0x1a, 0x1a, 0x2e);
    public static final Color BG_CARD = new Color(0x2d, 0x2d, 0x44);
    public static final Color BORDER = new Color(0x3d, 0x3d, 0x5c);
    public static final Color PRIMARY = new Color(0x8b, 0x5c, 0xf6);
    public static final Color PRIMARY_HOVER = new Color(0x7c, 0x4d, 0xe7);
    public static final Color TEXT_PRIMARY = new Color(0xe2, 0xe8, 0xf0);
    public static final Color TEXT_SECONDARY = new Color(0x94, 0xa3, 0xb8);
    public static final Color SUCCESS = new Color(0x10, 0xb9, 0x81);
    public static final Color DANGER = new Color(0xef, 0x44, 0x44);
    public static final Color WARNING = new Color(0xf5, 0x9e, 0x0b);

    // 圆角
    public static final int RADIUS = 12;

    // 字体
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font LARGE_FONT = new Font("SansSerif", Font.BOLD, 24);

    // 间距
    public static final int PADDING = 16;
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_LARGE = 24;

    // 尺寸
    public static final Dimension BUTTON_SIZE = new Dimension(120, 36);
    public static final Dimension LARGE_BUTTON_SIZE = new Dimension(200, 48);

    private UIConstants() {
        // 禁止实例化
    }
}
