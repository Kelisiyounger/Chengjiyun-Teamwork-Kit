package com.chengjiyun.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 DTO —— 用于 Controller 层接收请求参数，与 DO 严格分离.
 *
 * <p>规范约定（来自 .claude/rules/code-style.md + CLAUDE.md）：
 * <ul>
 *   <li>DO / VO / DTO 严格分离，禁止混用</li>
 *   <li>金额字段必须用 BigDecimal，禁止 float/double</li>
 *   <li>POJO 必须重写 toString()</li>
 *   <li>命名：类名 PascalCase，字段 camelCase</li>
 * </ul>
 */
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 账户余额 —— 金额字段必须 BigDecimal（阿里规约），禁止 float/double 造成精度丢失 */
    @Positive(message = "余额必须大于0")
    private BigDecimal balance;

    // ---------- 构造器 ----------

    public UserDTO() {}

    public UserDTO(String username, String email, BigDecimal balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    // ---------- getter / setter ----------

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    /** POJO 必须重写 toString() */
    @Override
    public String toString() {
        return "UserDTO{username='" + username + "', email='" + email
             + "', balance=" + balance + '}';
    }

    // ================================================================
    // VO（视图对象）——只用于返回给前端，与 DTO/DO 分离
    // ================================================================

    public static class UserVO {
        private Long id;
        private String username;
        private String email;
        private BigDecimal balance;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;

        public UserVO() {}

        /** 从 DO 转换为 VO（Service 层负责调用） */
        public static UserVO from(UserDO userDO) {
            UserVO vo = new UserVO();
            vo.id = userDO.getId();
            vo.username = userDO.getUsername();
            vo.email = userDO.getEmail();
            vo.balance = userDO.getBalance();
            vo.createTime = userDO.getCreateTime();
            vo.updateTime = userDO.getUpdateTime();
            return vo;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

        public LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

        @Override
        public String toString() {
            return "UserVO{id=" + id + ", username='" + username
                 + "', email='" + email + "', balance=" + balance + '}';
        }
    }
}
