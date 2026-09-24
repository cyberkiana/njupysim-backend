package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ExpListDTO;
import org.njupt.njuptphysim.pojo.dto.ResourcesUpdateDTO;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.vo.ExpVO;

import java.util.List;

public interface ResourcesService {

    /**
     * 添加资源
     * @param resources
     * @return
     */
    void addResources(  Resources resources );

    /**
     * 删除资源
     * @param name
     * @return
     */
    void deleteResources(String name);

    /**
     * 修改资源
     * @param resources
     * @return
     */
    void updateResources(ResourcesUpdateDTO resources);

    /**
     * 根据id查找实验
     * @param id 实验id
     * @return 实验信息
     */
    Resources getResourcesById(int id);

    /**
     * 查找现有实验list
     * @return id,title
     */
    List<ExpListDTO> getAllExpList();

    /**
     * 实验评价：每个用户对每个实验仅能评价一次（点赞/十分简单/十分困难三选一）
     * @param resourceId 实验id
     * @param userId 用户id
     * @param type likes/easy/hard
     */
    void evaluate(int resourceId, String userId, String type);

    /**
     * 查询当前用户对某实验的评价类型
     * @return likes/easy/hard，未评价时为 null
     */
    String getMyEvaluationType(int resourceId, String userId);

    /**
     * 查找班级布置的实验
     * @param clazzId 班级id
     * @return id,title
     */
    List<ExpListDTO> getClazzExpList(String clazzId);

    /**
     * 根据实验id查询实验详情
     * @param id 实验id
     * @return ExpVO
     */
    ExpVO getExpDetail(int id);



}
