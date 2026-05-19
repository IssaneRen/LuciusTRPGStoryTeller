# Full Delivery Skill 设计规格

## 定位

用户说"开始开发"或"全链路" + 需求描述 → 自动走完全流水线交付产品功能。

## 触发条件

- "开始开发"
- "全链路"

## 执行流水线

```
Phase 0: 需求理解 (Leader)         → brief.md
Phase 1: 产品设计 (PM)             → prd.md
Phase 2: 交互视觉设计 (Designer)   → design.md + Figma 链接
Phase 3: 技术评审 (Consultant)     → contract.md (API契约)
Phase 4: 并行开发 (Workers)        → 代码
Phase 5: 集成验证 (QA)             → test-report.md
Phase 6: 代码审查 (Consultant)     → review.md
Phase 7: 交付 (Leader)             → commit + 汇报
```

## 核心规则

### 不卡死原则

- 所有阶段默认自动推进，不等待用户确认
- 每阶段完成后输出一行汇报 + 产物路径
- 用户想干预时主动打断，修改产物后说"继续"恢复

### 智能跳过

Leader 在 Phase 0 评估复杂度后决定:
- 简单需求（单文件/文档）: Leader 直接完成，不走流水线
- 中等需求（单端改动）: 跳过 Phase 1 和 Phase 2
- 复杂需求（跨端功能）: 走完整流水线

### 接口契约优先

- Phase 3 由 Consultant 基于 PRD + 设计稿产出 contract.md
- contract.md 包含: API 路径、请求/响应结构、状态码、前端需要的字段
- Phase 4 前后端并行开发时，严格按契约实现，不互相依赖

## 中间产物

```
.tasks/
└── YYYY-MM-DD-<slug>/
    ├── brief.md        # Phase 0: 需求摘要 + 复杂度
    ├── prd.md          # Phase 1: 精简 PRD
    ├── design.md       # Phase 2: 设计说明 + Figma 链接
    ├── contract.md     # Phase 3: API 契约 + 数据模型
    ├── progress.md     # 实时进度（当前阶段 + 已完成列表）
    ├── test-report.md  # Phase 5: 测试结果
    └── review.md       # Phase 6: 审查意见
```

## 打断与恢复

1. 每阶段产出立即落盘到 `.tasks/<id>/`
2. `progress.md` 记录已完成阶段和下一步
3. 用户打断后修改任意产物文件
4. 说"继续"时 skill 读取 progress.md 从断点恢复
5. 如果契约被修改，标记已完成的代码需重新对齐

## Agent 调度表

| Phase | Agent | 方式 | 输入 | 输出 |
|-------|-------|------|------|------|
| 0 | Leader | 主对话 | 用户需求 | brief.md |
| 1 | PM | subagent | brief.md | prd.md |
| 2 | Designer | subagent | prd.md | design.md + Figma |
| 3 | Consultant | subagent | prd.md + design.md | contract.md |
| 4a | Backend Dev | subagent (并行) | contract.md | 后端代码 |
| 4b | Web Dev | subagent (并行) | contract.md + design.md | 前端代码 |
| 5 | QA | subagent | 代码 + contract.md | test-report.md |
| 6 | Consultant | subagent | 代码 + contract.md | review.md |
| 7 | Leader | 主对话 | 全部产物 | commit + 汇报 |

## 汇报格式

每阶段完成后输出:

```
✓ Phase N: [阶段名] 完成
  产物: .tasks/<id>/<file>.md
  摘要: [一句话核心结论]
  [如有问题]: ⚠️ [问题描述]
```

## 最终交付汇报

```
## 交付完成

### 变更摘要
- [一句话描述做了什么]

### 文件变更
- 新增: [列表]
- 修改: [列表]

### 验证方式
- [如何验证功能正常]

### 产物归档
- .tasks/<id>/ 保留完整过程记录
```

## 边界处理

- 需求过大时: Leader 在 Phase 0 拆为多个迭代，每个迭代独立走一遍流水线
- PM/Designer 产出不合格: Consultant 在 Phase 3 打回并标注问题，自动回到对应阶段重做
- 开发与契约不一致: QA 在 Phase 5 发现后，标记具体不一致点，自动通知对应 agent 修复
- 审查不通过: Phase 6 Consultant 打回，回到 Phase 4 修复后重新提交
