# Lucius TRPG Story Teller

AI 驱动的 TRPG 故事讲述平台。

## 快速开始

```bash
make install   # 安装依赖
make dev       # 启动所有服务
```

启动后访问:
- 用户端: http://localhost:3000
- 管理后台: http://localhost:5173
- API: http://localhost:8080

## 项目结构

```
backend/    → Go API 服务（鉴权）
frontend/   → 管理后台（Vue 3）
web/        → 用户端展示页（Vue 3）
knowledge/  → 知识库（Markdown）
docs/       → 项目文档
guides/     → 使用指南
```

## 文档导航

| 我想... | 看这里 |
|---------|--------|
| 快速跑起来 | [guides/quick-start.md](guides/quick-start.md) |
| 了解架构 | [docs/architecture.md](docs/architecture.md) |
| 了解技术选型 | [docs/decisions/0001-tech-stack.md](docs/decisions/0001-tech-stack.md) |
| 部署到服务器 | [guides/deployment.md](guides/deployment.md) |
| AI Agent 工作流 | [docs/workflow.md](docs/workflow.md) |
| Agent 角色说明 | [docs/agents.md](docs/agents.md) |

## 技术栈

- **后端**: Go 1.22+ / Gin / JWT / SQLite
- **前端**: Vue 3 / TypeScript / Vite / Naive UI / UnoCSS
- **AI 工具**: Claude Code / Cursor / Codex
