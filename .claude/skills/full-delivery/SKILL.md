---
name: full-delivery
description: 全链路业务需求交付。从需求描述到产品设计→UI设计→技术评审→并行开发→测试→审查→交付，全自动执行，阶段性汇报可打断。触发词：开始开发、全链路。
---

# Full Delivery — 全链路业务交付

当用户说"开始开发"或"全链路"并描述业务需求时，执行以下全自动流水线。

## 核心原则

1. **不卡死** — 所有阶段自动推进，不等用户确认。用户看到汇报后可主动打断。
2. **契约优先** — PM+设计完成后，Consultant 定义接口契约，前后端严格按契约并行开发。
3. **产物落地** — 每阶段产出写入 `.tasks/YYYY-MM-DD-<slug>/`，打断后可基于产物恢复。

## 执行流程

### Phase 0: 需求理解 (Leader — 主对话直接执行)

1. 解析用户需求，判断复杂度
2. 写入 `.tasks/<id>/brief.md`:
   - 需求一句话摘要
   - 影响范围（哪些端）
   - 复杂度评估（简单/中等/复杂）
3. 决定跳过策略:
   - 简单（单文件/文档）→ 直接完成，不走流水线
   - 中等（单端改动）→ 跳过 Phase 1-2，直接从 Phase 3 开始
   - 复杂（跨端功能）→ 走完整流水线
   - 过大（多模块）→ 拆为多个迭代，每个独立走流水线
4. 汇报并立即进入下一阶段

### Phase 1: 产品设计 (PM — subagent)

派发 PM agent，输入 brief.md，要求产出:
- `.tasks/<id>/prd.md`
- 包含: 功能描述、核心用户流程、页面列表、边界条件、验收标准
- 不超过 150 行

汇报后立即进入 Phase 2。

### Phase 2: 交互与视觉设计 (Designer — subagent)

派发 Designer agent，输入 prd.md，要求:
- 调用 Figma MCP 创建设计稿
- 写入 `.tasks/<id>/design.md`:
  - 页面列表与 Figma 链接
  - 组件状态说明（default/hover/disabled/loading/error）
  - 响应式断点要求
- 如果 Figma MCP 不可用，降级为 ASCII 线框图 + 交互说明

汇报后立即进入 Phase 3。

### Phase 3: 技术评审与契约定义 (Consultant — subagent)

派发 Consultant agent，输入 prd.md + design.md，要求:
- 评估技术可行性
- 写入 `.tasks/<id>/contract.md`:
  - API 端点定义（路径、方法、请求体、响应体、状态码）
  - 数据模型（新增/修改的 struct/type）
  - 前端需要的状态管理和路由变更
  - 前后端职责边界
- 如果 PRD 或设计有问题，在 contract.md 顶部标注 `## ⚠️ 打回` 并说明原因
  - 此时自动回到 Phase 1 或 Phase 2 重做

汇报后立即进入 Phase 4。

### Phase 4: 并行开发 (Backend Dev + Web Dev — 并行 subagent)

同时派发两个 agent:

**Backend Dev** 输入: contract.md
- 按契约实现 API 端点
- 写入 `backend/` 工程目录
- 确保与现有代码风格一致

**Web Dev** 输入: contract.md + design.md
- 按契约对接 API
- 按设计稿实现页面
- 写入 `frontend/` 或 `web/` 工程目录

两个 agent 完成后汇总汇报。

### Phase 5: 集成验证 (QA — subagent)

派发 QA agent，输入: 代码 + contract.md，要求:
- 验证 API 是否与契约一致
- 验证前端调用是否与契约一致
- 如果可以运行: 启动服务，执行 curl 测试
- 写入 `.tasks/<id>/test-report.md`:
  - 通过/失败的用例列表
  - 不一致项标注
- 如果有不一致: 自动通知对应 agent 修复后重新验证

汇报后进入 Phase 6。

### Phase 6: 代码审查 (Consultant — subagent)

派发 Consultant agent，输入: 代码 + contract.md + test-report.md，要求:
- 安全性检查
- 代码规范一致性
- 写入 `.tasks/<id>/review.md`:
  - 通过/不通过
  - 问题列表（Critical/Warning/Suggestion）
- 不通过时: 标注具体修复点，回到 Phase 4 修复后重新审查
- 最多循环 2 次，仍不通过则汇报给用户

汇报后进入 Phase 7。

### Phase 7: 交付 (Leader — 主对话)

1. 在 `.tasks/<id>/progress.md` 标记所有阶段完成
2. git add + commit 所有变更
3. 更新相关文档索引（如果新增了页面/API）
4. 输出最终交付汇报:
   - 变更摘要
   - 新增/修改文件列表
   - 验证方式（如何确认功能正常）
   - `.tasks/<id>/` 路径（完整过程记录）

## 汇报模板

每阶段完成后输出:

```
✓ Phase N: [阶段名] 完成
  产物: .tasks/<id>/<file>.md
  摘要: [一句话]
```

## 打断恢复

用户随时可以打断。恢复时:
1. 读取 `.tasks/<id>/progress.md` 确认断点
2. 检查用户是否修改了已有产物
3. 如果契约被修改 → 标记后续代码需重新对齐
4. 从断点的下一阶段继续执行

## progress.md 格式

```markdown
# 进度追踪

## 任务: <slug>
## 创建时间: YYYY-MM-DD HH:mm

| Phase | 状态 | 完成时间 |
|-------|------|---------|
| 0 需求理解 | ✓ | HH:mm |
| 1 产品设计 | ✓ | HH:mm |
| 2 交互设计 | 进行中 | - |
| 3 技术评审 | 待执行 | - |
| ... | | |

## 下一步
Phase 2: 等待 Designer 产出

## 备注
[任何需要记录的上下文]
```

## 复杂需求拆解

当需求过大时（Leader Phase 0 判断），拆为多个迭代:

```markdown
# brief.md

## 迭代拆解
1. 迭代1: [最小可用功能] ← 当前执行
2. 迭代2: [增强功能]
3. 迭代3: [完善功能]

每个迭代独立走一遍完整流水线。
```
