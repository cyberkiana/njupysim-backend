package org.njupt.njuptphysim.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ExpListDTO;
import org.njupt.njuptphysim.pojo.dto.ResourcesUpdateDTO;
import org.njupt.njuptphysim.pojo.po.Comments;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.vo.ExpVO;
import org.njupt.njuptphysim.server.mapper.ResourcesMapper;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
public class ResourcesServiceImpl implements ResourcesService {
    @Autowired
    ResourcesMapper resourcesMapper;

    @Autowired
    org.njupt.njuptphysim.server.mapper.ExpEvaluationMapper expEvaluationMapper;

    //todo createrId(当前登录人id)依赖于LoginInterceptor,暂未启用,目前仅使用默认值ROOT
    /**
     * 添加资源
     * @param resources
     * @return
     */
    @Override
    public void addResources( Resources resources) {
        //判断资源是否存在
        resources.setCreaterId("ROOT");
        if (resourcesMapper.getResourcesByName(resources.getTitle()) != null) {
            throw new BaseException("同名资源已存在");
        }
        //create_time、cover_image、url 列 NOT NULL，未传时填默认值
        if (resources.getCreateTime() == null) {
            resources.setCreateTime(LocalDate.now());
        }
        if (resources.getCoverImage() == null || resources.getCoverImage().isBlank()) {
            resources.setCoverImage("");
        }
        if (resources.getUrl() == null || resources.getUrl().isBlank()) {
            resources.setUrl("");
        }
        //添加资源
        resourcesMapper.insert(resources);
    }

    /**
     * 删除资源
     * @param name
     */
    @Override
    public void deleteResources(String name) {
        //判断资源是否存在
        if (resourcesMapper.getResourcesByName(name) == null) {
            throw new BaseException("资源不存在");
        }
        //删除资源
        resourcesMapper.deleteByName(name);
    }

    //todo createrId(当前登录人id)依赖于LoginInterceptor,暂未启用,目前仅使用默认值ROOT
    /**
     * 更新资源
     * @param resources
     */
    @Override
    public void updateResources(ResourcesUpdateDTO resources) {
        // 判断资源是否存在
        if (resourcesMapper.getResourcesByName(resources.getOldName()) == null) {
            throw new BaseException("资源不存在");
        }

        // 按旧名称定位, 仅更新传入的非空字段(表中列名为 title, 不存在 name 列)
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("title", resources.getOldName());
        if (resources.getName() != null && !resources.getName().isBlank()) {
            updateWrapper.set("title", resources.getName());
        }
        if (resources.getCreateTime() != null && !resources.getCreateTime().isBlank()) {
            updateWrapper.set("create_time", LocalDate.parse(resources.getCreateTime()));
        }
        if (resources.getTag() != null && !resources.getTag().isBlank()) {
            updateWrapper.set("tag", resources.getTag());
        }
        if (resources.getContent() != null && !resources.getContent().isBlank()) {
            updateWrapper.set("content", resources.getContent());
        }
        if (resources.getUrl() != null && !resources.getUrl().isBlank()) {
            updateWrapper.set("url", resources.getUrl());
        }
        if (resources.getCoverImage() != null && !resources.getCoverImage().isBlank()) {
            //一键更换头图: 前端上传到 /img 后把新路径传进来, 立即生效
            updateWrapper.set("coverImage", resources.getCoverImage());
        }

        resourcesMapper.update(null, updateWrapper);
    }

    @Override
    public Resources getResourcesById(int id) {
        return resourcesMapper.getResourcesById(id);
    }

    @Override
    public List<ExpListDTO> getAllExpList() {
        return resourcesMapper.getAllExpList();
    }

    @Override
    public void evaluate(int resourceId, String userId, String type) {
        if (expEvaluationMapper.getType(userId, resourceId) != null) {
            throw new BaseException("您已评价过该实验，不能重复评价");
        }
        int t;
        Integer likes = null, easy = null, hard = null;
        switch (type) {
            case "likes" -> { t = 1; likes = 1; }
            case "easy" -> { t = 2; easy = 1; }
            case "hard" -> { t = 3; hard = 1; }
            default -> throw new BaseException("评价类型无效");
        }
        try {
            expEvaluationMapper.insert(userId, resourceId, t);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BaseException("您已评价过该实验，不能重复评价");
        }
        resourcesMapper.updateEvaluation(resourceId, likes, easy, hard);
    }

    @Override
    public String getMyEvaluationType(int resourceId, String userId) {
        Integer t = expEvaluationMapper.getType(userId, resourceId);
        if (t == null) return null;
        return switch (t) { case 1 -> "likes"; case 2 -> "easy"; default -> "hard"; };
    }

    @Override
    public List<ExpListDTO> getClazzExpList(String clazzId) {
        return resourcesMapper.getClazzExpList(clazzId);
    }

    @Override
    public ExpVO getExpDetail(int id) {
        Resources resource = resourcesMapper.getResourcesById(id);
        if (resource == null) {
            throw new BaseException("实验不存在");
        }
        ExpVO build = ExpVO.builder().id(resource.getId())
                .url(resource.getUrl())
                .title(resource.getTitle())
                .coverImage(resource.getCoverImage())
                .likes(resource.getLikes())
                .easyCount(resource.getEasyCount())
                .hardCount(resource.getHardCount())
                .build();
        return build;
    }

}
