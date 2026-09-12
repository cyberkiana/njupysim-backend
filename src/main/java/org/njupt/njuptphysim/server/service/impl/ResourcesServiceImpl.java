package org.njupt.njuptphysim.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ExpEvaluationDTO;
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

        // 直接设置要更新的字段
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", resources.getOldName())
                .set("name", resources.getName())
                .set("create_time", LocalDate.parse(resources.getCreateTime()) )
                .set("creater_id", "ROOT"); // 如需要更新创建者

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
    public List<ExpListDTO> getClazzExpList(String clazzId) {
        return resourcesMapper.getClazzExpList(clazzId);
    }

    @Override
    public ExpVO getExpDetail(int id) {
        Resources resource = resourcesMapper.getResourcesById(id);
        ExpVO build = ExpVO.builder().id(resource.getId())
                .url(resource.getUrl())
                .title(resource.getTitle())
                .likes(resource.getLikes())
                .easyCount(resource.getEasyCount())
                .hardCount(resource.getHardCount())
                .build();
        return build;
    }

    @Override
    public void updateEvaluation(int id, ExpEvaluationDTO expEvaluationDTO) {
        resourcesMapper.updateEvaluation(id, expEvaluationDTO.getLikes(), expEvaluationDTO.getEasyCount(), expEvaluationDTO.getHardCount());
    }

}
