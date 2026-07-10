# /project:deploy — 项目部署检查清单

在部署前执行完整的部署检查。

## 部署检查清单

### 代码质量
- [ ] `npm run lint` / `./gradlew check` 通过
- [ ] `npm test` / `./gradlew test` 全部通过
- [ ] 代码审查已完成（参考 `/project:code-review`）
- [ ] 无 TODO/FIXME 遗留（或已记录为 Issue）

### 数据库
- [ ] 迁移脚本已准备（如有 Schema 变更）
- [ ] 回滚方案已就绪
- [ ] 敏感数据已脱敏处理

### 配置
- [ ] 环境变量已更新（`.env.example` 已同步）
- [ ] API 端点配置正确
- [ ] CORS 白名单已更新

### 安全
- [ ] 无硬编码密钥/密码
- [ ] API 参数校验完整
- [ ] SQL 使用参数化查询

### 部署
- [ ] CHANGELOG 已更新
- [ ] 版本号已递增
- [ ] Git tag 已创建

## 常见部署命令

```bash
# 前端构建
npm run build

# 后端构建
./gradlew build -x test

# Docker 构建
docker compose build

# 启动服务
docker compose up -d

# 查看日志
docker compose logs -f
```
