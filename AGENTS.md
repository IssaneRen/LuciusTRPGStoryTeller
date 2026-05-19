# LuciusTrpgStoryTeller

> AI 超级个体全栈项目 — 单人 + 多 AI Agent 协作开发 TRPG 故事讲述平台

## 项目概览

本项目采用 Orchestrator-Workers 编排模式，由一位全栈开发者配合多个 AI Agent 角色协作完成开发。

- 架构文档: [docs/architecture.md](docs/architecture.md)
- 技术选型: [docs/decisions/0001-tech-stack.md](docs/decisions/0001-tech-stack.md)
- 工作流程: [docs/workflow.md](docs/workflow.md)
- Agent 角色定义: [docs/agents.md](docs/agents.md)
- 使用指南: [guides/README.md](guides/README.md)
- 全链路交付 Skill: [.claude/skills/full-delivery/SKILL.md](.claude/skills/full-delivery/SKILL.md)

## 项目结构

```
.
├── AGENTS.md              # 通用 AI Rules 入口（本文件）
├── CLAUDE.md              # Claude Code 入口（@AGENTS.md）
├── .claude/agents/        # Agent 角色定义
├── .claude/rules/         # 条件加载规则
├── .cursor/rules/         # Cursor 特有规则
├── docs/                  # 项目文档（分层索引）
├── guides/                # 使用指南（快速开始、部署等）
├── knowledge/             # 知识库（md 文件管理）
├── backend/               # 后端 API（Go + Gin，:8080）
├── frontend/              # 管理后台（Vue 3 + Naive UI，:5173）
├── web/                   # 用户端展示（Vue 3 + Naive UI，:3000）
├── Makefile               # 统一任务入口（make help）
# 以下为规划中的模块，目录在实际启动时创建:
# - miniprogram/           # 小程序项目（uni-app）
# - flutter/               # Flutter 客户端（Dart）
```

## 核心开发规范

### 代码风格

- 后端: Go 标准格式 (gofmt)，函数注释仅在 exported 函数上
- 前端: TypeScript strict mode，Vue 3 Composition API + `<script setup>`
- 通用: 文件 < 300 行，函数 < 50 行，单一职责

### 提交规范

格式: `<type>(<scope>): <subject>`

type: feat | fix | docs | refactor | test | chore
scope: backend | frontend | knowledge | docs | agents

### 文档规范

- 每个 md 文件 < 200 行，只说明一件事
- 信息不冗余：同一内容只在一处定义，其他处用相对路径链接
- 每个目录必须有 README.md 作为该层索引

### 知识库写入规范

- 新增知识先确定分类目录
- 文件命名: kebab-case，如 `go-jwt-auth.md`
- 写入后更新对应目录的 README.md 索引

## Agent 调度规则

详见 [docs/workflow.md](docs/workflow.md)

任务复杂度判定:
- **简单任务** (< 30min): Leader 直接完成
- **中等任务** (30min-2h): 派发给单个 Agent
- **复杂任务** (> 2h): 拆分为子任务，多 Agent 并行

## 禁止事项

- 不要生成超过 300 行的单文件
- 不要在多处重复同一信息
- 不要创建空目录占位（用文档标注"待实现"即可）
- 不要在知识库中存放可运行代码（代码属于工程目录）
- 不要修改 Agent 定义文件而不经 Consultant 审查
