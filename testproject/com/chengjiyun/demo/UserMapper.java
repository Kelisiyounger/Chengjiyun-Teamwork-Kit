package com.chengjiyun.demo;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户数据访问层 —— 单表操作，SQL 必须参数化.
 *
 * <p>规范约定（来自 .claude/rules/code-style.md + api-conventions.md）：
 * <ul>
 *   <li>禁止 SELECT *（所有查询显式列出列名）</li>
 *   <li>SQL 必须参数化（#{xxx}），禁止字符串拼接</li>
 *   <li>表名 / 列名小写蛇形（user_info, create_time）</li>
 *   <li>必有 id, create_time, update_time 三字段</li>
 *   <li>禁止 COUNT(常量)，统一 COUNT(*)</li>
 * </ul>
 */
@Mapper
public interface UserMapper {

    /** 根据 ID 查询 —— 显式列出列名，禁止 SELECT * */
    @Select("SELECT id, username, email, balance, create_time, update_time "
          + "FROM user_info WHERE id = #{id}")
    UserDO findById(@Param("id") Long id);

    /** 列表查询 —— 参数化排序，禁止字符串拼接 */
    @Select("SELECT id, username, email, balance, create_time, update_time "
          + "FROM user_info "
          + "ORDER BY create_time DESC "
          + "LIMIT #{size} OFFSET #{offset}")
    List<UserDO> findAll(@Param("offset") int offset, @Param("size") int size);

    /** 统计总数 —— 使用 COUNT(*)，禁止 COUNT(常量) */
    @Select("SELECT COUNT(*) FROM user_info")
    long count();

    /** 插入 —— 参数化，禁止字符串拼接 */
    @Insert("INSERT INTO user_info (username, email, balance, create_time, update_time) "
          + "VALUES (#{username}, #{email}, #{balance}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserDO userDO);

    /** 更新 —— 参数化 SQL，设置 update_time */
    @Update("UPDATE user_info "
          + "SET username = #{username}, email = #{email}, "
          + "    balance = #{balance}, update_time = NOW() "
          + "WHERE id = #{id}")
    int update(UserDO userDO);

    /** 删除 */
    @Delete("DELETE FROM user_info WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /** 唯一索引 UK_user_info_email 查重 */
    @Select("SELECT COUNT(*) FROM user_info WHERE email = #{email}")
    long countByEmail(@Param("email") String email);
}
