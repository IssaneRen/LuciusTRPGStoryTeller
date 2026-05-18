# AI Agent 使用指南

## 首次接触本项目

1. 阅读 AGENTS.md 了解项目规范
2. `make help` 查看所有可用命令
3. `make dev` 启动开发环境

## 常见工作场景

### 新增 API 端点

1. 在 `backend/internal/handler/` 新建或修改 handler
2. 在 `backend/internal/router/router.go` 注册路由
3. 运行 `make test-backend` 验证

### 新增前端页面

1. 在 `frontend/src/views/` 新建 Vue 组件
2. 在 `frontend/src/router/routes.ts` 添加路由
3. 运行 `make dev-frontend` 预览

### 新增知识文件

1. 确定分类目录 (knowledge/backend/ 或 frontend/ 或 stories/)
2. 创建 kebab-case.md 文件
3. 更新对应目录 README.md 索引

## 提交前检查

```bash
make check
```

等价于依次执行: fmt → lint → test

## Agent 角色切换

在 Claude Code 中使用 `/agent <name>` 切换角色:
- `/agent leader` — 任务调度
- `/agent researcher` — 信息搜索
- `/agent backend-developer` — 后端开发
- `/agent web-developer` — 前端开发
- `/agent qa` — 测试
- `/agent consultant` — 技术审查
