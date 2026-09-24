package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.po.IpBlacklist;

import java.util.List;

/**
 * IP黑名单服务：黑名单存储在数据库表 sys_ip_blacklist 中（替代 yml 固定配置），
 * 管理员可查看并解封。
 */
public interface IpBlacklistService {

    /**
     * 获取当前处于封禁状态的黑名单规则（带短时缓存，供 IpFilter 每次请求调用）
     */
    List<String> getActiveIps();

    /**
     * 查询全部黑名单记录（含已解封，供管理员查看）
     */
    List<IpBlacklist> listAll();

    /**
     * 封禁IP（已存在记录则重新置为封禁），并立即刷新缓存
     *
     * @param ip      IP地址
     * @param reason  封禁原因
     * @param operator 操作人（系统自动 / 管理员账号）
     */
    void ban(String ip, String reason, String operator);

    /**
     * 解封黑名单记录，并立即刷新缓存
     *
     * @param id       记录id
     * @param operator 操作人（管理员账号）
     */
    void unban(long id, String operator);
}
