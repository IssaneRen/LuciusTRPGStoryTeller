# 图编辑器功能

已完成管理后台的图编辑器功能集成。

## 文件清单

### API 层
- `/src/api/graph.ts` - 图 API 调用封装

### 视图层
- `/src/views/graph/list.vue` - 图列表页，支持创建、删除和导航到编辑器
- `/src/views/graph/editor.vue` - 图编辑器主界面，基于 Vue Flow

### 组件层
- `/src/components/graph/PropertyPanel.vue` - 节点属性编辑面板

### 路由配置
- `/src/router/index.ts` - 新增 `/graph` 和 `/graph/:id/edit` 路由
- `/src/layouts/default.vue` - 侧边栏新增"图管理"菜单

## 依赖
- `@vue-flow/core` - Vue 3 流程图库
- `@dagrejs/dagre` - 自动布局算法（已安装但未启用，可后续扩展）

## 功能特性

### 列表页
- 展示所有图：名称、类型、节点数、边数
- 新建图：弹窗输入名称和类型（线索/模组）
- 删除图：二次确认
- 点击编辑进入编辑器

### 编辑器
- 可视化画布：拖拽节点、创建连线
- 双击空白处创建新节点
- 从节点 handle 拖出连线到另一个节点
- 点击节点显示属性面板
- 属性面板支持编辑：名称、描述、标签
- 删除节点：属性面板中的删除按钮
- 保存：全量 PUT 到后端

## 技术实现

- 严格使用 Composition API + `<script setup lang="ts">`
- 使用 Naive UI 组件库
- 数据流：Vue Flow 内部状态 ↔ 本地 ref ↔ 后端 API
- 样式：UnoCSS utility classes

## 构建状态

- Vite 构建成功（1.86s）
- TypeScript 严格模式检查通过（跳过第三方库检查）
- 无运行时错误

## 后续扩展建议

1. 使用 dagre 实现自动布局功能
2. 支持边的标签和样式
3. 撤销/重做功能
4. 图的导出（JSON/PNG）
5. 小地图导航
6. 节点搜索和高亮
