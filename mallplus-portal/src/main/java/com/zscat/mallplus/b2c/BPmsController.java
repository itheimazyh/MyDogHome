package com.zscat.mallplus.b2c;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.CmsFavorite;
import com.zscat.mallplus.cms.service.ICmsFavoriteService;
import com.zscat.mallplus.cms.service.ICmsSubjectCategoryService;
import com.zscat.mallplus.cms.service.ICmsSubjectCommentService;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.enums.OrderStatus;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.mapper.OmsOrderMapper;
import com.zscat.mallplus.pms.entity.*;
import com.zscat.mallplus.pms.mapper.PmsProductCategoryMapper;
import com.zscat.mallplus.pms.mapper.PmsProductMapper;
import com.zscat.mallplus.pms.service.*;
import com.zscat.mallplus.pms.vo.*;
import com.zscat.mallplus.single.ApiBaseAction;
import com.zscat.mallplus.sms.entity.SmsGroup;
import com.zscat.mallplus.sms.entity.SmsGroupMember;
import com.zscat.mallplus.sms.mapper.SmsGroupMapper;
import com.zscat.mallplus.sms.mapper.SmsGroupMemberMapper;
import com.zscat.mallplus.sms.service.ISmsGroupService;
import com.zscat.mallplus.sms.service.ISmsHomeAdvertiseService;
import com.zscat.mallplus.sys.entity.SysStore;
import com.zscat.mallplus.sys.mapper.SysStoreMapper;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberLevel;
import com.zscat.mallplus.ums.entity.UmsMemberReceiveAddress;
import com.zscat.mallplus.ums.service.IUmsMemberLevelService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.ums.service.impl.RedisUtil;
import com.zscat.mallplus.util.DateUtils;
import com.zscat.mallplus.util.GoodsUtils;
import com.zscat.mallplus.util.JsonUtils;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import com.zscat.mallplus.vo.Rediskey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.StoreManager;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@Slf4j
@RestController
@Api(tags = "SingePmsController", description = "商品关系管理")
public class BPmsController extends ApiBaseAction {


    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ISmsGroupService groupService;
    @Resource
    private SmsGroupMapper groupMapper;
    @Resource
    private IUmsMemberLevelService memberLevelService;
    @Resource
    private IPmsProductService pmsProductService;
    @Resource
    private IPmsProductAttributeCategoryService productAttributeCategoryService;
    @Resource
    private IPmsProductCategoryService productCategoryService;
    @Resource
    private IPmsBrandService IPmsBrandService;

    @Resource
    private ICmsSubjectCategoryService subjectCategoryService;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private ICmsSubjectCommentService commentService;
    @Autowired
    private ISmsHomeAdvertiseService advertiseService;
    @Resource
    private PmsProductMapper productMapper;
    @Resource
    private RedisService redisService;
    @Autowired
    private IPmsProductConsultService pmsProductConsultService;
    @Autowired
    private IPmsFavoriteService favoriteService;
    @Resource
    private SmsGroupMemberMapper groupMemberMapper;
    @Resource
    private  PmsProductCategoryMapper categoryMapper;
    @Resource
    private IPmsGiftsService giftsService;
    @Resource
    private IPmsGiftsCategoryService giftsCategoryService;
    @Resource
    private SysStoreMapper storeMapper;
    @Resource
    private OmsOrderMapper omsOrderMapper;
    @SysLog(MODULE = "pms", REMARK = "查询商品详情信息")
    @IgnoreAuth
    @PostMapping(value = "/goods.getdetial")
    @ApiOperation(value = "查询商品详情信息")
    public Object queryProductDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        GoodsDetailResult goods = null;
        try {
            goods = pmsProductService.getGoodsRedisById(id);
                    //JsonUtils.jsonToPojo(redisService.get(String.format(Rediskey.GOODSDETAIL, id+"")), GoodsDetailResult.class);
            if (ValidatorUtils.empty(goods) || ValidatorUtils.empty(goods.getGoods())){
                log.info("redis缓存失效："+id);
                goods = pmsProductService.getGoodsRedisById(id);
            }
        } catch (Exception e) {
            log.info("redis缓存失效："+id);
            goods = pmsProductService.getGoodsRedisById(id);
        }
        Map<String, Object> map = new HashMap<>();
        UmsMember umsMember = UserUtils.getCurrentMember();
        if (umsMember != null && umsMember.getId() != null) {
            PmsProduct p = goods.getGoods();
            PmsFavorite query = new PmsFavorite();
            query.setObjId(p.getId());
            query.setMemberId(umsMember.getId());
            query.setType(1);
            PmsFavorite findCollection = favoriteService.getOne(new QueryWrapper<>(query));
            if(findCollection!=null){
                map.put("favorite", true);
            }else{
                map.put("favorite", false);
            }
        }
        //记录浏览量到redis,然后定时更新到数据库
        String key=Rediskey.GOODS_VIEWCOUNT_CODE+id;
        //找到redis中该篇文章的点赞数，如果不存在则向redis中添加一条
        Map<Object,Object> viewCountItem=redisUtil.hGetAll(Rediskey.GOODS_VIEWCOUNT_KEY);
        Integer viewCount=0;
        if(!viewCountItem.isEmpty()){
            if(viewCountItem.containsKey(key)){
                viewCount=Integer.parseInt(viewCountItem.get(key).toString())+1;
                redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,viewCount+"");
            }else {
                redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,1+"");
            }
        }else{
            redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,1+"");
        }

        map.put("goods", goods);
        return new CommonResult().success(map);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品列表")
    @PostMapping(value = "/goods.getlist")
    public Object goodsList(PmsProduct product,
                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        product.setPublishStatus(1);
        product.setVerifyStatus(1);
        product.setMemberId(null);
        IPage<PmsProduct> list;
        if (ValidatorUtils.notEmpty(product.getKeyword())){
            list = pmsProductService.page(new Page<PmsProduct>(pageNum, pageSize), new QueryWrapper<>(product).like("name",product.getKeyword()));
        }else{
            list = pmsProductService.page(new Page<PmsProduct>(pageNum, pageSize), new QueryWrapper<>(product));
        }
        return new CommonResult().success(list);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品分类列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品分类列表")
    @PostMapping(value = "/productCategoryList")
    public Object productCategoryList(PmsProductCategory productCategory,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(productCategoryService.page(new Page<PmsProductCategory>(pageNum, pageSize), new QueryWrapper<>(productCategory)));
    }


    @ApiOperation("创建商品")
    @SysLog(MODULE = "pms", REMARK = "创建商品")
    @PostMapping(value = "/createGoods")
    public Object createGoods(PmsProduct productParam) {
        CommonResult commonResult;
        UmsMember member = UserUtils.getCurrentMember();
        if (member.getMemberLevelId() > 0) {
            UmsMemberLevel memberLevel = memberLevelService.getById(member.getMemberLevelId());
            Integer countGoodsByToday  = pmsProductService.countGoodsByToday(member.getId());
            if (ValidatorUtils.empty(countGoodsByToday)){
                countGoodsByToday=0;
            }
            if (countGoodsByToday > memberLevel.getGoodscount()) {
                commonResult = new CommonResult().failed("你今天已经有发" + countGoodsByToday + "个商品");
                return commonResult;
            }
        }else {
            return new CommonResult().success("没有设置会员等级");
        }
        if (productParam.getQsType()==1){
            productParam.setSchoolName(member.getSchoolName());
            productParam.setSchoolId(member.getSchoolId());
        }else {
            productParam.setAreaName(member.getAreaName());
            productParam.setAreaId(member.getAreaId());
        }
        productParam.setMemberId(member.getId());
        productParam.setCreateTime(new Date());
        boolean count = pmsProductService.save(productParam);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }


    @SysLog(MODULE = "pms", REMARK = "根据条件查询所有品牌表列表")
    @ApiOperation("根据条件查询所有品牌表列表")
    @PostMapping(value = "/brand/list")
    public Object getPmsBrandByPage(PmsBrand entity,
                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        try {
            return new CommonResult().success(IPmsBrandService.page(new Page<PmsBrand>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            log.error("根据条件查询所有品牌表列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "cms", REMARK = "添加商品评论")
    @ApiOperation(value = "添加商品评论")
    @PostMapping(value = "/user.orderevaluate")
    public Object addGoodsConsult( @RequestParam(value = "orderId", defaultValue = "1") Long orderId,
                                   @RequestParam(value = "items", defaultValue = "10") String items) throws Exception {
        CommonResult commonResult;
        UmsMember member = UserUtils.getCurrentMember();

        List<ProductConsultParam> itemss = JsonUtils.json2list(items,ProductConsultParam.class);
        for (ProductConsultParam param : itemss){
            PmsProductConsult productConsult = new PmsProductConsult();
            if (member!=null){
                productConsult.setPic(member.getIcon());
                productConsult.setMemberName(member.getNickname());
                productConsult.setMemberId(member.getId());
            }else {
                return new CommonResult().failed("请先登录");
            }
            productConsult.setGoodsId(param.getGoodsId());
            productConsult.setConsultContent(param.getTextarea());
            productConsult.setStars(param.getScore());
            productConsult.setEmail(Arrays.toString(param.getImages()));
            productConsult.setConsultAddtime(new Date());
            pmsProductConsultService.save(productConsult);
        }
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setId(orderId);
        omsOrder.setIsComment(2);
        omsOrder.setStatus(OrderStatus.TRADE_SUCCESS.getValue());
        if ( omsOrderMapper.updateById(omsOrder)>0) {
            commonResult = new CommonResult().success(1);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @IgnoreAuth
    @ApiOperation("获取某个商品的评价")
    @RequestMapping(value = "/goods.getgoodscomment", method = RequestMethod.POST)
    @ResponseBody
    public Object list(@RequestParam(value = "goodsId", required = false, defaultValue = "0") Long goodsId,
                       @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        PmsProductConsult productConsult = new PmsProductConsult();
        productConsult.setGoodsId(goodsId);
        List<PmsProductConsult> list =  pmsProductConsultService.list(new QueryWrapper<>(productConsult));

        int goods = 0;
        int general = 0;
        int bad = 0;
        ConsultTypeCount count = new ConsultTypeCount();
        for (PmsProductConsult consult : list) {
            if (consult.getStars() != null) {
                if (consult.getStars()==1){
                    bad++;
                }
                if (consult.getStars()==2){
                    general++;
                }
                if (consult.getStars()==3){
                    goods++;
                }
            }
        }
        count.setAll(list.size());
        count.setBad(bad);
        count.setGeneral(general);
        count.setGoods(goods);
        if (count.getAll()>0){
            count.setPersent(new BigDecimal(goods).divide(new BigDecimal(count.getAll())).multiply(new BigDecimal(100)));
        }else {
            count.setPersent(new BigDecimal(200));
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("list", list);
        objectMap.put("count", count);
        return new CommonResult().success(objectMap);
    }
    @SysLog(MODULE = "pms", REMARK = "查询团购商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询团购商品列表")
    @PostMapping(value = "/groupHotGoods/list")
    public Object groupHotGoods(PmsProduct product,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        List<SmsGroup> groupList =  groupService.list(new QueryWrapper<>());
        List<SmsGroup> result = new ArrayList<>();
        for (SmsGroup group :groupList){
            if (ValidatorUtils.empty(group.getHours())){
                continue;
            }
            Long nowT = System.currentTimeMillis();
            Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");
            if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {
                PmsProduct g =pmsProductService.getById(group.getGoodsId());
                if(g!=null){
                    group.setGoods(GoodsUtils.sampleGoods(g));
                    result.add(group);
                }

            }
        }
        return new CommonResult().success(result);
    }

    @SysLog(MODULE = "pms", REMARK = "查询团购商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询团购商品列表")
    @PostMapping(value = "/groupGoods/list")
    public Object groupGoodsList(PmsProduct product,
                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
       List<SmsGroup> groupList =  groupService.list(new QueryWrapper<>());
       if (groupList!=null && groupList.size()>0){
           List<Long> ids = groupList.stream()
                   .map(SmsGroup::getGoodsId)
                   .collect(Collectors.toList());
           product.setPublishStatus(1);
           product.setVerifyStatus(1);
           product.setMemberId(null);
           IPage<PmsProduct> list  = pmsProductService.page(new Page<PmsProduct>(pageNum, pageSize), new QueryWrapper<>(product).in("id",ids));
           return new CommonResult().success(list);
       }
        return null;
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品详情信息")
    @IgnoreAuth
    @PostMapping(value = "/goodsGroup/detail")
    @ApiOperation(value = "查询商品详情信息")
    public Object groupGoodsDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        //记录浏览量到redis,然后定时更新到数据库
        String key=Rediskey.GOODS_VIEWCOUNT_CODE+id;
        //找到redis中该篇文章的点赞数，如果不存在则向redis中添加一条
        Map<Object,Object> viewCountItem=redisUtil.hGetAll(Rediskey.GOODS_VIEWCOUNT_KEY);
        Integer viewCount=0;
        if(!viewCountItem.isEmpty()){
            if(viewCountItem.containsKey(key)){
                viewCount=Integer.parseInt(viewCountItem.get(key).toString())+1;
                redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,viewCount+"");
            }else {
                viewCount=1;
                redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,1+"");
            }
        }else{
            viewCount=1;
            redisUtil.hPut(Rediskey.GOODS_VIEWCOUNT_KEY,key,1+"");
        }
        GoodsDetailResult goods = null;
        try {
              goods =pmsProductService.getGoodsRedisById(id);
                      //JsonUtils.jsonToPojo(redisService.get(String.format(Rediskey.GOODSDETAIL, id+"")), GoodsDetailResult.class);
            if (ValidatorUtils.empty(goods)){
                log.info("redis缓存失效："+id);
                goods = pmsProductService.getGoodsRedisById(id);
            }
        } catch (Exception e) {
            log.info("redis缓存失效："+id);
            goods = pmsProductService.getGoodsRedisById(id);
            e.printStackTrace();
        }
        SmsGroup group = groupMapper.getByGoodsId(id);
        Map<String, Object> map = new HashMap<>();
        UmsMember umsMember = UserUtils.getCurrentMember();
        if (umsMember != null && umsMember.getId() != null) {
            PmsProduct p = goods.getGoods();
            p.setHit(viewCount);
            PmsFavorite query = new PmsFavorite();
            query.setObjId(p.getId());
            query.setMemberId(umsMember.getId());
            query.setType(1);
            PmsFavorite findCollection = favoriteService.getOne(new QueryWrapper<>(query));
            if(findCollection!=null){
                map.put("favorite", true);
            }else{
                map.put("favorite", false);
            }
        }
        if (group!=null){
            map.put("memberGroupList",groupMemberMapper.selectList(new QueryWrapper<SmsGroupMember>().eq("group_id",group.getId())));
            map.put("group", group);
        }


        map.put("goods", goods);
        return new CommonResult().success(map);
    }


    @SysLog(MODULE = "pms", REMARK = "查询团购商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询礼物商品列表")
    @PostMapping(value = "/gift/list")
    public Object giftList(PmsGifts product,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

            IPage<PmsGifts> list  = giftsService.page(new Page<PmsGifts>(pageNum, pageSize), new QueryWrapper<>(product));
            return new CommonResult().success(list);

    }

    @SysLog(MODULE = "pms", REMARK = "查询商品详情信息")
    @IgnoreAuth
    @PostMapping(value = "/gift/detail")
    @ApiOperation(value = "查询礼物商品详情信息")
    public Object giftDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        PmsGifts  goods = giftsService.getById(id);
        Map<String, Object> map = new HashMap<>();
        UmsMember umsMember = UserUtils.getCurrentMember();
        if (umsMember != null && umsMember.getId() != null) {
            PmsFavorite query = new PmsFavorite();
            query.setObjId(goods.getId());
            query.setMemberId(umsMember.getId());
            query.setType(4);
            PmsFavorite findCollection = favoriteService.getOne(new QueryWrapper<>(query));
            if(findCollection!=null){
                map.put("favorite", true);
            }else{
                map.put("favorite", false);
            }
        }
        map.put("goods", goods);
        return new CommonResult().success(map);
    }
    @SysLog(MODULE = "pms", REMARK = "查询商品类型下的商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询积分商品类型")
    @PostMapping(value = "/typeGiftList")
    public Object typeGiftList(PmsGiftsCategory productCategory) {
        List<PmsGiftsCategory> categories = giftsCategoryService.list(new QueryWrapper<>(productCategory));
        return new CommonResult().success(categories);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品分类列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品分类列表")
    @PostMapping(value = "/categories.getallcat")
    public Object categoryAndGoodsList(PmsProductAttributeCategory productCategory) {
        List<PmsProductAttributeCategory> productAttributeCategoryList = productAttributeCategoryService.list(new QueryWrapper<>());
        for (PmsProductAttributeCategory gt : productAttributeCategoryList) {
            PmsProduct productQueryParam = new PmsProduct();
            productQueryParam.setProductAttributeCategoryId(gt.getId());
            productQueryParam.setPublishStatus(1);
            productQueryParam.setVerifyStatus(1);
            gt.setGoodsList(GoodsUtils.sampleGoodsList(pmsProductService.list(new QueryWrapper<>(productQueryParam))));
        }
        return new CommonResult().success(productAttributeCategoryList);
    }

    @SysLog(MODULE = "pms", REMARK = "查询首页推荐品牌")
    @IgnoreAuth
    @ApiOperation(value = "查询首页推荐品牌")
    @PostMapping(value = "/recommendBrand/list")
    public Object getRecommendBrandList(
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(pmsProductService.getRecommendBrandList(1,1));
    }

    @SysLog(MODULE = "pms", REMARK = "查询首页新品")
    @IgnoreAuth
    @ApiOperation(value = "查询首页新品")
    @PostMapping(value = "/newProductList/list")
    public Object getNewProductList(
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(pmsProductService.getHotProductList(1,1));
    }

    @SysLog(MODULE = "pms", REMARK = "查询首页热销商品")
    @IgnoreAuth
    @ApiOperation(value = "查询首页热销商品")
    @PostMapping(value = "/hotProductList/list")
    public Object getHotProductList(
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(pmsProductService.getHotProductList(1,1));
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品优惠")
    @PostMapping(value = "/getPromotionProductList")
    public List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids){
        return productMapper.getPromotionProductList(ids);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品类型下的商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品类型下的商品列表")
    @PostMapping(value = "/typeGoodsList")
    public Object typeGoodsList(PmsProductCategory productCategory) {
        PmsProduct productQueryParam = new PmsProduct();

        productQueryParam.setPublishStatus(1);
        productQueryParam.setVerifyStatus(1);
        List<PmsProduct> list = pmsProductService.list(new QueryWrapper<>(productQueryParam));

        List<ProductTypeVo> relList = new ArrayList<>();
        for (PmsProduct l : list){
            ProductTypeVo vo = new ProductTypeVo();
            vo.setGoodsId(l.getId());
            vo.setId(l.getId());
            vo.setPic(l.getPic());
            vo.setName(l.getName());
            vo.setPrice(l.getPrice());
            vo.setPid(l.getProductCategoryId());
            relList.add(vo);
        }
        List<PmsProductCategory> categories = categoryMapper.selectList(new QueryWrapper<>());
        for (PmsProductCategory v : categories){
            if (v.getParentId()==0){
                ProductTypeVo vo = new ProductTypeVo();
                vo.setName(v.getName());
                vo.setId(v.getId());
                relList.add(vo);
            }else{
                ProductTypeVo vo = new ProductTypeVo();
                vo.setName(v.getName());
                vo.setId(v.getId());
                vo.setPid(v.getParentId());
                relList.add(vo);
            }
        }

        return new CommonResult().success(relList);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品类型下的商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询商品类型下的商品列表")
    @PostMapping(value = "/typeList")
    public Object typeList(PmsProductCategory productCategory) {
        List<ProductTypeVo> relList = new ArrayList<>();
        List<PmsProductCategory> categories = categoryMapper.selectList(new QueryWrapper<>());
        for (PmsProductCategory v : categories){
            if (v.getParentId()==0){
                ProductTypeVo vo = new ProductTypeVo();
                vo.setName(v.getName());
                vo.setId(v.getId());
                relList.add(vo);
            }else{
                ProductTypeVo vo = new ProductTypeVo();
                vo.setName(v.getName());
                vo.setId(v.getId());
                vo.setPid(v.getParentId());
                relList.add(vo);
            }
        }

        return new CommonResult().success(relList);
    }
    @IgnoreAuth
    @ApiOperation("添加商品浏览记录")
    @SysLog(MODULE = "pms", REMARK = "添加商品浏览记录")
    @PostMapping(value = "/user.addgoodsbrowsing")
    public Object addView(@RequestParam  Long goodsId) {

        String key = String.format(Rediskey.GOODSHISTORY, UserUtils.getCurrentMember().getId());

        //为了保证浏览商品的 唯一性,每次添加前,将list 中该 商品ID去掉,在加入,以保证其浏览的最新的商品在最前面

        redisUtil.lRemove(key, 1, goodsId.toString());
        //将value push 到该key下的list中
        redisUtil.lLeftPush(key,goodsId.toString());
        //使用ltrim将60个数据之后的数据剪切掉
        redisUtil.lTrim(key,0,59);
        //设置缓存时间为一个月
        redisUtil.expire(key,60*60*24*30, TimeUnit.SECONDS);
        return new CommonResult().success();
    }
    @SysLog(MODULE = "pms", REMARK = "查询用户浏览记录列表")
    @IgnoreAuth
    @ApiOperation(value = "查询用户浏览记录列表")
    @PostMapping(value = "/user.goodsbrowsing")
    public Object viewList(
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        String key = String.format(Rediskey.GOODSHISTORY, UserUtils.getCurrentMember().getId());

        //获取用户的浏览的商品的总页数;
        long pageCount = redisUtil.lLen(key);
        //拼装返回
        Map<String,Object> map = new HashMap<>();
        //根据用户的ID分頁获取该用户最近浏览的50个商品信息
        List<String> result = redisUtil.lRange(key,(pageNum-1)*pageSize,pageNum*pageSize-1);
        if (result!=null && result.size()>0){
            List<PmsProduct> list = (List<PmsProduct>) pmsProductService.listByIds(result);

            map.put("result",list);
            map.put("pageCount",(pageCount%pageSize == 0 ? pageCount/pageSize : pageCount/pageSize+1));
        }

        return new CommonResult().success(map);
    }

    @Autowired
    private IPmsFavoriteService memberCollectionService;
    @Autowired
    private ICmsFavoriteService cmsFavoriteService;

    @ApiOperation("添加和取消收藏 type 1 商品 2 文章")
    @PostMapping("user.goodscollection")
    public Object favoriteSave(PmsFavorite productCollection) {
        int count = memberCollectionService.addProduct(productCollection);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("删除收藏中的某个商品")
    @PostMapping(value = "/delete")
    public Object delete(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(ids.split(",").length);
        for (String s : ids.split(",")) {
            resultList.add(Long.valueOf(s));
        }
        if (memberCollectionService.removeByIds(resultList)) {
            return new CommonResult().success();
        }
        return new CommonResult().failed();
    }

    @ApiOperation("显示收藏列表")
    @PostMapping(value = "/user.goodscollectionlist")
    public Object listCollectByType( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listProduct(UserUtils.getCurrentMember().getId(),productCollection.getType());
        return new CommonResult().success(memberProductCollectionList);
    }
    @ApiOperation("显示收藏列表")
    @PostMapping(value = "/listCollect")
    public Object listCollect( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listCollect(UserUtils.getCurrentMember().getId());
        return new CommonResult().success(memberProductCollectionList);
    }


    @ApiOperation("添加和取消点赞 type 1 商品 2 文章")
    @PostMapping("likeSave")
    public Object likeSave(CmsFavorite productCollection) {
        int count = cmsFavoriteService.addProduct(productCollection);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("删除点赞中的某个商品")
    @PostMapping(value = "/deleteLike")
    public Object deleteLike(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(ids.split(",").length);
        for (String s : ids.split(",")) {
            resultList.add(Long.valueOf(s));
        }

        if (cmsFavoriteService.removeByIds(resultList)) {
            return new CommonResult().success();
        }
        return new CommonResult().failed();
    }

    @ApiOperation("显示点赞列表")
    @PostMapping(value = "/listLikeByType")
    public Object listLikeByType( CmsFavorite productCollection) {
        List<CmsFavorite> memberProductCollectionList = cmsFavoriteService.listProduct(UserUtils.getCurrentMember().getId(),productCollection.getType());
        return new CommonResult().success(memberProductCollectionList);
    }
    @ApiOperation("显示点赞列表")
    @PostMapping(value = "/listLike")
    public Object listLike( CmsFavorite productCollection) {
        List<CmsFavorite> memberProductCollectionList = cmsFavoriteService.listCollect(UserUtils.getCurrentMember().getId());
        return new CommonResult().success(memberProductCollectionList);
    }


    @ApiOperation("生成海报")
    @PostMapping(value = "/user.getposter")
    public Object getposter( @RequestParam Long id) {
        PmsProduct product = pmsProductService.getById(id);
        return new CommonResult().success(product.getPic());
    }

    @IgnoreAuth
    @ApiOperation("显示默认收货地址")
    @RequestMapping(value = "/store.getdefaultstore", method = RequestMethod.POST)
    @ResponseBody
    public Object getItemDefautl( @RequestParam Long id) {
        SysStore address = storeMapper.selectById(id);
        return new CommonResult().success(address);
    }

    @SysLog(MODULE = "pms", REMARK = "查询商品列表")
    @IgnoreAuth
    @ApiOperation(value = "查询首页推荐商品")
    @PostMapping(value = "/initGoodsRedis")
    public Object initGoodsRedis() {

        return pmsProductService.initGoodsRedis();

    }



}