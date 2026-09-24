package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.ResourcesUpdateDTO;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

/**
 * @author ricefoodie
 */
@RestController
@RequestMapping("/admin/res")
public class ResourcesController {

    @Autowired
    private ResourcesService resourcesService;

    /** WebGL 实验文件存放目录（服务器独立文件夹），可用 yml webgl.upload-dir 调整 */
    @Value("${webgl.upload-dir:./webgl-files}")
    private String webglUploadDir;

    /** 实验头图存放目录（服务器独立文件夹），可用 yml resource.img-dir 调整 */
    @Value("${resource.img-dir:./img}")
    private String imgUploadDir;

    /**
     * 上传实验头图到服务器 /img 独立文件夹，返回可通过 /img/** 静态访问的url。
     * 调用方将返回的url写入 resources 表的 coverImage 字段。
     * @param file 图片文件（png/jpg/webp 等）
     * @return { url: 可访问路径 }
     */
    @OperationLog(value = "新增", detail = "上传实验头图")
    @PostMapping("/upload-cover")
    public Result uploadCover(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择头图文件");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            return Result.error("文件名无效");
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!java.util.Set.of("png", "jpg", "jpeg", "webp", "gif").contains(ext)) {
            return Result.error("仅支持图片格式：png/jpg/jpeg/webp/gif");
        }
        try {
            Path dir = Paths.get(imgUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String stored = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "_cover." + ext;
            file.transferTo(dir.resolve(stored).toFile());
            return Result.success("/img/" + stored);
        } catch (IOException e) {
            throw new BaseException("头图保存失败，请稍后重试");
        }
    }

    /**
     * 上传 WebGL 实验文件到服务器独立文件夹，返回可通过 /webgl/** 静态访问的url。
     * 前端将返回的url填入实验资源的 url 字段。
     * @param file WebGL文件（.html/.wasm/.data/.js/.zip 等）
     * @return { url: 可访问路径 }
     */
    @OperationLog(value = "新增", detail = "上传WebGL实验文件")
    @PostMapping("/upload-webgl")
    public Result uploadWebgl(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的WebGL文件");
        }
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            return Result.error("文件名无效");
        }
        //只保留文件名部分, 过滤路径穿越字符
        String safeName = original.replace('\\', '/');
        safeName = safeName.substring(safeName.lastIndexOf('/') + 1).trim();
        safeName = safeName.replaceAll("[^\\w.\\-\\u4e00-\\u9fa5]", "_");
        if (safeName.isBlank()) {
            safeName = "webglBuild";
        }

        try {
            Path dir = Paths.get(webglUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String stored = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "_" + safeName;
            file.transferTo(dir.resolve(stored).toFile());
            String url = "/webgl/" + stored;
            return Result.success(url);
        } catch (IOException e) {
            throw new BaseException("WebGL文件保存失败，请稍后重试");
        }
    }



    /**
     * 添加资源
     * @param resources
     * @return
     */
    @OperationLog(value = "新增", detail = "添加实验资源")
    @PostMapping
    public Result addResources(@RequestBody Resources resources)
    {
        resourcesService.addResources( resources );
        return Result.success();
    }


    /**
     * 删除资源
     * @param name
     * @return
     */
    @OperationLog(value = "删除", detail = "删除实验资源")
    @DeleteMapping
    public Result deleteResources( @RequestParam String name )
    {
        resourcesService.deleteResources( name );
        return Result.success();
    }

    /**
     * 修改资源
     * @param resources
     * @return
     */
    @OperationLog(value = "修改", detail = "修改实验资源")
    @PostMapping("/update")
    public Result updateResources(@RequestBody ResourcesUpdateDTO resources)
    {
        resourcesService.updateResources( resources );
        return Result.success();
    }




}
