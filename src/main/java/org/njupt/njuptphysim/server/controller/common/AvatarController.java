package org.njupt.njuptphysim.server.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.properties.AvatarProperty;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 用户头像：上传 / 更新 / 查询。
 *
 * <p>头像文件存放在虚拟机 nginx 的静态目录（/data/nginx/avatar）：
 * 后端收到上传后通过 HTTP PUT 推送到 nginx 的 WebDAV 上传路径，
 * 前端通过 nginx 静态路径 /avatar/** 访问，实现 nginx 头像静态传输。</p>
 */
@RestController
@RequestMapping("/common/user/avatar")
@Slf4j
public class AvatarController {

    /** 允许的图片扩展名白名单 */
    private static final Set<String> ALLOWED_EXTS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    @Autowired
    private AvatarProperty avatarProperty;

    @Autowired
    private UserService userService;

    /**
     * 上传头像图片文件：推送到虚拟机 nginx 静态目录，返回可访问的头像url
     * @param file 图片文件（multipart 字段名 avatar）
     */
    @OperationLog(value = "上传", detail = "上传用户头像图片")
    @PostMapping("/upload")
    public Result upload(@RequestParam("avatar") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        // 扩展名白名单校验
        String original = file.getOriginalFilename();
        String ext = original == null || !original.contains(".")
                ? "" : original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTS.contains(ext)) {
            return Result.error("仅支持图片格式：" + String.join("/", ALLOWED_EXTS));
        }

        // 文件名：当前用户id + 时间戳，避免重名与越权覆盖
        String filename = BaseContext.getCurrentId() + "_" + System.currentTimeMillis() + "." + ext;
        String putUrl = avatarProperty.getNginxBaseUrl() + avatarProperty.getUploadPath() + filename;

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(putUrl))
                    .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .timeout(Duration.ofSeconds(15))
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("头像上传到nginx失败，status={} body={}", response.statusCode(), response.body());
                return Result.error("头像存储服务异常(HTTP " + response.statusCode() + ")，请检查虚拟机nginx是否正常运行");
            }
        } catch (Exception e) {
            log.error("头像上传到nginx失败: {}", e.getMessage());
            return Result.error("无法连接头像存储服务，请检查虚拟机nginx是否已启动");
        }

        // 返回相对路径（开发模式由 vite 代理转发，生产模式由 nginx 直接静态服务）
        return Result.success(avatarProperty.getStaticPath() + filename);
    }

    /**
     * 将头像url更新到数据库（仅允许修改自己的头像）
     * @param body { id: 用户id, avatar: 头像url }
     */
    @OperationLog(value = "修改", detail = "更新用户头像")
    @PostMapping("/update")
    public Result update(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String avatar = body.get("avatar");
        if (id == null || id.isBlank() || avatar == null || avatar.isBlank()) {
            return Result.error("参数不完整");
        }
        String currentId = BaseContext.getCurrentId();
        if (!id.equals(currentId)) {
            return Result.error("只能修改自己的头像");
        }
        userService.updateAvatar(id, avatar);
        return Result.success();
    }

    /**
     * 根据用户id查询头像url
     * @param id 用户id
     */
    @OperationLog(value = "查询", detail = "查询用户头像")
    @GetMapping("")
    public Result get(@RequestParam String id) {
        return Result.success(userService.getAvatarById(id));
    }
}
