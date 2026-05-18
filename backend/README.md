# 后端服务

> Go + Gin 轻量鉴权服务

## 状态: MVP 已实现

API 端点:
- `POST /api/auth/register` — 用户注册
- `POST /api/auth/login` — 登录获取 token
- `GET /api/auth/me` — 获取当前用户信息 (需 Bearer token)

## 启动

```bash
cd backend && go run ./cmd/server
```

## 技术栈

- Go 1.22+ / Gin / golang-jwt/jwt v5 / bcrypt / glebarez/sqlite(GORM)
- 详见 [docs/decisions/0001-tech-stack.md](../docs/decisions/0001-tech-stack.md)
