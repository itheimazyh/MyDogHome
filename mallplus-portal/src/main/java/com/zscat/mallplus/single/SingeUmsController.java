package com.zscat.mallplus.single;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.service.ISysAreaService;
import com.zscat.mallplus.cms.service.ISysSchoolService;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.sys.entity.SysArea;
import com.zscat.mallplus.sys.entity.SysSchool;
import com.zscat.mallplus.ums.entity.UmsEmployInfo;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberMemberTagRelation;
import com.zscat.mallplus.ums.mapper.UmsEmployInfoMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.ums.service.IUmsMemberMemberTagRelationService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "UmsController", description = "会员关系管理")
@RequestMapping("/api/single/user")
public class SingeUmsController extends ApiBaseAction {

    @Resource
    private ISysSchoolService schoolService;
    @Resource
    private IUmsMemberService memberService;
    @Resource
    private ISysAreaService areaService;
    @Resource
    private IUmsMemberMemberTagRelationService memberTagService;
    @Resource
    private UmsRewardLogMapper rewardLogMapper;
    @Resource
    private UmsEmployInfoMapper employInfoMapper;
    @Resource
    private RedisService redisService;
    @ApiOperation("获取会员详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Object detail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        UmsMember member = memberService.getById(id);
        return new CommonResult().success(member);
    }

    @IgnoreAuth
    @ApiOperation(value = "查询学校列表")
    @GetMapping(value = "/school/list")
    @SysLog(MODULE = "ums", REMARK = "查询学校列表")
    public Object subjectList(SysSchool entity,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(schoolService.page(new Page<SysSchool>(pageNum, pageSize), new QueryWrapper<>(entity)));
    }

    @IgnoreAuth
    @SysLog(MODULE = "ums", REMARK = "根据pid查询区域")
    @ApiOperation("根据pid查询区域")
    @RequestMapping(value = "/getAreaByPid", method = RequestMethod.GET)
    public Object getAreaByPid(@RequestParam(value = "pid", required = false, defaultValue = "0") Long pid) {
        SysArea queryPid = new SysArea();
        queryPid.setPid(pid);
        List<SysArea> list = areaService.list(new QueryWrapper<SysArea>(queryPid));
        return new CommonResult().success(list);
    }

    @ApiOperation("更新会员信息")
    @SysLog(MODULE = "ums", REMARK = "更新会员信息")
    @PostMapping(value = "/updateMember")
    public Object createGoods(UmsMember member) {
        UmsMember member1 = UserUtils.getCurrentMember();
        member.setId(member1.getId());
        return memberService.updateById(member);
    }
    @ApiOperation("添加招聘")
    @SysLog(MODULE = "ums", REMARK = "添加招聘")
    @PostMapping(value = "/addJob")
    public Object addJob(UmsEmployInfo member) {
        return employInfoMapper.insert(member);
    }
    @ApiOperation(value = "会员绑定学校")
    @PostMapping(value = "/bindSchool")
    @SysLog(MODULE = "ums", REMARK = "会员绑定学校")
    public Object bindSchool(@RequestParam(value = "schoolId", required = true) Long schoolId) {
        try {
            UmsMember member = UserUtils.getCurrentMember();

            String countKey = "bindSchool:count:" + LocalDate.now().toString() + ":" + member.getId();
            String value = redisService.get(countKey);
            if (value != null) {
                Integer count = Integer.valueOf(value);
                if (count > 100) {
                    return new CommonResult().success("已超过当天最大次数");
                }
            }
            member.setSchoolId(schoolId);
            memberService.updateById(member);
            // 当天发送验证码次数+1
            redisService.increment(countKey, 1L);
            redisService.expire(countKey, 1 * 3600 * 24*365);
            return new CommonResult().success("绑定学校成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed("绑定学校失败");
        }
    }

    @ApiOperation(value = "会员绑定区域")
    @PostMapping(value = "/bindArea")
    @SysLog(MODULE = "ums", REMARK = "会员绑定区域")
    public Object bindArea(@RequestParam(value = "areaId", required = true) Long areaId) {
        try {
            UmsMember member = UserUtils.getCurrentMember();
            String countKey = "bindArea:count:" + LocalDate.now().toString() + ":" + member.getId();
            String value = redisService.get(countKey);
            if (value != null) {
                Integer count = Integer.valueOf(value);
                if (count > 100) {
                    return new CommonResult().success("已超过当天最大次数");
                }
            }

           SysArea area = areaService.getById(areaId);
           if (area==null){
               return new CommonResult().failed("区域不存在");
           }
            member.setAreaId(areaId);
            memberService.updateById(member);
            // 当天发送验证码次数+1
            redisService.increment(countKey, 1L);
            redisService.expire(countKey, 1 * 3600 * 24*365);
            return new CommonResult().success(area);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed("绑定区域失败");
        }
    }

    /*@ApiOperation(value = "会员绑定区域")
    @PostMapping(value = "/bindArea")
    @SysLog(MODULE = "ums", REMARK = "会员绑定区域")
    public Object bindArea(@RequestParam(value = "areaIds", required = true) String areaIds) {
        try {
            if (ValidatorUtils.empty(areaIds)) {
                return new CommonResult().failed("请选择区域");
            }
            UmsMember member = UserUtils.getCurrentMember();
            String[] areIdList = areaIds.split(",");
            List<UmsMemberMemberTagRelation> list = new ArrayList<>();
            for (String id : areIdList) {
                UmsMemberMemberTagRelation tag = new UmsMemberMemberTagRelation();
                tag.setMemberId(member.getId());
                tag.setTagId(Long.valueOf(id));
                list.add(tag);
            }
            if (list != null && list.size() > 0) {
                memberTagService.saveBatch(list);
            }
            return new CommonResult().success("绑定区域成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed("绑定区域失败");
        }
    }*/
}