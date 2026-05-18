# 开发环境配置

## 必需工具

| 工具 | 版本 | 安装方式 |
|------|------|---------|
| Go | 1.22+ | https://go.dev/dl/ |
| Node.js | 20+ | https://nodejs.org/ |
| pnpm | 9+ | `npm install -g pnpm` |

## 可选工具

| 工具 | 用途 | 安装方式 |
|------|------|---------|
| Air | Go 热重载 | `go install github.com/air-verse/air@latest` |
| Docker | 容器化部署 | https://docs.docker.com/get-docker/ |

## 首次初始化

```bash
# 安装后端依赖
cd backend && go mod download

# 安装前端依赖
cd frontend && pnpm install
```

或使用 Makefile:

```bash
make install
```

## 环境变量

后端支持以下环境变量 (均有默认值，开发时无需配置):

| 变量 | 默认值 | 说明 |
|------|--------|------|
| PORT | 8080 | 服务端口 |
| JWT_SECRET | dev-secret | JWT 签名密钥 |
| DB_PATH | ./data/app.db | SQLite 数据库路径 |
| GIN_MODE | debug | Gin 运行模式 |

## 目录约定

- 后端代码: `backend/`
- 前端代码: `frontend/`
- 数据库文件: `backend/data/app.db` (自动创建，已 gitignore)
