# TypeScript / React 代码规范

## 技术栈

- React 18 + TypeScript 5.4
- 状态管理: Zustand
- UI 组件库: Ant Design 5.x
- 构建: pnpm + Vite
- 格式化: Prettier
- Lint: ESLint + `@chengjiyun-dev/eslint-config-react`

## 组件架构

```
Pages → Features → Components → UI Kit
```

- **Pages**: 路由级别页面组件
- **Features**: 业务功能组件（组合多个 UI 组件实现完整功能）
- **Components**: 通用业务组件（可跨页面复用）
- **UI Kit**: Ant Design 二次封装的基础组件

## 组件规范

- 必须使用函数组件 + Hooks，禁止 Class 组件
- 每个组件必须定义 Props 接口（命名 `I<ComponentName>Props`）
- 禁止 prop drilling 超过 2 层——超过则抽取为 Context 或 Zustand store
- `useEffect` 依赖数组必须完整且正确
- 定时器、订阅、事件监听必须在 `useEffect` 的 cleanup 中清理

## API 调用规范

- 所有 HTTP 调用统一通过 `services/` 层
- 禁止在组件中直接使用 `fetch` / `axios`
- API 响应必须有类型定义
- 错误处理必须覆盖网络异常、超时、业务错误码三种情况

## 状态管理 (Zustand)

- 全局共享状态使用 Zustand
- 组件局部状态使用 `useState` / `useReducer`
- Store 按业务领域拆分，禁止单一巨型 store

## 禁止事项

- 禁止 `any` 类型
- 禁止 `console.log` 提交到主分支
- 禁止在 JSX 中写复杂逻辑——提取为函数或自定义 Hook
- 禁止直接操作 DOM——使用 React 声明式方式
