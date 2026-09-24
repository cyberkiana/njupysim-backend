package org.njupt.njuptphysim.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 头像存储配置（nginx 静态服务方案）。
 *
 * <p>头像文件存放在虚拟机 nginx 的静态目录中：后端收到上传后通过 HTTP PUT
 * 把文件推送到 nginx 的 WebDAV 上传路径，前端通过 nginx 静态路径访问头像。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "avatar")
public class AvatarProperty {

    /** nginx 所在地址（如 http://192.168.233.130），后端上传文件时的目标 */
    private String nginxBaseUrl = "http://192.168.233.130";

    /** nginx WebDAV 上传路径（nginx 配置中对应 dav_methods PUT 的 location） */
    private String uploadPath = "/_avatar_upload/";

    /** 头像静态访问路径前缀（存入数据库的相对url前缀，由 nginx 静态服务或前端开发代理解析） */
    private String staticPath = "/avatar/";
}
