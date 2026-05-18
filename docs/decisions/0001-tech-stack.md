# ADR-0001: 技术栈选型

## 状态: 已采纳

## 背景

单人全栈开发者 + 多 AI Agent 协作模式，需要选择对 AI agent 友好、轻量、易维护的技术栈。

## 决策

### 后端: Go + Gin

- AI 友好度: 8/10 (88.5k stars，训练数据最丰富的 Go 框架)
- 部署: 单二进制，无运行时依赖
- 适合场景: 轻量鉴权服务，不做复杂业务逻辑

### 前端: Vue 3 + Vite + TypeScript + UnoCSS

- AI 友好度: 7/10 (SFC 模板直观，模式明确)
- 跨端: 通过 uni-app 覆盖 12+ 小程序平台
- 组件库: 待定 (Naive UI / Element Plus / 自建)

### 知识库: Markdown 文件 + 多层 README 索引

- 无运行环境，纯文件存储
- AI agent 可通过 README 索引逐级导航
- 零部署成本

## 备选方案（已否决）

| 方案 | 否决理由 |
|------|---------|
| Java + Spring Boot | 对轻量鉴权过度工程化，JVM 资源占用大 |
| Go + Hertz | AI 训练数据不足 (仅7.2k stars)，agent 编码准确率低 |
| React + Next.js | AI 友好度更高但小程序跨端方案 (Taro) 不如 uni-app 生态 |
| Node.js + NestJS | TypeScript 一致性好但对"只做鉴权"场景过于复杂 |

## 影响

- 后端 Agent 需要掌握 Go + Gin 生态
- 前端 Agent 需要掌握 Vue 3 Composition API + Vite
- 知识库不需要专门的 Agent 维护，由 Researcher 产出
