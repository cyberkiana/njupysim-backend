package org.njupt.njuptphysim.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginVO {
    String name;//姓名
    String account;//账号
    String id; //id
    String token;//令牌
    String roleName;//具体见数据库roles,前端根据此匹配动态路由
}
