# Agent 角色定义

本文档是所有 Agent 角色的索引入口。每个 Agent 的完整提示词定义在 `.claude/agents/` 目录中。

## 角色总览

| 角色 | 文件 | 核心职责 | 产出物 |
|------|------|---------|--------|
| Leader | [leader.md](../.claude/agents/leader.md) | 任务调度、上下文管理 | 任务拆分方案 |
| Researcher | [researcher.md](../.claude/agents/researcher.md) | 信息搜索、知识整理 | 知识库文件 |
| Backend Dev | [backend-developer.md](../.claude/agents/backend-developer.md) | 后端开发 | Go 代码 + 测试 |
| Web Dev | [web-developer.md](../.claude/agents/web-developer.md) | 前端开发 | Vue 组件 + 页面 |
| QA | [qa.md](../.claude/agents/qa.md) | 质量保障 | 测试用例 + 自动化脚本 |
| Consultant | [consultant.md](../.claude/agents/consultant.md) | 技术审查 | 审查意见 + 改进建议 |
| PM | [pm.md](../.claude/agents/pm.md) | 产品设计 | PRD + 逻辑流程图 |
| Designer | [designer.md](../.claude/agents/designer.md) | 视觉/交互设计 | 设计稿 + 图片素材 |

## 职责边界

- 每个 Agent 只对自己的产出物负责
- Agent 间通过文件交接（不依赖对话上下文）
- Consultant 对所有技术产出有审查权
- PM 对需求文档有最终解释权

## 制衡机制

- Developer 写代码 → QA 写测试验证 → Consultant 审查
- PM 出方案 → Consultant 评估可行性 → Developer 反馈工期
- Researcher 产出知识 → 对应 Agent 消费并反馈准确性
