package org.njupt.njuptphysim.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// jwt 配置类
@Data
@Component
@ConfigurationProperties(prefix = "njupt-phy-sim.jwt")
public class JwtProperty {

    private String secretKey;   // 密钥
    private Long ttlMillis;     // 令牌有效期 毫秒

}
