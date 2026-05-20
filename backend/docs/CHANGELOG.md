# Changelog

## 2026-05-20

### Added - Graph CRUD API

新增图数据的完整 CRUD API 功能。

#### 新增文件
- `internal/store/graph.go` - 图数据 JSON 文件存储层
- `internal/store/graph_test.go` - 存储层单元测试
- `internal/handler/graph.go` - 图数据 HTTP 处理器
- `data/graphs/clue-demo.json` - 示例图数据
- `docs/api-graph.md` - API 接口文档

#### 修改文件
- `internal/router/router.go` - 注册图数据路由
- `cmd/server/main.go` - 启动时创建 data/graphs 目录

#### API 端点
**公开端点（无需认证）:**
- `GET /api/graphs` - 获取图列表
- `GET /api/graphs/:id` - 获取单个图数据

**管理员端点（需要 Admin Token）:**
- `POST /api/graphs` - 创建新图
- `PUT /api/graphs/:id` - 更新图数据（全量覆盖）
- `DELETE /api/graphs/:id` - 删除图

#### 技术实现
- 存储: JSON 文件，位于 `data/graphs/` 目录
- ID 生成: `{type}-{6位随机16进制}` (如 `clue-a1b2c3`)
- 安全: ID 仅允许 `[a-z0-9-]`，防止路径遍历
- 认证: 写操作使用现有 AdminAuth 中间件保护

#### 测试覆盖
- 单元测试: `internal/store/graph_test.go`
- 集成测试: 手动 curl 测试通过
- 代码检查: `go vet` 通过

详细 API 文档见: [docs/api-graph.md](./api-graph.md)
