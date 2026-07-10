# Git 工作流与 Commit 规范

## 分支策略

```
main (受保护)
  ├── develop (日常集成分支)
  │     ├── feature/xxx   (功能开发)
  │     ├── fix/xxx       (Bug 修复)
  │     └── refactor/xxx  (重构)
  ├── release/x.x.x       (发布分支)
  └── hotfix/xxx          (紧急修复)
```

## 分支命名

| 类型 | 格式 | 示例 |
|------|------|------|
| 功能 | `feature/<描述>` | `feature/user-login` |
| 修复 | `fix/<问题>` | `fix/order-timezone` |
| 重构 | `refactor/<模块>` | `refactor/payment-flow` |
| 发布 | `release/<版本号>` | `release/2.1.0` |
| 紧急 | `hotfix/<问题>` | `hotfix/critical-npe` |

## Commit 规范 (Conventional Commits)

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `refactor` | 重构（非功能、非修复） |
| `docs` | 文档变更 |
| `style` | 格式调整（不影响逻辑） |
| `test` | 测试相关 |
| `chore` | 构建/工具/依赖变更 |
| `perf` | 性能优化 |

### 示例

```bash
feat(order): 支持批量取消订单

- 新增 POST /api/v1/orders/batch-cancel
- 添加幂等性校验
- 补充单元测试

Closes #1234
```

## PR 流程

1. 从 `develop` 创建 feature 分支
2. 开发完成后推送分支
3. 在 GitLab 创建 Merge Request → `develop`
4. 至少 1 人 Approve 后方可合并
5. 使用 Squash Merge，保持 main 分支历史干净

## .gitignore 基础配置

```gitignore
# IDE
.idea/
*.iml
.vscode/
*.swp

# 构建产物
build/
target/
dist/
node_modules/

# 环境变量
.env
.env.local
*.env

# Claude Code
.claude/settings.local.json
.claude/sessions/
.claude/telemetry/
.claude/file-history/

# 系统
.DS_Store
Thumbs.db
```
