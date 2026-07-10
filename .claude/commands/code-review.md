# /project:code-review — 团队代码审查

按照城际云阿里巴巴规范对当前变更进行代码审查。

## 审查维度

按优先级依次检查：

1. **安全性** — SQL 注入、XSS、敏感信息泄露、权限校验
2. **正确性** — 边界条件、空指针、并发安全、事务完整性
3. **性能** — N+1 查询、循环内 IO、缓存策略
4. **规范** — 阿里巴巴 Java/TS/SQL 规约逐条检查
5. **业务** — 需求符合性、异常处理完整性

## 输出格式

| 级别 | 说明 |
|------|------|
| 🔴 严重 | 必须修复（安全漏洞、数据风险） |
| 🟡 建议 | 推荐修复（性能、规范） |
| 🟢 亮点 | 值得保留的好实践 |

## 检查清单

### Java（16 项）
- [ ] Controller 不包含业务逻辑
- [ ] DTO/VO/Entity 严格分离
- [ ] 金额使用 BigDecimal
- [ ] POJO 重写 toString()
- [ ] 循环中无 + 拼接字符串
- [ ] SimpleDateFormat 非 static
- [ ] 集合 isEmpty() 代替 size()==0
- [ ] SQL 参数化
- [ ] 异常不用于流程控制
- [ ] 日志使用 SLF4J 占位符
- [ ] 禁止 System.out.println
- [ ] equals 比较常量在前
- [ ] 包装类比较使用 equals
- [ ] 禁止 catch Exception
- [ ] 命名符合 PascalCase/camelCase
- [ ] 方法单一职责

### TypeScript（7 项）
- [ ] 禁止 any 类型
- [ ] Props 接口定义（I + 组件名 + Props）
- [ ] 无 prop drilling 超过 2 层
- [ ] API 调用经 services/ 层
- [ ] useEffect 依赖数组完整
- [ ] 无 console.log
- [ ] 组件单一职责

### SQL（6 项）
- [ ] 禁止 SELECT *
- [ ] 必须有 id/create_time/update_time
- [ ] 表名列名小写蛇形
- [ ] UNIQUE 索引 UK_ 前缀
- [ ] 普通索引 idx_ 前缀
- [ ] 禁止 COUNT(常量)
