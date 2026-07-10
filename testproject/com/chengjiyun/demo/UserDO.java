package com.chengjiyun.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户数据对象（DO）—— 与数据库表 user_info 一一映射.
 *
 * <p>规范约定（来自 .claude/rules/code-style.md + api-conventions.md）：
 * <ul>
 *   <li>表必须有 id、create_time、update_time 三个字段</li>
 *   <li>表名 / 列名小写蛇形命名（user_info, create_time）</li>
 *   <li>金额字段 BigDecimal</li>
 *   <li>POJO 必须重写 toString()</li>
 *   <li>DO / VO / DTO 严格分离</li>
 * </ul>
 */
public class UserDO {

    private Long id;
    private String username;
    private String email;
    /** 余额 —— BigDecimal 保证精度，禁止 float/double */
    private BigDecimal balance;
    /** 非标字段——与数据库列 create_time 映射 */
    private LocalDateTime createTime;
    /** 非标字段——与数据库列 update_time 映射 */
    private LocalDateTime updateTime;

    // ---------- 构造器 ----------

    public UserDO() {}

    public UserDO(String username, String email, BigDecimal balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    // ---------- getter / setter ----------

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

    /** POJO 必须重写 toString（阿里巴巴规约） */
    @Override
    public String toString() {
        return "UserDO{id=" + id + ", username='" + username
             + "', email='" + email + "', balance=" + balance
             + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
