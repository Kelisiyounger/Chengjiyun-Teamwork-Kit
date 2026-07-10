# API 约定

> 此文件为 `CLAUDE.md` 的模块化扩展，Claude Code 自动加载。

---

## RESTful 设计

### URL 规范
```
GET     /api/v1/users          → 列表
GET     /api/v1/users/{id}     → 详情
POST    /api/v1/users          → 创建
PUT     /api/v1/users/{id}     → 全量更新
PATCH   /api/v1/users/{id}     → 部分更新
DELETE  /api/v1/users/{id}     → 删除
```

- 全部小写，短横线分隔
- 资源名用复数
- 版本前缀 `/api/v1/`
- 嵌套不超过 2 层: `/api/v1/users/{id}/orders`

## 统一响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": { },
  "timestamp": "2026-07-09T12:00:00Z"
}
```

### 业务码规范

| Code | 含义 |
|------|------|
| 0 | 成功 |
| 1001 | 参数校验失败 |
| 1002 | 资源不存在 |
| 1003 | 无权限 |
| 2001 | 业务规则限制 |
| 5000 | 服务内部错误 |

## 分页

```json
// 请求
GET /api/v1/users?page=1&size=20&sort=create_time,desc

// 响应 data
{
  "content": [...],
  "page": 1,
  "size": 20,
  "totalElements": 156,
  "totalPages": 8
}
```

## 错误处理

```java
// Controller 层全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
```

- Service 抛业务异常 → Controller Advice 统一处理
- 禁止在 Controller 中 try-catch
- 错误信息对用户友好，不暴露内部细节
