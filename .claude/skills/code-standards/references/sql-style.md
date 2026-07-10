# SQL 代码规范

## 数据库

- PostgreSQL 15
- 缓存: Redis 7
- 迁移工具: Flyway

## 命名约定

| 对象 | 规则 | 示例 |
|------|------|------|
| 表名 | 小写蛇形，复数 | `user_accounts`, `order_records` |
| 列名 | 小写蛇形，单数 | `created_at`, `order_status` |
| 主键 | `id` (UUID 或 BIGINT) | `id` |
| 外键 | `<关联表单数>_id` | `user_id`, `order_id` |
| 索引 | `idx_<表>_<列>` | `idx_orders_user_id` |
| 唯一约束 | `uq_<表>_<列>` | `uq_users_email` |
| 序列 | `<表>_id_seq` | `orders_id_seq` |

## 编写规范

- 关键字大写: `SELECT`, `FROM`, `WHERE`, `JOIN`
- 每个子句独立一行
- 复杂查询必须有注释说明业务含义
- 禁止 `SELECT *`——显式列出所需列
- 所有 SQL 变更必须通过 Flyway Migration 执行
- 禁止直接在代码中拼接 SQL 字符串——使用 MyBatis XML 或参数化注解

## 索引规范

- 每个外键列必须有索引
- WHERE / JOIN / ORDER BY 中频繁使用的列建立索引
- 避免在索引列上使用函数或计算
- 定期用 `EXPLAIN ANALYZE` 审查慢查询
