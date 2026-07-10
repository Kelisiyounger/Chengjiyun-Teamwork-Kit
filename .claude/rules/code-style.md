# 代码风格规范

> 此文件为 `CLAUDE.md` 的模块化扩展，Claude Code 自动加载 `.claude/rules/` 下所有 `.md` 文件。

---

## Java（阿里巴巴 Java 开发手册 泰山版）

### 命名
- 类名: `PascalCase`
- 方法/变量: `camelCase`
- 常量: `UPPER_SNAKE_CASE`
- 包名: 全小写，点分隔

### 分层架构
```
Controller → Service → Mapper（禁止逆向调用）
    ↓           ↓         ↓
   DTO         BO        DO
```
- Controller: 参数校验 + 视图转换，不含业务逻辑
- Service: 业务逻辑 + 事务管理
- Mapper: 数据访问，单表操作

### 关键规则
- 金额字段必须 `BigDecimal`，禁止 `float`/`double`
- POJO 必须重写 `toString()`
- 循环中禁止 `+` 拼接字符串（用 `StringBuilder`）
- `SimpleDateFormat` 禁止定义为 `static`
- 集合判空: `isEmpty()` 而非 `size() == 0`
- SQL 必须参数化，禁止字符串拼接
- equals 比较: 常量在前 (`"OK".equals(str)`)
- 包装类比较: 使用 `equals()`，不用 `==`

---

## TypeScript（阿里巴巴前端规约）

### 类型系统
- 禁止 `any`（strict 模式）
- Props 接口: `I` + 组件名 + `Props`
- 状态管理: prop drilling ≤ 2 层，超过使用 Zustand

### 架构
- API 调用统一经 `services/` 层
- 组件: `components/` (复用) + `pages/` (页面)
- Hooks: `hooks/` (自定义 Hook)

### 关键规则
- `useEffect` 依赖数组必须完整
- 禁止 `console.log`（生产代码）
- 条件渲染优先用 `&&` 或三元表达式

---

## SQL（阿里巴巴 MySQL 规约 + PostgreSQL 15 适配）

### 表设计
- 命名: 小写蛇形 (`user_order`)
- 必有三字段: `id`, `create_time`, `update_time`
- 索引: `UK_` (唯一), `idx_` (普通)

### 查询
- 禁止 `SELECT *`
- 禁止 `COUNT(常量)` 代替 `COUNT(*)`
- 参数化查询，禁止拼接
