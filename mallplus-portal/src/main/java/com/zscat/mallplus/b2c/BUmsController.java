package com.zscat.mallplus.b2c;


import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.fenxiao.entity.FenxiaoRecords;
import com.zscat.mallplus.fenxiao.mapper.FenxiaoRecordsMapper;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.sys.entity.SysArea;
import com.zscat.mallplus.sys.entity.SysShop;
import com.zscat.mallplus.sys.mapper.SysAreaMapper;
import com.zscat.mallplus.sys.mapper.SysShopMapper;
import com.zscat.mallplus.sys.mapper.SysStoreMapper;
import com.zscat.mallplus.ums.entity.*;
import com.zscat.mallplus.ums.service.*;
import com.zscat.mallplus.ums.vo.UserBankcardsVo;
import com.zscat.mallplus.util.JsonUtils;
import com.zscat.mallplus.utils.BankUtil;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页内容管理Controller
 * https://github.com/shenzhuan/mallplus on 2019/1/28.
 */
@Slf4j
@RestController
@Api(tags = "HomeController", description = "首页内容管理")
public class BUmsController {

    @Resource
    FenxiaoRecordsMapper fenxiaoRecordsMapper;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private RedisService redisService;
    @Autowired
    private IUmsMemberService memberService;
    @Resource
    private IUmsMemberLevelService memberLevelService;
    @Resource
    private IUmsMemberBlanceLogService blanceLogService;
    @Resource
    private IUmsIntegrationChangeHistoryService integrationChangeHistoryService;
    @Resource
    private SysAreaMapper areaMapper;
    @Resource
    private SysStoreMapper storeMapper;
    @Resource
    private IUserBankcardsService bankcardsService;
    @Resource
    private SysShopMapper sysShopMapper;
    @Resource
    private IOmsOrderService orderService;

    public static Object getObject(UmsMember member, IUmsMemberService memberService) {
        if (member == null) {
            return new CommonResult().paramFailed();
        }
        UmsMember member1 = memberService.getNewCurrentMember();
        if (member1 != null && member1.getId() != null) {
            member.setId(member1.getId());
            return new CommonResult().success(memberService.updateById(member));
        }
        return new CommonResult().failed();
    }

    @ApiOperation("更新会员信息")
    @SysLog(MODULE = "ums", REMARK = "更新会员信息")
    @PostMapping(value = "/userInfo")
    public Object userInfo() {
        return new CommonResult().success(memberService.getNewCurrentMember());
    }

    @ApiOperation("更新会员信息")
    @SysLog(MODULE = "ums", REMARK = "更新会员信息")
    @PostMapping(value = "/user.editinfo")
    public Object updateMember(@RequestParam(value = "nickname", required = false) String nickname,
                               @RequestParam(value = "icon", required = false) String icon,
                               @RequestParam(value = "invitecode", required = false) String invitecode,
                               @RequestParam(value = "gender", required = false) Integer gender
    ) {
        UmsMember m = new UmsMember();
        m.setId(memberService.getNewCurrentMember().getId());
        if (ValidatorUtils.notEmpty(nickname)) {
            m.setNickname(nickname);
        }
        if (ValidatorUtils.notEmpty(icon)) {
            m.setIcon(icon);
        }
        if (ValidatorUtils.notEmpty(gender)) {
            m.setGender(gender);
        }
        if (ValidatorUtils.notEmpty(invitecode)) {
            if (memberService.getById(invitecode) != null) {
                m.setInvitecode(invitecode);
            } else {
                return new CommonResult().failed();
            }
        }
        return memberService.updateById(m);
    }

    @IgnoreAuth
    @ApiOperation(value = "查询余额列表")
    @PostMapping(value = "/user.balancelist")
    @SysLog(MODULE = "ums", REMARK = "查询余额列表")
    public Object balancelist(UmsMemberBlanceLog entity,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        if (memberService.getNewCurrentMember() == null) {
            return new CommonResult().success();
        }
        entity.setMemberId(memberService.getNewCurrentMember().getId());
        if (entity == null || ValidatorUtils.empty(entity.getType())) {
            return new CommonResult().success(blanceLogService.page(new Page<UmsMemberBlanceLog>(pageNum, pageSize), new QueryWrapper<>()));
        }
        return new CommonResult().success(blanceLogService.page(new Page<UmsMemberBlanceLog>(pageNum, pageSize), new QueryWrapper<>(entity)));
    }

    @IgnoreAuth
    @ApiOperation(value = "查询会员等级列表")
    @PostMapping(value = "/user.levellist")
    @SysLog(MODULE = "ums", REMARK = "查询会员等级列表")
    public Object levellist() {
        return new CommonResult().success(memberLevelService.list(new QueryWrapper<>()));
    }

    @IgnoreAuth
    @ApiOperation("获取会员等级详情信息")
    @RequestMapping(value = "/user.leveldetail", method = RequestMethod.POST)
    @ResponseBody
    public Object leveldetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return new CommonResult().success(memberLevelService.getById(id));
    }

    @IgnoreAuth
    @ApiOperation(value = "查询会员和积分消费记录列表")
    @PostMapping(value = "/user.userpointlog")
    @SysLog(MODULE = "ums", REMARK = "查询会员和积分消费记录")
    public Object userpointlog(UmsIntegrationChangeHistory entity,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        Map<String, Object> map = new HashedMap();
        map.put("member", memberService.getById(memberService.getNewCurrentMember().getId()));
        entity.setMemberId(memberService.getNewCurrentMember().getId());
        map.put("intList", integrationChangeHistoryService.page(new Page<UmsIntegrationChangeHistory>(pageNum, pageSize), new QueryWrapper<>(entity)));
        return new CommonResult().success(map);
    }

    @IgnoreAuth
    @ApiOperation(value = "查询区域列表")
    @PostMapping(value = "/user.getareaid")
    @SysLog(MODULE = "ums", REMARK = "查询学校列表")
    public Object getareaid(SysArea entity) {
        return new CommonResult().success(areaMapper.selectOne(new QueryWrapper<>(entity)));
    }

    @SysLog(MODULE = "cms", REMARK = "判断是否签到")
    @ApiOperation(value = "判断是否签到")
    @PostMapping(value = "/user.issign")
    public Object issign(CmsSubject subject, BindingResult result) {
        UmsMember member = memberService.getNewCurrentMember();
        return new CommonResult().success();

    }

    @SysLog(MODULE = "cms", REMARK = "签到接口")
    @ApiOperation(value = "签到接口")
    @PostMapping(value = "/user.sign")
    public Object sign(CmsSubject subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();

        return new CommonResult().success();
    }

    @ApiOperation("添加银行卡")
    @RequestMapping(value = "/user.addbankcard")
    @ResponseBody
    public Object saveusership(UserBankcards address) {
        boolean count = false;
        UmsMember umsMember = memberService.getNewCurrentMember();
        if (ValidatorUtils.notEmpty(address.getIsDefault()) && address.getIsDefault() == 1) {
            UserBankcards query = new UserBankcards();
            query.setIsDefault(2);
            bankcardsService.update(query, new QueryWrapper<UserBankcards>().eq("user_id", umsMember.getId()));
        }
        address.setUserId(umsMember.getId());
        if (address.getId() != null && address.getId() > 0) {
            bankcardsService.updateById(address);
        } else {
            bankcardsService.save(address);
        }
        return new CommonResult().success();
    }

    @IgnoreAuth
    @ApiOperation("获取我的银行卡列表")
    @RequestMapping(value = "/user.getbankcardlist", method = RequestMethod.POST)
    @ResponseBody
    public Object getbankcardlist() {
        UmsMember umsMember = memberService.getNewCurrentMember();
        if (umsMember != null && umsMember.getId() != null) {
            List<UserBankcards> addressList = bankcardsService.list(new QueryWrapper<UserBankcards>().eq("user_id", umsMember.getId()));
            return new CommonResult().success(addressList);
        }
        return new ArrayList<UmsMemberReceiveAddress>();
    }

    @IgnoreAuth
    @ApiOperation("获取银行卡信息")
    @RequestMapping(value = "/user.getbankcardinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object getbankcardinfo(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return new CommonResult().success(bankcardsService.getById(id));
    }

    @IgnoreAuth
    @ApiOperation("获取银行卡组织信息")
    @RequestMapping(value = "/user.getbankcardorganization", method = RequestMethod.POST)
    @ResponseBody
    public Object getbankcardorganization(@RequestParam(value = "card_code", required = true) String card_code) {
        try {
            String json1 = HttpUtil.get("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=" + card_code + "&cardBinCheck=true");
            // 转为 Json 对象
            UserBankcardsVo vo = JsonUtils.fromJson(json1, UserBankcardsVo.class);
            // 北京农村商业银行·凤凰卡
            String cardName = BankUtil.getNameOfBank(card_code);
            vo.setName(cardName.split("·")[0]);
            vo.setTypeName(cardName.split("·")[1]);
            return new CommonResult().success(vo);
        } catch (Exception e) {
            return new CommonResult().failed();
        }

    }

    @IgnoreAuth
    @ApiOperation("获取默认的银行卡")
    @RequestMapping(value = "/user.getdefaultbankcard", method = RequestMethod.POST)
    @ResponseBody
    public Object getItemDefautl() {
        UserBankcards address = bankcardsService.getOne(new QueryWrapper<UserBankcards>().eq("user_id", memberService.getNewCurrentMember().getId()).eq("is_default", 1));
        if (address != null && address.getId() > 0) {
            return new CommonResult().success(address);
        }
        List<UserBankcards> addressList = bankcardsService.list(new QueryWrapper<UserBankcards>().eq("user_id", memberService.getNewCurrentMember().getId()));
        if (addressList != null && addressList.size() > 0) {
            return new CommonResult().success(addressList.get(0));
        }
        return new CommonResult().success();
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation("设置默认银行卡")
    @RequestMapping(value = "/user.setdefaultbankcard")
    @ResponseBody
    public Object setDefault(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        int count = bankcardsService.setDefault(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("申请用户提现")
    @PostMapping(value = "/user.cash")
    @ResponseBody
    public Object cash(UmsMemberBlanceLog blanceLog) {
        try {
            return new CommonResult().success(memberService.withDraw(blanceLog));
        } catch (Exception e) {
            return new CommonResult().failed(e.getMessage());
        }
    }

    @ApiOperation("用户推荐列表")
    @PostMapping(value = "/user.recommend")
    @ResponseBody
    public Object inviteUser(UmsMember entity,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(memberService.page(new Page<UmsMember>(pageNum, pageSize), new QueryWrapper<UmsMember>().eq("invitecode", memberService.getNewCurrentMember().getId())));
    }

    @ApiOperation("邀请码")
    @PostMapping(value = "/user.sharecode")
    @ResponseBody
    public Object shareCode(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        int count = bankcardsService.setDefault(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("获取分销商进度状态")
    @PostMapping(value = "/distribution_center-api-info")
    @ResponseBody
    public Object getDistributioninfo(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        UmsMember member = memberService.getNewCurrentMember();
        UmsMember newMember = memberService.getById(member.getId());

        List<FenxiaoRecords> recordss = fenxiaoRecordsMapper.selectList(new QueryWrapper<FenxiaoRecords>().eq("member_id", member.getId()));
        newMember.setBuyCount(recordss.size());
        BigDecimal totalMoney = BigDecimal.ZERO;
        BigDecimal sucessMoney = BigDecimal.ZERO; // 结算
        for (FenxiaoRecords fenxiaoRecords : recordss) {
            totalMoney = fenxiaoRecords.getMoney().add(totalMoney);
            if ("2".equals(fenxiaoRecords.getStatus())) {
                sucessMoney = fenxiaoRecords.getMoney().add(sucessMoney);
            }
        }
        newMember.setBlance(totalMoney);
        newMember.setBuyMoney(sucessMoney);
        return new CommonResult().success(newMember);
    }

    @ApiOperation("申请分销商")
    @PostMapping(value = "/distribution_center-api-applydistribution")
    @ResponseBody
    public Object applyDistribution(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        int count = bankcardsService.setDefault(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("店铺设置")
    @PostMapping(value = "/distribution_center-api-setstore")
    @ResponseBody
    public Object setStore(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        int count = bankcardsService.setDefault(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("我的分销订单")
    @PostMapping(value = "/distribution_center-api-getstoreinfo")
    @ResponseBody
    public Object distribution_centerApiGetstoreinfo(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        int count = bankcardsService.setDefault(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("我的分销订单")
    @PostMapping(value = "/distribution_center-api-myorder")
    @ResponseBody
    public Object getDistributionOrder(@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        UmsMember member = memberService.getNewCurrentMember();
        UmsMember newMember = memberService.getById(member.getId());
        Integer count = memberService.count(new QueryWrapper<UmsMember>().eq("invitecode", member.getId()));
        newMember.setBuyCount(count);
        List<FenxiaoRecords> recordss = fenxiaoRecordsMapper.selectList(new QueryWrapper<FenxiaoRecords>().eq("member_id", member.getId()));
        List<Integer> ids = recordss.stream()
                .map(FenxiaoRecords::getId)
                .collect(Collectors.toList());
        if (ids != null && ids.size() > 0) {
            return new CommonResult().success(orderService.page(new Page<OmsOrder>(pageNum, pageSize), new QueryWrapper<>(new OmsOrder()).in("id", ids)));
        }
        return new CommonResult().success();
    }

    @ApiOperation("判断是否是店员")
    @PostMapping(value = "/store.isclerk")
    @ResponseBody
    public Object ischeck(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {

        return new CommonResult().failed();
    }


    @ApiOperation("店铺详情")
    @PostMapping(value = "/storeDetail")
    @ResponseBody
    public Object storeDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
        return new CommonResult().success(storeMapper.selectById(id));
    }

    @IgnoreAuth
    @ApiOperation(value = "查询门店管理列表")
    @PostMapping(value = "/user.shoplist")
    @SysLog(MODULE = "ums", REMARK = "查询门店管理列表")
    public Object shoplist(@RequestParam(value = "storeId", required = true) Long storeId) {
        return new CommonResult().success(sysShopMapper.selectList(new QueryWrapper<SysShop>().eq("store_id", storeId)));
    }

    @ApiOperation("门店详情")
    @PostMapping(value = "/shopDetail")
    @ResponseBody
    public Object shopDetail(@RequestParam(value = "storeId", required = true) Integer storeId) {
        List<SysShop> list = sysShopMapper.selectList(new QueryWrapper<SysShop>().eq("store_id", storeId));
        if (list != null && list.size() > 0) {
            return new CommonResult().success(list.get(0));
        }
        return new CommonResult().success();
    }
}
