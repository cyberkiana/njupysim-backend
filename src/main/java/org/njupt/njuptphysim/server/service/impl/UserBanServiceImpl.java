package org.njupt.njuptphysim.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.pojo.vo.UserBan;
import org.njupt.njuptphysim.server.mapper.UserBanMapper;
import org.njupt.njuptphysim.server.service.UserBanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账号封禁服务实现。
 *
 * <p>数据库表 sys_user_ban 为准，内存缓存生效封禁名单，每 30 秒刷新一次；
 * ban/unban 操作会立即刷新缓存保证实时生效。</p>
 */
@Service
@Slf4j
public class UserBanServiceImpl implements UserBanService {

    /** 缓存刷新周期（毫秒） */
    private static final long CACHE_TTL_MS = 30_000;

    @Autowired
    private UserBanMapper userBanMapper;

    /** 缓存的生效封禁名单 */
    private volatile List<String> activeCache = List.of();

    /** 缓存加载时间戳 */
    private volatile long lastLoadTime = 0;

    @Override
    public List<UserBan> listAll() {
        return userBanMapper.searchAll();
    }

    @Override
    public void ban(String userId, String reason, String operator) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        int updated = userBanMapper.rebanByUserId(userId, reason, operator);
        if (updated == 0) {
            userBanMapper.insert(userId, reason, operator);
        }
        refreshCache();
        log.warn("[账号封禁] 账号[{}] 已被封禁，原因：{}，操作人：{}", userId, reason, operator);
    }

    @Override
    public void unban(long id, String operator) {
        int updated = userBanMapper.unbanById(id);
        if (updated > 0) {
            refreshCache();
            log.info("[账号封禁] 记录[{}] 已由管理员[{}]解封", id, operator);
        }
    }

    @Override
    public List<String> getActiveUserIds() {
        long now = System.currentTimeMillis();
        if (now - lastLoadTime > CACHE_TTL_MS) {
            refreshCache();
        }
        return activeCache;
    }

    @Override
    public boolean isUserBanned(String userId) {
        return getActiveUserIds().contains(userId);
    }

    /** 从数据库重新加载生效封禁名单。数据库异常时保留旧缓存。 */
    private synchronized void refreshCache() {
        try {
            activeCache = userBanMapper.searchActiveUserIds();
            lastLoadTime = System.currentTimeMillis();
        } catch (Exception e) {
            log.error("[账号封禁] 加载封禁名单失败，沿用旧缓存: {}", e.getMessage());
            lastLoadTime = System.currentTimeMillis();
        }
    }
}
