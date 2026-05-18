# Claude Code 项目配置

@AGENTS.md

## Claude 特有指令

- 使用 subagent 做研究任务，保持主上下文干净
- 复杂任务先进入 plan mode，确认后再实施
- 每完成一个子任务判断当前上下文占用情况，决定是否用 `/clear` 清理上下文
- 优先使用 worktree 并行处理独立功能

## 条件规则

后端相关规则见 `.claude/rules/backend.md`
前端相关规则见 `.claude/rules/frontend.md`
