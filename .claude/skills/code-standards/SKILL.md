---
name: code-standards
description: |
  城际云代码规范 Skill — 以阿里巴巴 Java 开发手册（泰山版）及阿里巴巴前端规约为核心。
  当用户编写、审查或重构 Java/TypeScript/SQL 代码时自动加载。
  触发关键词：代码规范、阿里巴巴规范、命名约定、代码格式、注释规范、日志规范、
  异常处理、Java规范、TypeScript规范、SQL规范、naming、format、lint。
---

# 城际云代码规范 Skill

> 📖 **核心依据**: 阿里巴巴 Java 开发手册（泰山版）· 阿里巴巴前端规约（TypeScript 篇）· 阿里巴巴 MySQL 规约

## 使用场景
- 编写新的 Java / TypeScript / SQL 代码
- 代码审查时检查规范性
- 重构旧代码以符合阿里巴巴规范
- 新人入职学习团队编码风格

## 快速索引

| 规范类别 | 核心参考 | 详细参考 |
|----------|----------|----------|
| Java 规范 | 阿里巴巴 Java 开发手册（泰山版） | [java-style.md](./references/java-style.md) |
| TypeScript/React 规范 | 阿里巴巴前端规约 + Airbnb | [react-style.md](./references/react-style.md) |
| SQL 规范 | 阿里巴巴 MySQL 规约（适配 PG） | [sql-style.md](./references/sql-style.md) |

---

## 通用规范（来自阿里巴巴手册）

### 命名约定

| 类型 | 规则 | 正确示例 | 错误示例 |
|------|------|----------|----------|
| Java 类 | UpperCamelCase | `OrderService` | `order_service` |
| Java 方法 | lowerCamelCase | `getUserById()` | `get_user_by_id()` |
| Java 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` | `maxRetryCount` |
| Java 包名 | 全小写，点分隔 | `com.chengjiyun.order` | `com.chengjiYun.Order` |
| TS 组件 | UpperCamelCase | `UserProfile` | `userProfile` |
| TS 函数/Hook | lowerCamelCase | `useUserData` | `use_user_data` |
| TS 类型/接口 | UpperCamelCase + I/T 前缀 | `IUserProps`, `TOrderStatus` | `userPropsType` |
| 数据库表 | 小写蛇形，复数 | `user_accounts` | `UserAccounts` |
| 数据库列 | 小写蛇形，单数 | `created_at` | `CreatedAt` |
| 索引名 | `idx_表名_列名` | `idx_orders_user_id` | `orders_user_id_idx` |

### 代码格式

| 规则 | 前端 (TS/TSX) | 后端 (Java) |
|------|---------------|-------------|
| 缩进 | 2 空格 | 4 空格 |
| 行宽上限 | 100 字符 | 120 字符 |
| 换行符 | LF (Unix) | LF (Unix) |
| 文件末尾 | 有且仅有一个空行 | 有且仅有一个空行 |
| 大括号 | K&R 风格 | K&R 风格 |

### 注释规范

- 所有 public 方法必须有 Javadoc / JSDoc（含 `@param`、`@return`、`@throws`）
- 复杂业务逻辑必须有行内注释，说明"为什么这样做"而非"做了什么"
- 禁止注释掉的代码块提交到仓库——直接删除，依赖 Git 历史
- TODO / FIXME 必须标注负责人和日期：`// TODO(zhangsan): 2026-07-31 补充幂等处理`

### 日志规范

- ✅ 使用 SLF4J (Java) / 统一 logger (TS) 的参数占位符
- ❌ 禁止 `System.out.println()` 或 `console.log()` 用于业务日志
- ❌ 禁止字符串拼接日志信息
- ❌ 禁止记录敏感信息（密码、Token、身份证号、手机号、银行卡号）

### 异常处理规范

- ✅ 抛出自定义业务异常，携带错误码和上下文
- ❌ 禁止空 catch 块吞掉异常（至少记录日志）
- ❌ 禁止使用异常做业务控制流
- ❌ 禁止在 Controller 层捕获异常后返回 200 OK（应使用全局异常处理器）
- ❌ 禁止 `catch (Exception e)` 捕获所有异常（应捕获具体异常类型）

### 禁止事项（强制）

| 类别 | 禁止项 |
|------|--------|
| Java | 禁止在循环中使用 `+` 拼接字符串（用 `StringBuilder`） |
| Java | 禁止将 `SimpleDateFormat` 定义为 `static` 变量 |
| Java | 金额字段禁止使用 `float`/`double`（必须 `BigDecimal`） |
| Java | 禁止 `Map` 的 Key 使用可变对象 |
| Java | POJO 类必须重写 `toString()` 方法 |
| TS | 禁止 `any` 类型 |
| TS | 禁止 `console.log` 提交到主分支 |
| SQL | 禁止 `SELECT *`（必须显式列出字段） |
| SQL | 禁止使用 `COUNT(常量)` 代替 `COUNT(*)` |
| SQL | 禁止在 WHERE 条件中对列使用函数 |
| 通用 | 禁止硬编码密钥/Token/密码/连接字符串 |
| 通用 | 禁止 Entity/VO 直接暴露到 Controller 层 |
