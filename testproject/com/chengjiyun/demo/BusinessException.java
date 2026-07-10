package com.chengjiyun.demo;

/**
 * 业务异常 —— Service 层抛出，由 GlobalExceptionHandler 统一处理.
 *
 * <p>规范约定（来自 .claude/rules/api-conventions.md）：
 * <ul>
 *   <li>Service 抛业务异常 → Controller Advice 统一处理</li>
 *   <li>禁止在 Controller 中 try-catch</li>
 *   <li>错误信息对用户友好，不暴露内部细节</li>
 * </ul>
 */
public class BusinessException extends RuntimeException {

    private final int code;

    // ---------- 常用业务码 ----------
    /** 参数校验失败 */
    public static final int CODE_BAD_REQUEST = 1001;
    /** 资源不存在 */
    public static final int CODE_NOT_FOUND = 1002;
    /** 无权限 */
    public static final int CODE_FORBIDDEN = 1003;
    /** 业务规则限制（如余额不足） */
    public static final int CODE_BUSINESS_LIMIT = 2001;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(CODE_NOT_FOUND, message);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(CODE_BAD_REQUEST, message);
    }

    public static BusinessException forbidden() {
        return new BusinessException(CODE_FORBIDDEN, "无权限访问该资源");
    }

    /** 业务规则限制，如：余额不足、状态不允许等 */
    public static BusinessException limit(String message) {
        return new BusinessException(CODE_BUSINESS_LIMIT, message);
    }

    public int getCode() { return code; }

    @Override
    public String toString() {
        return "BusinessException{code=" + code + ", message='" + getMessage() + "'}";
    }
}
