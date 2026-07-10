package com.chengjiyun.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务 —— 业务逻辑 + 事务管理，禁止 Controller 直接调用 Mapper.
 *
 * <p>规范约定（来自 CLAUDE.md + .claude/rules/）：
 * <ul>
 *   <li>分层: Controller → Service → Mapper，禁止逆向调用</li>
 *   <li>金额字段 BigDecimal，运算用 .add()/.subtract() 等方法</li>
 *   <li>集合判空用 isEmpty() 而非 size() == 0</li>
 *   <li>DTO → DO → VO 转换在 Service 层完成</li>
 *   <li>异常用 BusinessException 抛出，Controller 不 try-catch</li>
 * </ul>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // 模拟注入（实际用 @Autowired/@RequiredArgsConstructor）
    private final UserMapper userMapper;

    public UserService() {
        this.userMapper = null; // 演示代码，实际由 Spring 注入
    }

    // ==================== 查询 ====================

    /** 按 ID 查询 —— 不存在抛 BusinessException */
    public UserDTO.UserVO findById(Long id) {
        UserDO userDO = userMapper.findById(id);
        if (userDO == null) {
            throw BusinessException.notFound("用户不存在: id=" + id);
        }
        return UserDTO.UserVO.from(userDO);
    }

    /**
     * 分页列表 —— 集合判空用 isEmpty()（阿里规约）.
     *
     * <p>分页格式遵循 api-conventions.md: {content, page, size, totalElements, totalPages}
     */
    public ApiResponse.PageData<UserDTO.UserVO> listPage(int page, int size) {
        int offset = (page - 1) * size;
        List<UserDO> list = userMapper.findAll(offset, size);
        long total = userMapper.count();

        // 集合判空用 isEmpty()，禁止 size() == 0
        if (list.isEmpty()) {
            return new ApiResponse.PageData<>(Collections.emptyList(), page, size, 0);
        }

        // DTO → VO 转换在 Service 层
        List<UserDTO.UserVO> voList = list.stream()
                .map(UserDTO.UserVO::from)
                .collect(Collectors.toList());

        return new ApiResponse.PageData<>(voList, page, size, total);
    }

    // ==================== 创建 ====================

    /** 创建用户 —— 事务管理，余额 BigDecimal 运算 */
    @Transactional
    public UserDTO.UserVO create(UserDTO dto) {
        // 邮箱唯一性校验（UK_user_info_email）
        if (userMapper.countByEmail(dto.getEmail()) > 0) {
            throw BusinessException.limit("该邮箱已被注册");
        }

        UserDO userDO = new UserDO(dto.getUsername(), dto.getEmail(), dto.getBalance());
        userMapper.insert(userDO);
        log.info("用户创建成功: id={}, username={}", userDO.getId(), userDO.getUsername());
        return UserDTO.UserVO.from(userDO);
    }

    // ==================== 扣款（展示 BigDecimal 运算） ====================

    /**
     * 扣款——演示 BigDecimal 安全运算.
     * 金额运算禁止用 float/double，必须用 BigDecimal 的 add/subtract/multiply 方法.
     */
    @Transactional
    public UserDTO.UserVO deduct(Long userId, BigDecimal amount) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.badRequest("扣款金额必须大于0");
        }

        UserDO userDO = userMapper.findById(userId);
        if (userDO == null) {
            throw BusinessException.notFound("用户不存在: id=" + userId);
        }

        // BigDecimal 比较用 compareTo，禁止用 equals（精度敏感）
        if (userDO.getBalance().compareTo(amount) < 0) {
            throw BusinessException.limit("余额不足：当前余额 " + userDO.getBalance() + "，扣款 " + amount);
        }

        // BigDecimal 减法运算 —— 用 .subtract()，禁止用 -
        userDO.setBalance(userDO.getBalance().subtract(amount));
        userMapper.update(userDO);

        log.info("扣款成功: userId={}, amount={}, remain={}", userId, amount, userDO.getBalance());
        return UserDTO.UserVO.from(userDO);
    }
}
