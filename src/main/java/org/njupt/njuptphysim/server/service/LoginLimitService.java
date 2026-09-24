package org.njupt.njuptphysim.server.service;

/**
 * 登录限流服务：基于 Redis 的登录计数。
 * 每次登录尝试为 IP 创建/自增一个 1 分钟过期的计数 key，
 * 1 分钟内连续登录超过阈值则将 IP 列入黑名单。
 */
public interface LoginLimitService {

    /** 1 分钟内允许的最大登录尝试次数 */
    int MAX_ATTEMPTS_PER_MINUTE = 30;

    /**
     * 登录前调用：对客户端IP计数，超过阈值时封禁IP并返回 true
     *
     * @param ip 客户端IP
     * @return true 表示该IP已被封禁，应拒绝登录
     */
    boolean isBlockedAfterRecord(String ip);
}
