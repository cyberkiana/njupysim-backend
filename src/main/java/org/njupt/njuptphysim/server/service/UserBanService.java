package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.vo.UserBan;

import java.util.List;

/**
 * 账号封禁服务。
 *
 * <p>黑名单以数据库表 sys_user_ban 为准，为避免每个请求都查库，
 * 内存缓存生效封禁名单，每 30 秒刷新一次；ban/unban 立即刷新缓存。</p>
 */
public interface UserBanService {

    /** 查询全部封禁记录（封禁中+已解封） */
    List<UserBan> listAll();

    /** 封禁账号（存在历史记录则重新封禁） */
    void ban(String userId, String reason, String operator);

    /** 解封指定封禁记录 */
    void unban(long id, String operator);

    /** 查询当前处于封禁状态的账号id集合（拦截器用） */
    List<String> getActiveUserIds();

    /** 账号是否处于封禁状态（登录校验用） */
    boolean isUserBanned(String userId);
}
