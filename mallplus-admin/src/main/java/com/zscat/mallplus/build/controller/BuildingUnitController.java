package com.zscat.mallplus.build.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.build.entity.BuildingUnit;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zscat
 * @since 2019-11-27
 */
@Slf4j
@RestController
@RequestMapping("/building/unit")
public class BuildingUnitController {

    @Resource
    private com.zscat.mallplus.build.service.IBuildingUnitService IBuildingUnitService;

    @SysLog(MODULE = "cms", REMARK = "根据条件查询所有房屋单元列表")
    @ApiOperation("根据条件查询所有房屋单元列表")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('building:unit:read')")
    public Object getBuildingUnitByPage(BuildingUnit entity,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        try {
            return new CommonResult().success(IBuildingUnitService.page(new Page<BuildingUnit>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            log.error("根据条件查询所有房屋单元列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "cms", REMARK = "保存房屋单元")
    @ApiOperation("保存房屋单元")
    @PostMapping(value = "/create")

    public Object saveBuildingUnit(@RequestBody BuildingUnit entity) {
        try {
            if (ValidatorUtils.empty(entity.getFloorId())){
                return new CommonResult().failed("请选择楼");
            }
            entity.setCreateTime(new Date());
            if (IBuildingUnitService.save(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("保存房屋单元：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "cms", REMARK = "更新房屋单元")
    @ApiOperation("更新房屋单元")
    @PostMapping(value = "/update/{id}")

    public Object updateBuildingUnit(@RequestBody BuildingUnit entity) {
        try {
            if (IBuildingUnitService.updateById(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("更新房屋单元：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "cms", REMARK = "删除房屋单元")
    @ApiOperation("删除房屋单元")
    @GetMapping(value = "/delete/{id}")

    public Object deleteBuildingUnit(@ApiParam("房屋单元id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("房屋单元id");
            }
            if (IBuildingUnitService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("删除房屋单元：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "cms", REMARK = "给房屋单元分配房屋单元")
    @ApiOperation("查询房屋单元明细")
    @GetMapping(value = "/{id}")

    public Object getBuildingUnitById(@ApiParam("房屋单元id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("房屋单元id");
            }
            BuildingUnit coupon = IBuildingUnitService.getById(id);
            return new CommonResult().success(coupon);
        } catch (Exception e) {
            log.error("查询房屋单元明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @ApiOperation(value = "批量删除房屋单元")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.GET)
    @ResponseBody
    @SysLog(MODULE = "pms", REMARK = "批量删除房屋单元")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = IBuildingUnitService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

}



