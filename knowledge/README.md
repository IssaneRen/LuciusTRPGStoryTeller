# 知识库

轻量级文件知识管理，不需要数据库，纯 Markdown 存储。

## 分类索引

| 目录 | 内容 | 消费者 |
|------|------|--------|
| [backend/](backend/) | Go/Gin 技术知识 | Backend Developer |
| [frontend/](frontend/) | Vue/TS/CSS 技术知识 | Web Developer |
| [stories/](stories/) | TRPG 故事背景、世界观设定 | PM, Designer, 全员 |

## 写入规范

1. 文件命名: `kebab-case.md`
2. 每个文件 < 200 行，只讲一件事
3. 必须包含: 标题、一句话摘要、正文、来源
4. 新增文件后更新对应目录的 README.md

## 使用方式

AI Agent 进入知识库时:
1. 先读本文件获取全局地图
2. 进入对应分类目录读 README.md
3. 按需读取具体知识文件
