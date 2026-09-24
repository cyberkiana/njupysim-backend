package org.njupt.njuptphysim.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.pojo.po.IpBlacklist;
import org.njupt.njuptphysim.server.mapper.IpBlacklistMapper;
import org.njupt.njuptphysim.server.service.IpBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IP黑名单服务实现。
 *
 * <p>黑名单以数据库表 sys_ip_blacklist 为准，为避免每个请求都查库，
 * 内存中缓存一份生效规则，每 CACHE_TTL_MS 刷新一次；
 * ban/unban 操作会立即刷新缓存保证实时生效。</p>
 */
@Service
@Slf4j
public class IpBlacklistServiceImpl implements IpBlacklistService {

    /** 缓存刷新周期（毫秒） */
    private static final long CACHE_TTL_MS = 30_000;

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    /** 缓存的生效黑名单规则 */
    private volatile List<String> activeIpsCache = List.of();

    /** 缓存加载时间戳 */
    private volatile long lastLoadTime = 0;

    @Override
    public List<String> getActiveIps() {
        long now = System.currentTimeMillis();
        if (now - lastLoadTime > CACHE_TTL_MS) {
            refreshCache();
        }
        return activeIpsCache;
    }

    @Override
    public List<IpBlacklist> listAll() {
        return ipBlacklistMapper.searchAll();
    }

    @Override
    public void ban(String ip, String reason, String operator) {
        if (ip == null || ip.isBlank()) {
            return;
        }
        int updated = ipBlacklistMapper.rebanByIp(ip, reason, operator);
        if (updated == 0) {
            ipBlacklistMapper.insert(ip, reason, operator);
        }
        refreshCache();
        log.warn("[IP黑名单] IP[{}] 已被列入黑名单，原因：{}，操作人：{}", ip, reason, operator);
    }

    @Override
    public void unban(long id, String operator) {
        int updated = ipBlacklistMapper.unbanById(id, operator);
        if (updated > 0) {
            refreshCache();
            log.info("[IP黑名单] 记录[{}] 已由管理员[{}]解封", id, operator);
        }
    }

    /**
     * 从数据库重新加载生效规则。数据库异常时保留旧缓存并放行（避免误杀全部请求）。
     */
    private synchronized void refreshCache() {
        try {
            activeIpsCache = ipBlacklistMapper.searchActiveIps();
            lastLoadTime = System.currentTimeMillis();
        } catch (Exception e) {
            log.error("[IP黑名单] 加载黑名单失败，沿用旧缓存: {}", e.getMessage());
            lastLoadTime = System.currentTimeMillis();
        }
    }
}
