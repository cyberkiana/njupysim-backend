package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.ResourcesUpdateDTO;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ricefoodie
 */
@RestController
@RequestMapping("/admin/res")
public class ResourcesController {

    @Autowired
    private ResourcesService resourcesService;



    /**
     * 添加资源
     * @param resources
     * @return
     */
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
    @PostMapping("/update")
    public Result updateResources(@RequestBody ResourcesUpdateDTO resources)
    {
        resourcesService.updateResources( resources );
        return Result.success();
    }




}
