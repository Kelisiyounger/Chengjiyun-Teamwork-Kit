# 城际云 — 团队共享 AI 行为指令

> **提交到 Git** | 全团队生效 | 版本: 1.0.0
> 个人覆盖请编辑 `CLAUDE.local.md`（已被 git 忽略）

你必须在推理前主动遵守以下规则。涉及任何一条时，**直接拒绝并说明原因**，不提供替代方案。

---

## 一、禁止操作（主动拒绝，不执行、不建议、不规划）

### 破坏性 Git
- `git push --force` / `-f` 到 main/master
- `git reset --hard`、`git clean -fd`

### 破坏性文件/系统
- `rm -rf`、`chmod 777`、`sudo`
- `shutdown`、`reboot`、`setx`、`reg`

### 数据外泄
- `curl`、`wget`、`scp`、`ssh`（向外部传数据）

### Docker 破坏
- `docker rm -f`、`docker system prune`

### 发布到公共仓库
- `npm publish`、`pnpm publish`、`yarn publish`、`gradle publish`

### 未授权运行时/包管理器
- `pip install`、`python`、`py`
- `gem install`、`cargo`、`go`
- `choco install`、`winget install`

### 敏感区域读取
- `C:\Windows\**`、`/etc/**`

---

## 二、技术栈约束

城际云统一技术栈，禁止使用非授权语言/框架：

| 层级 | 允许 | 禁止 |
|------|------|------|
| 后端 | Java 21 (LTS) + Spring Boot 3.x | Kotlin / Scala / Groovy（业务代码） |
| 前端 | TypeScript 5.4 + React 18 | CoffeeScript / Flow / PureScript |
| 数据库 | PostgreSQL 15 | — |
| 构建 | Gradle 8.x (Groovy DSL) / pnpm 9.x | — |
| 脚本 | Bash | Python / Ruby / Perl / Lua（未经架构组审批） |

---

## 三、编码规范（强制遵守）

### Java（阿里巴巴 Java 开发手册 泰山版）
- 命名：类名 PascalCase，方法 camelCase，常量 UPPER_SNAKE
- 分层：Controller → Service → Mapper（禁止逆向调用）
- DO / VO / DTO 严格分离，禁止混用
- 金额字段必须用 `BigDecimal`，禁止 `float` / `double`
- 禁止在循环中使用 `+` 拼接字符串（用 `StringBuilder`）
- POJO 类必须重写 `toString()`
- `SimpleDateFormat` 禁止定义为 static
- 集合判空用 `isEmpty()` 而非 `size() == 0`
- SQL 必须参数化，禁止字符串拼接

### TypeScript（阿里巴巴前端规约）
- 禁止 `any` 类型（严格模式）
- 组件必须定义 Props 接口：`I` + 组件名 + `Props`
- 禁止 prop drilling 超过 2 层，超过使用 Zustand
- API 调用统一经 `services/` 层
- `useEffect` 依赖数组必须完整

### SQL（阿里巴巴 MySQL 规约 + PostgreSQL 适配）
- 表名 / 列名小写蛇形命名
- 必须有 `id`、`create_time`、`update_time` 三个字段
- 禁止 `SELECT *`
- 禁止使用 `COUNT(常量)` 代替 `COUNT(*)`
- UNIQUE 索引前缀 `UK_`，普通索引前缀 `idx_`

---

## 四、工作流要求

### Git 提交
- 提交信息使用中文，格式：`类型: 简述`（如 `feat: 新增用户登录接口`）
- commit 末尾附带 `Co-Authored-By: Claude <noreply@anthropic.com>`

### 代码审查前
- 确保通过 lint 检查（Java: p3c-pmd / TypeScript: ESLint + @alibaba/eslint-config-ali）
- 确保所有测试通过

---

## 五、输出风格

Proactive（主动执行）。安全规则优先级更高——一旦触及上述禁止操作，Proactive 自动让位给拒绝。

---

## 六、环境信息

- API 端点：`https://api.deepseek.com/anthropic`
- 默认模型：`deepseek-v4-pro`（对话）/ `deepseek-v4-flash`（轻量任务）
- 工作语言：中文（代码和注释可使用英文）
