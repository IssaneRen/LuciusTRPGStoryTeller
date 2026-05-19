.PHONY: help dev dev-backend dev-admin dev-web build install check fmt lint test test-backend clean

help: ## 显示所有可用命令
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-16s\033[0m %s\n", $$1, $$2}'

dev: ## 启动完整开发环境 (后端+管理后台+用户端)
	@echo "Backend :8080 | Admin :5173 | Web :3000"
	@make dev-backend & make dev-admin & make dev-web

dev-backend: ## 启动后端 API (:8080)
	@mkdir -p backend/cmd/server/dist && touch backend/cmd/server/dist/index.html
	cd backend && go run ./cmd/server

dev-admin: ## 启动管理后台 (:5173)
	cd frontend && pnpm dev

dev-web: ## 启动用户端展示 (:3000)
	cd web && pnpm dev

install: ## 安装所有依赖
	cd backend && go mod download
	cd frontend && pnpm install
	cd web && pnpm install

build: ## 构建生产包 (前端嵌入Go单二进制)
	cd frontend && pnpm build
	rm -rf backend/cmd/server/dist
	cp -r frontend/dist backend/cmd/server/dist
	cd backend && go build -o bin/server ./cmd/server

check: fmt lint test ## 提交前检查 (fmt + lint + test)

fmt: ## 格式化代码
	cd backend && gofmt -w .

lint: ## 静态检查
	cd backend && go vet ./...

test: test-backend ## 运行所有测试

test-backend: ## 运行后端测试
	cd backend && go test ./...

clean: ## 清理构建产物
	rm -rf backend/bin frontend/dist web/dist
