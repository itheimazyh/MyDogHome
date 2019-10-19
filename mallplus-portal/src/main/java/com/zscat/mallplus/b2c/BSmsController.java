package com.zscat.mallplus.b2c;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.enums.ConstansValue;
import com.zscat.mallplus.pms.entity.PmsFavorite;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.service.IPmsFavoriteService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.pms.vo.GoodsDetailResult;
import com.zscat.mallplus.single.ApiBaseAction;
import com.zscat.mallplus.sms.entity.*;
import com.zscat.mallplus.sms.service.ISmsBasicGiftsService;
import com.zscat.mallplus.sms.service.ISmsBasicMarkingService;
import com.zscat.mallplus.sms.service.ISmsCouponService;
import com.zscat.mallplus.sms.service.ISmsGroupActivityService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.ums.service.impl.RedisUtil;
import com.zscat.mallplus.util.JsonUtils;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import com.zscat.mallplus.vo.Rediskey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@Slf4j
@RestController
@Api(tags = "SingeMarkingController", description = "营销管理")
public class BSmsController extends ApiBaseAction {

    @Resource
    private ISmsBasicGiftsService basicGiftsService;
    @Resource
    private ISmsBasicMarkingService basicMarkingService;
    @Resource
    private RedisService redisService;
    @Resource
    private ISmsGroupActivityService smsGroupActivityService;
    @Resource
    private IPmsProductService productService;
    @Autowired
    private ISmsCouponService couponService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private IPmsFavoriteService favoriteService;

    @ApiOperation("领取指定优惠券")
    @PostMapping(value = "/coupon.getcoupon")
    public Object add(@RequestParam(value = "couponId", required = true) Long couponId) {
        return couponService.add(couponId);
    }
    @ApiOperation("批量领取指定优惠券")
    @PostMapping(value = "/batch.getcoupon")
    public Object addbatch(@RequestParam(value = "couponIds", required = true) String couponIds) {
        return couponService.addbatch(couponIds);
    }
    @ApiOperation("获取用户优惠券列表")
    @ApiImplicitParam(name = "useStatus", value = "优惠券筛选类型:0->未使用；1->已使用；2->已过期",
            allowableValues = "0,1,2", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/coupon.usercoupon", method = RequestMethod.POST)
    public Object list(@RequestParam(value = "useStatus", required = false) Integer useStatus) {
        List<SmsCouponHistory> couponHistoryList = couponService.listMemberCoupon(useStatus);
        return new CommonResult().success(couponHistoryList);
    }

    @ApiOperation("获取没有领取的优惠券列表")
    @RequestMapping(value = "/coupon.couponlist", method = RequestMethod.POST)
    public Object couponlist() {
        List<SmsCoupon> couponHistoryList = couponService.selectNotRecive();
        return new CommonResult().success(couponHistoryList);
    }




}
