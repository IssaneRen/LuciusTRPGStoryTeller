# CoC 7th 战斗模拟器

Java Swing 桌面应用，模拟 Call of Cthulhu 7th Edition 战斗并评估数值平衡。

## 前置要求

- Java 17+
- Gradle 8+

## 启动

```bash
cd java-client
gradle run
```

## 功能

- 可视化配置调查员 PC（属性、技能、武器）
- 预设常见怪物（深潜者、修格斯幼体、食尸鬼）
- 批量模拟 N 次战斗
- 实时输出战斗日志（带颜色标记）
- 自动评估难度（1-5 星）
- 日志导出到 logs/ 目录

## 规则引擎

详见 [.claude/skills/coc7th-combat/SKILL.md](../.claude/skills/coc7th-combat/SKILL.md)

支持:
- 近战（格斗+闪避对抗）
- 枪械（手枪/步枪/霰弹枪/冲锋枪）
- 伤害加值 DB
- 护甲减伤
- 重伤昏迷判定
