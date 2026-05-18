# 30 秒快速开始

## 前置要求

- Go 1.22+
- Node.js 20+ / pnpm
- (可选) Air — Go 热重载

## 一键启动开发环境

```bash
make dev
```

## 分别启动

```bash
# 后端 (默认 :8080)
make dev-backend

# 前端 (默认 :5173)
make dev-frontend
```

## 构建生产包

```bash
make build
```

产出单个二进制文件 `backend/bin/server`，内嵌前端静态资源。

## 验证

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```
