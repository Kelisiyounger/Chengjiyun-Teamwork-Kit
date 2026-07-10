package com.chengjiyun.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 —— 统一将业务异常转换为 ApiResponse.
 *
 * <p>规范约定（来自 .claude/rules/api-conventions.md）：
 * <ul>
 *   <li>@RestControllerAdvice 统一拦截异常</li>
 *   <li>禁止 Controller 中 try-catch（保持控制器干净）</li>
 *   <li>错误信息对用户友好，不暴露堆栈</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 → 返回对应业务码和信息 */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /** 参数校验失败（Spring Validation） */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException e) {
        // StringBuilder 拼接，遵循阿里巴巴手册
        StringBuilder sb = new StringBuilder("参数校验失败: ");
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
            sb.append(fieldError.getField())
              .append(" ")
              .append(fieldError.getDefaultMessage())
              .append("; ")
        );
        String msg = sb.toString().trim();
        log.warn(msg);
        return ApiResponse.error(BusinessException.CODE_BAD_REQUEST, msg);
    }

    /** 兜底：未预期的内部错误，不暴露堆栈 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnknown(Exception e) {
        log.error("服务内部错误", e);
        // 对用户友好，不暴露内部细节
        return ApiResponse.error(5000, "服务繁忙，请稍后重试");
    }
}
