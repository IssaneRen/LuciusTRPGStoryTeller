# 部署方案（国内低成本）

## 推荐方案对比

| 方案 | 年费 | 配置 | 备案 | 适合场景 |
|------|------|------|------|---------|
| 腾讯云轻量(新用户) | 79-99元 | 2C2G-4C4G | 需要 | 最高性价比 |
| 腾讯云香港轻量 | ~480元 | 2C1G-2C2G | 免备案 | 快速上线 |
| Serverless(SCF/FC) | 0-50元 | 按量 | 前端需要 | 极低流量 |
| Cloudflare Pages+海外VPS | ~$24 | 各异 | 免备案 | 纯海外 |

## 推荐: 腾讯云轻量 + Go embed 单二进制

### 为什么选这个

- Go embed 将前端打包进二进制，只需部署一个文件
- 无需 Nginx/Docker，直接运行
- 腾讯云新用户 2C2G 仅 99 元/年（续费同价活动）

### 部署步骤

```bash
# 1. 本地构建
make build

# 2. 上传到服务器
scp backend/bin/server user@your-server:/opt/app/

# 3. 服务器上运行
ssh user@your-server
chmod +x /opt/app/server
nohup /opt/app/server &
```

### Systemd 服务配置

```ini
# /etc/systemd/system/lucius.service
[Unit]
Description=Lucius TRPG Server
After=network.target

[Service]
ExecStart=/opt/app/server
WorkingDirectory=/opt/app
Restart=always
Environment=GIN_MODE=release
Environment=JWT_SECRET=your-secret-here

[Install]
WantedBy=multi-user.target
```

```bash
systemctl enable lucius
systemctl start lucius
```

## 备选: Serverless (极低流量)

适合日均 < 100 PV 的场景:
- 前端: 腾讯云 COS 静态托管 (几毛钱/月)
- 后端: 腾讯云 SCF (前3月免费，之后几块钱/月)
- 缺点: Go 冷启动约 200-500ms

## 域名与备案

- 大陆服务器必须备案（约 7-20 个工作日）
- 免备案: 选香港/海外服务器
- 域名: .com 约 55-75 元/年，.top 首年约 5 元
