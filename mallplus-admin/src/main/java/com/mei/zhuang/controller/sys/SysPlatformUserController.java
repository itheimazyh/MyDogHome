package com.mei.zhuang.controller.sys;


import com.arvato.admin.constant.AdminCommonConstant;
import com.arvato.admin.constant.SysPlatformUserStatus;
import com.arvato.admin.constant.SysTenantStatus;
import com.arvato.admin.dto.SysPlatformUserPostData;
import com.arvato.admin.dto.SysPlatformUserPutData;
import com.arvato.admin.service.ISysPlatformUserService;
import com.arvato.common.dto.SysPlatformUserPagingData;
import com.arvato.common.dto.SysPlatformUserPagingParam;
import com.arvato.common.msg.DictData;
import com.arvato.common.orm.model.SysPlatformUser;
import com.arvato.common.vo.returnformat.BaseResponse;
import com.arvato.common.vo.returnformat.TableData;
import com.arvato.utils.annotation.SysLog;
import com.arvato.utils.util.StringHelper;
import com.baomidou.mybatisplus.mapper.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"平台账户管理CRUD接口"})
@RestController
@RequestMapping("/sysPlatformUsers")
public class SysPlatformUserController {

    @Autowired
    private ISysPlatformUserService service;

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "平台账户列表")
    @ApiOperation("平台账户列表")
    @GetMapping
    public BaseResponse<TableData<SysPlatformUserPagingData>> list(SysPlatformUserPagingParam param) {
        return BaseResponse.successResponnse(
                new TableData<>()
                        .total(this.service.getPagingTotal(param))
                        .rows(this.service.getPagingList(param))
        );
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "平台账户状态字典")
    @ApiOperation("平台账户状态字典")
    @GetMapping("/status/dict")
    public BaseResponse<List<DictData>> getStatusDictList() {
        return BaseResponse.successResponnse(
                SysPlatformUserStatus.toDictList()
        );
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "新增平台账户")
    @ApiOperation("新增平台账户")
    @PostMapping
    public BaseResponse add(SysPlatformUserPostData tenantPostData) {
        Assert.isTrue(
                StringUtils.isNotBlank(tenantPostData.getUsername()),
                "请填写账号"
        );
        Assert.isTrue(
                StringUtils.isNotBlank(tenantPostData.getPassword()),
                "请填写密码"
        );
        Assert.isTrue(StringHelper.isPassword(tenantPostData.getPassword()),
                "请填写6-16位字符，不能包含空格、中文"
        );
        Assert.isTrue(
                this.service.selectCount(new QueryWrapper<>(
                        new SysPlatformUser().setUsername(tenantPostData.getUsername())
                )) == 0,
                "该账号已被使用"
        );

        tenantPostData.setPassword(this.service.encryptPassword(tenantPostData.getPassword()));

        SysPlatformUser sysPlatformUser = new SysPlatformUser()
                .setUserType(AdminCommonConstant.SYS_PLATFORM_USER_TYPE_TENANT_ADMIN)
                .setUsername(tenantPostData.getUsername())
                .setPassword(tenantPostData.getPassword())
                .setManageTenantIds(tenantPostData.getManageTenantIds())
                .setStatus(SysPlatformUserStatus.NORMAL.getStatus());
        this.service.insert(sysPlatformUser);

        this.service.platformUserInit(sysPlatformUser);

        return BaseResponse.successResponnse();
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "更新平台账户")
    @ApiOperation("更新平台账户")
    @PutMapping
    public BaseResponse update(SysPlatformUserPutData tenantPutData) {

        this.service.updateById(
                new SysPlatformUser()
                        .setId(tenantPutData.getId())
                        .setManageTenantIds(tenantPutData.getManageTenantIds())
        );

        return BaseResponse.successResponnse();
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "平台账户状态设置")
    @ApiOperation("平台账户状态设置")
    @PutMapping("/{id}/status")
    public BaseResponse update(@PathVariable("id") int id, boolean enable) {

        this.service.updateById(
                new SysPlatformUser()
                        .setId(id)
                        .setStatus(enable ? SysTenantStatus.ENABLE.getStatus() : SysTenantStatus.DISABLE.getStatus())
        );

        return BaseResponse.successResponnse();
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "平台账户状态设置")
    @ApiOperation("平台账户状态设置")
    @PutMapping("/{id}/password")
    public BaseResponse update(@PathVariable("id") int id, String newPassword) {

        Assert.isTrue(StringUtils.isNotBlank(newPassword), "请填写密码");
        Assert.isTrue(StringHelper.isPassword(newPassword),
                "请填写6-16位字符，不能包含空格、中文"
        );

        this.service.updateById(
                new SysPlatformUser()
                        .setId(id)
                        .setPassword(this.service.encryptPassword(newPassword))
        );

        return BaseResponse.successResponnse();
    }

    @SysLog(MODULE = "平台账户管理CRUD接口", REMARK = "获取平台账号详情")
    @ApiOperation("获取平台账号详情")
    @GetMapping("/{username}")
    public BaseResponse<SysPlatformUser> getById(@PathVariable("username") String username) {
        return BaseResponse.successResponnse(this.service.selectByUsername(username));
    }

}

