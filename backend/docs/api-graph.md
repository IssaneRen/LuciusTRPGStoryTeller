# Graph API Documentation

图数据 CRUD API 接口文档。

## 数据结构

### Graph
```json
{
  "id": "clue-demo",
  "name": "示例线索图",
  "type": "clue",
  "nodes": [
    {
      "id": "n1",
      "label": "起始事件",
      "description": "故事从这里开始",
      "tags": ["main", "ch1"],
      "x": 0,
      "y": 0
    }
  ],
  "edges": [
    {
      "id": "e1",
      "source": "n1",
      "target": "n2",
      "label": ""
    }
  ]
}
```

## 公开端点（无需认证）

### GET /api/graphs

获取所有图的列表。

**Response:**
```json
{
  "code": 200,
  "data": [
    {
      "id": "clue-1",
      "name": "主线",
      "type": "clue",
      "nodeCount": 12
    }
  ]
}
```

### GET /api/graphs/:id

获取完整的图数据。

**Response:**
```json
{
  "code": 200,
  "data": {
    "id": "clue-demo",
    "name": "示例线索图",
    "type": "clue",
    "nodes": [...],
    "edges": [...]
  }
}
```

**Error Response:**
```json
{
  "code": 404,
  "message": "graph not found"
}
```

## 管理员端点（需要 Admin Token）

所有写操作需要在 Header 中携带 Admin Token:
```
Authorization: Bearer <admin-token>
```

### POST /api/graphs

创建新图。

**Request:**
```json
{
  "name": "新图",
  "type": "clue"
}
```

**Response:**
```json
{
  "code": 201,
  "data": {
    "id": "clue-a1b2c3"
  }
}
```

### PUT /api/graphs/:id

保存完整图数据（全量覆盖）。

**Request:**
```json
{
  "name": "更新后的图",
  "type": "clue",
  "nodes": [
    {
      "id": "n1",
      "label": "节点1",
      "description": "描述",
      "tags": ["tag1"],
      "x": 0,
      "y": 0
    }
  ],
  "edges": [
    {
      "id": "e1",
      "source": "n1",
      "target": "n2",
      "label": "连接"
    }
  ]
}
```

**Response:**
```json
{
  "code": 200,
  "message": "saved"
}
```

### DELETE /api/graphs/:id

删除图。

**Response:**
```json
{
  "code": 200,
  "message": "deleted"
}
```

**Error Response:**
```json
{
  "code": 404,
  "message": "graph not found"
}
```

## 测试

### 1. 获取图列表（无需认证）

```bash
curl http://localhost:8080/api/graphs
```

### 2. 获取单个图（无需认证）

```bash
curl http://localhost:8080/api/graphs/clue-demo
```

### 3. 创建新图（需要 admin token）

先获取 admin token:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your-password"}' \
  | jq -r '.data.token')
```

创建图:
```bash
curl -X POST http://localhost:8080/api/graphs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"测试图","type":"mod"}'
```

### 4. 更新图（需要 admin token）

```bash
curl -X PUT http://localhost:8080/api/graphs/clue-demo \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d @- <<'EOF'
{
  "name": "更新后的示例",
  "type": "clue",
  "nodes": [
    {
      "id": "n1",
      "label": "新节点",
      "description": "描述",
      "tags": ["test"],
      "x": 0,
      "y": 0
    }
  ],
  "edges": []
}
EOF
```

### 5. 删除图（需要 admin token）

```bash
curl -X DELETE http://localhost:8080/api/graphs/clue-demo \
  -H "Authorization: Bearer $TOKEN"
```

## 安全性

- ID 验证: 只允许 `[a-z0-9-]` 字符，防止路径遍历攻击
- 写操作保护: 所有 POST/PUT/DELETE 操作需要 Admin JWT Token
- 读操作公开: GET 操作无需认证，可公开访问

## 存储实现

- 存储位置: `backend/data/graphs/`
- 文件格式: JSON，每张图一个文件（如 `clue-demo.json`）
- ID 生成规则: `{type}-{6位随机16进制}` (如 `clue-a1b2c3`)
