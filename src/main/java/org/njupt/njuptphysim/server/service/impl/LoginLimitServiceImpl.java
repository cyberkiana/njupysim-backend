package org.njupt.njuptphysim.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.server.service.IpBlacklistService;
import org.njupt.njuptphysim.server.service.LoginLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 登录限流服务实现（Redis）。
 *
 * <p>用户每次登录时在 Redis 中尝试创建一个 1 分钟过期的计数 key
 * （login:limit:{ip}）：key 不存在则创建并置 1；已存在则值加 1（不续期，
 * 保证窗口固定为第一次尝试后的 1 分钟）。窗口内计数超过 30 次时，
 * 将该 IP 写入数据库黑名单表封禁。</p>
 */
@Service
@Slf4j
public class LoginLimitServiceImpl implements LoginLimitService {

    /** Redis 计数 key 前缀 */
    private static final String KEY_PREFIX = "login:limit:";

    /** 计数窗口：1 分钟 */
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IpBlacklistService ipBlacklistService;

    @Override
    public boolean isBlockedAfterRecord(String ip) {
        try {
            String key = KEY_PREFIX + ip;
            // 尝试创建 1 分钟过期的 key；已存在则自增
            Boolean created = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", WINDOW);
            long count = 1;
            if (Boolean.FALSE.equals(created)) {
                Long incremented = stringRedisTemplate.opsForValue().increment(key);
                count = incremented == null ? 1 : incremented;
            }
            if (count > MAX_ATTEMPTS_PER_MINUTE) {
                ipBlacklistService.ban(ip,
                        "1分钟内连续登录超过" + MAX_ATTEMPTS_PER_MINUTE + "次，触发自动封禁",
                        "系统自动");
                return true;
            }
            return false;
        } catch (Exception e) {
            // Redis 不可用时降级放行，不影响正常登录
            log.error("[登录限流] Redis 计数异常，本次放行: {}", e.getMessage());
            return false;
        }
    }
}
