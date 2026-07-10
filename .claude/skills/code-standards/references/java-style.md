# Java 代码规范 — 阿里巴巴 Java 开发手册（泰山版）核心摘要

> 📖 完整手册请参阅：阿里巴巴 Java 开发手册（泰山版）· IDEA 插件：Alibaba Java Coding Guidelines (p3c)

## 技术栈

- Java 21 LTS + Spring Boot 3.x
- 构建: Gradle 8.x (Groovy DSL)
- ORM: MyBatis Plus 3.5
- 代码格式化: Spotless (Google Java Format)
- 代码检查: p3c-pmd + SonarQube (alibaba-sonar-profile)

---

## 一、编程规约

### 1.1 命名风格

| 元素 | 规则 | 示例 |
|------|------|------|
| 类名 | UpperCamelCase | `OrderService`, `UserController` |
| 方法名 | lowerCamelCase | `getOrderById()`, `createPayment()` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| 包名 | 全小写 | `com.chengjiyun.order.service` |
| 抽象类 | Abstract 或 Base 开头 | `AbstractBaseService` |
| 异常类 | Exception 结尾 | `OrderNotFoundException` |
| 测试类 | 被测类名 + Test | `OrderServiceTest` |
| POJO 布尔变量 | 禁止 is 前缀（防止序列化问题） | `deleted` 而非 `isDeleted` |
| 接口 | 不加 I 前缀（与实现类区分） | `UserService` + `UserServiceImpl` |
| 枚举 | Enum 结尾 | `OrderStatusEnum` |

### 1.2 常量定义

- 跨应用共享常量放二方库 `constant` 目录
- 应用内共享常量放 `constant` 包
- 类内共享常量 `private static final`
- 禁止魔法值（-1、0、1、""、null 除外）

### 1.3 代码格式

- 大括号：K&R 风格（左括号不换行，右括号独占一行）
- 小括号：if/for/while 后必须跟空格
- 运算符：两侧各一个空格（`a = b + c`）
- 缩进：4 个空格（禁止 Tab 字符混用）
- 单行：不超过 120 字符
- 空行：不同逻辑/语义/业务段之间插入空行

### 1.4 OOP 规约

- **禁止**：通过一个类的对象引用访问其静态变量/方法（增加解析成本）
- **强制**：所有重写方法加 `@Override` 注解
- **强制**：POJO 类必须重写 `toString()` 方法
- **推荐**：使用 `StringBuilder` 的 `append` 方法在循环中拼接字符串
- **强制**：`Object.equals` 调用时，常量/确定值在前（避免 NPE）

### 1.5 集合处理

- **强制**：使用 `isEmpty()` 而非 `size() == 0` 判断集合是否为空
- **强制**：使用 `Map.entrySet()` 遍历 Key-Value（而非先 `keySet()` 再 `get()`）
- **强制**：`Map` 的 Key 必须使用不可变对象（String、Integer 等）
- **强制**：`Collections.emptyList()` 返回的集合禁止执行 `add` 操作
- **推荐**：集合初始化时指定容量（避免频繁扩容）

### 1.6 并发处理

- **强制**：`SimpleDateFormat` 禁止定义为 `static` 变量（非线程安全）——使用 `DateTimeFormatter`
- **强制**：线程池不允许使用 `Executors` 创建——通过 `ThreadPoolExecutor` 显式指定参数
- **强制**：高并发场景下，`HashMap` 初始化时必须指定容量（避免死链风险）
- **推荐**：使用 `ThreadLocal` 时务必调用 `remove()` 清理（防止内存泄漏）

---

## 二、异常日志

### 2.1 异常处理

- **强制**：禁止 `catch (Exception e)` 捕获所有异常
- **强制**：异常不应用于流程控制或条件判断
- **强制**：catch 块中必须处理异常（至少记录日志），禁止空 catch 块
- **推荐**：定义业务异常类 `BusinessException`，携带错误码
- **强制**：Controller 层使用全局异常处理器（`@RestControllerAdvice`），禁止 try-catch 返回 200

### 2.2 日志规约

- **强制**：使用 SLF4J 门面 + Logback 实现
- **强制**：禁止 `System.out.println()` 和 `System.err.println()`
- **强制**：占位符方式：`log.info("订单创建, orderId={}, amount={}", id, amount)`
- **禁止**：日志中记录敏感信息

---

## 三、单元测试

- **强制**：所有 Service 层 public 方法必须有单元测试
- **推荐**：覆盖率 >= 80%（核心模块 >= 90%）
- **强制**：测试用例必须包含边界条件、异常路径、并发场景

---

## 四、分层架构

```
Controller → Service(接口) → ServiceImpl → Mapper → Database
    ↕              ↕
  VO/DTO          Entity
```

- **Controller**：参数校验、VO 转换、调用 Service（不写业务逻辑）
- **Service**：业务逻辑、事务管理 (`@Transactional`)
- **Mapper**：数据访问（MyBatis Plus `BaseMapper<T>`）
- **禁止**：Controller 中直接调用 Mapper
- **禁止**：Service 层之间循环依赖

### DTO/VO/Entity 分离

| 对象 | 用途 | 禁止 |
|------|------|------|
| Entity | 数据库映射 | 禁止暴露到 Controller |
| DTO | 服务间传输 | 禁止包含业务逻辑 |
| VO | 前端响应 | 禁止包含数据库字段 |
| Query | 查询参数 | 禁止复用为 VO |
