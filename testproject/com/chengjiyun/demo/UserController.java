package com.chengjiyun.demo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户控制器 —— 仅负责参数校验 + 视图转换，不含业务逻辑.
 *
 * <p>规范约定（来自 .claude/rules/api-conventions.md + CLAUDE.md）：
 * <ul>
 *   <li>URL: 全小写、短横线分隔、资源名复数、版本前缀 /api/v1/</li>
 *   <li>Controller 禁止 try-catch（由 GlobalExceptionHandler 统一处理）</li>
 *   <li>Controller 不含业务逻辑，只做参数校验和调用 Service</li>
 *   <li>分层: Controller → Service → Mapper，禁止逆向调用</li>
 * </ul>
 *
 * <p>URL 设计示例：
 * <pre>
 *   GET    /api/v1/users/{id}   → 详情
 *   GET    /api/v1/users        → 分页列表
 *   POST   /api/v1/users        → 创建
 *   PATCH  /api/v1/users/{id}   → 部分更新
 *   DELETE /api/v1/users/{id}   → 删除
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService(); // 演示代码，实际由 Spring 注入
    }

    // ==================== GET /api/v1/users/{id} ====================

    /** 查询用户详情 —— Controller 不 try-catch，异常由 GlobalExceptionHandler 处理 */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO.UserVO> getById(@PathVariable Long id) {
        // Controller 不写业务逻辑，直接委托 Service
        UserDTO.UserVO vo = userService.findById(id);
        return ApiResponse.success(vo);
    }

    // ==================== GET /api/v1/users?page=1&size=20 ====================

    /**
     * 分页查询 —— 遵循 api-conventions.md 分页格式.
     * 请求: GET /api/v1/users?page=1&size=20&sort=create_time,desc
     */
    @GetMapping
    public ApiResponse<ApiResponse.PageData<UserDTO.UserVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        ApiResponse.PageData<UserDTO.UserVO> pageData = userService.listPage(page, size);
        return ApiResponse.page(pageData);
    }

    // ==================== POST /api/v1/users ====================

    /** 创建用户 —— @Valid 触发 Jakarta Validation，校验失败由 GlobalExceptionHandler 处理 */
    @PostMapping
    public ApiResponse<UserDTO.UserVO> create(@Valid @RequestBody UserDTO dto) {
        UserDTO.UserVO vo = userService.create(dto);
        return ApiResponse.success(vo);
    }

    // ==================== PATCH /api/v1/users/{id}/deduct ====================

    /** 扣款操作 —— 金额 BigDecimal，演示嵌套资源路径 */
    @PatchMapping("/{id}/deduct")
    public ApiResponse<UserDTO.UserVO> deduct(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        UserDTO.UserVO vo = userService.deduct(id, amount);
        return ApiResponse.success(vo);
    }

    // ==================== DELETE /api/v1/users/{id} ====================

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        // 先查存在性
        userService.findById(id); // 不存在会抛 BusinessException → Handler 处理
        // userMapper.deleteById(id); // 实际调用
        return ApiResponse.success();
    }
}
