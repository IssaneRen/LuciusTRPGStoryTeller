# UI Package - Claude 风格暗色主题 Swing 界面

## 概述

本包实现了 CoC 7th 战斗模拟器的桌面客户端 UI，采用 Claude 风格的暗色主题设计。

## 文件说明

### UIConstants.java

定义了全局 UI 常量，包括：
- 颜色定义（背景、文字、按钮等）
- 字体样式
- 间距和尺寸

主色调：
- 背景: `#1a1a2e` (深蓝黑)
- 卡片背景: `#2d2d44`
- 主色: `#8b5cf6` (紫色)
- 圆角: 12px

### MainFrame.java

主窗口，包含：
- 左侧面板（400px）
  - 调查员配置区：表格 + 添加/随机/移除按钮
  - 敌人配置区：表格 + 添加/预设/移除按钮
  - 模拟控制区：次数输入 + 开始按钮

- 右侧面板
  - 结果统计卡片：存活率、平均回合、难度评级
  - 战斗日志区：富文本日志 + 导出/清空按钮

特性：
- 使用 SwingWorker 后台执行模拟，不阻塞 UI
- 日志支持彩色输出（成功=绿色，危险=红色，警告=黄色）
- 自动导出日志到 `logs/` 目录

### CharacterDialog.java

角色配置对话框，模态窗口，包含：
- 基础属性输入：STR、CON、SIZ、DEX、INT、POW
- 技能输入：格斗、手枪、步枪
- 武器选择：下拉菜单（预设 6 种武器）
- 护甲输入
- 随机生成按钮：按 CoC 7th 规则（3D6×5）生成属性

## 使用方式

在 Main.java 中初始化：

```java
UIManager.setLookAndFeel(new FlatDarkLaf());
UIManager.put("Button.arc", UIConstants.RADIUS);
MainFrame frame = new MainFrame();
frame.setVisible(true);
```

## 依赖

- FlatLaf 3.6+：现代化 Swing 主题库
- JDK 17+：使用了现代 Java 特性

## 设计规范

- 所有面板使用 EmptyBorder 留白
- 圆角统一为 12px
- 按钮高度 36px（普通）/ 48px（大按钮）
- 表格行高 32px
- 颜色使用 UIConstants 中定义的常量
- 字体使用 SansSerif 系列

## 已知限制

1. 随机生成按钮在对话框中需要用户手动点击（未实现自动填充）
2. 闪避技能由 Character 类自动计算（DEX/2），不在对话框中设置
3. 日志导出为纯文本格式，不保留颜色信息

## 未来优化方向

- 支持主题切换（亮色/暗色）
- 添加角色模板保存/加载功能
- 优化日志显示性能（大量日志时）
- 添加动画效果（按钮悬停、过渡等）
