# 测试规范

> 此文件为 `CLAUDE.md` 的模块化扩展，Claude Code 自动加载。

---

## 测试策略

| 层级 | 框架 | 覆盖率要求 | 执行频率 |
|------|------|-----------|----------|
| 单元测试 | JUnit 5 / Vitest | ≥ 80% | 每次提交 |
| 集成测试 | Spring Boot Test / Supertest | ≥ 60% | 每次 PR |
| E2E | Playwright / Cypress | 核心流程 | 每次发布 |

## Java 测试

### 单元测试
```java
// 命名: 方法名_场景_预期结果
@Test
void findById_WhenExists_ReturnsUser() { ... }

@Test
void findById_WhenNotExists_ThrowsException() { ... }
```

### 要求
- 每个 Service 方法至少 2 个测试用例（正常 + 异常）
- Mock 外部依赖，不启动 Spring 容器（单元测试）
- 断言使用 AssertJ（流式断言）
- 禁止 `System.out.println` 验证结果

## TypeScript 测试

### 组件测试
```typescript
describe('UserProfile', () => {
  it('renders user name when loaded', () => { ... })
  it('shows error state when fetch fails', () => { ... })
  it('calls onEdit when edit button clicked', () => { ... })
})
```

### 要求
- 组件测试覆盖: 渲染 / 交互 / 错误状态
- API 层使用 MSW mock
- 使用 Testing Library 的 `screen` 和 `userEvent`

## 代码审查前检查

- [ ] `npm test` / `./gradlew test` 全部通过
- [ ] 新增代码有对应测试
- [ ] 无 skipped / disabled 测试遗留
