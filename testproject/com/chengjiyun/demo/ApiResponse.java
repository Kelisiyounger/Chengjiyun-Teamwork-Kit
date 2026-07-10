package com.chengjiyun.demo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 统一 API 响应体 —— 遵循 .claude/rules/api-conventions.md 定义的格式.
 *
 * <p>规范约定：
 * <ul>
 *   <li>code=0 成功，1001 参数错误，1002 资源不存在，5000 服务内部错误</li>
 *   <li>message 对用户友好，不暴露内部细节</li>
 *   <li>timestamp 使用 ISO-8601 格式（PostgreSQL 兼容）</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /* 业务码：0=成功，1001=参数校验失败，1002=资源不存在，1003=无权限，2001=业务规则限制，5000=服务内部错误 */
    private int code;
    private String message;
    private T data;
    private String timestamp;

    private ApiResponse() {}

    // ---------- 成功响应 ----------

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        r.timestamp = LocalDateTime.now().toString();
        return r;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    // ---------- 失败响应 ----------

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = code;
        r.message = message;
        r.timestamp = LocalDateTime.now().toString();
        return r;
    }

    // ---------- 分页响应 ----------
    // 遵循分页规范: { content, page, size, totalElements, totalPages }

    public static <T> ApiResponse<PageData<T>> page(PageData<T> pageData) {
        return success(pageData);
    }

    // ---------- getter / setter ----------

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // POJO 必须重写 toString（阿里巴巴 Java 开发手册 泰山版）
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); // 循环外使用 + 亦可，此处展示 StringBuilder 用法
        sb.append("ApiResponse{code=").append(code)
          .append(", message='").append(message).append('\'')
          .append(", data=").append(data)
          .append(", timestamp='").append(timestamp).append('\'')
          .append('}');
        return sb.toString();
    }

    // ========== 内部分页数据类 ==========

    public static class PageData<T> {
        private java.util.List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public PageData() {}

        public PageData(java.util.List<T> content, int page, int size, long totalElements) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            // 阿里巴巴规约：禁止使用 COUNT(常量)，用 COUNT(*)
            this.totalPages = (int) Math.ceil((double) totalElements / size);
        }

        public java.util.List<T> getContent() { return content; }
        public void setContent(java.util.List<T> content) { this.content = content; }

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        @Override
        public String toString() {
            return "PageData{content=" + content + ", page=" + page
                 + ", size=" + size + ", totalElements=" + totalElements
                 + ", totalPages=" + totalPages + '}';
        }
    }
}
