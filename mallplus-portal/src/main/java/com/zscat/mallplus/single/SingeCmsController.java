package com.zscat.mallplus.single;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.*;
import com.zscat.mallplus.cms.mapper.CmsSubjectMapper;
import com.zscat.mallplus.cms.service.*;
import com.zscat.mallplus.enums.ConstansValue;
import com.zscat.mallplus.pms.entity.CmsSubjectProductRelation;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.mapper.CmsSubjectProductRelationMapper;
import com.zscat.mallplus.pms.service.IPmsProductAttributeCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.sms.service.ISmsGroupService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberLevel;
import com.zscat.mallplus.ums.entity.UmsRewardLog;
import com.zscat.mallplus.ums.mapper.UmsMemberMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.ums.service.IUmsMemberLevelService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.IUmsRewardLogService;
import com.zscat.mallplus.ums.service.impl.RedisUtil;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import com.zscat.mallplus.vo.Rediskey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "CmsController", description = "内容关系管理")
@RequestMapping("/api/single/cms")
public class SingeCmsController extends ApiBaseAction {
    @Resource
    private CmsSubjectMapper subjectMapper;
    @Resource
    private ISmsGroupService groupService;
    @Resource
    private IUmsMemberLevelService memberLevelService;
    @Resource
    private IPmsProductService pmsProductService;
    @Resource
    private IPmsProductAttributeCategoryService productAttributeCategoryService;
    @Resource
    private IPmsProductCategoryService productCategoryService;
    @Resource
    private ICmsTopicService topicService;

    @Resource
    private ICmsSubjectCategoryService subjectCategoryService;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private ICmsSubjectCommentService commentService;
    @Resource
    private ICmsTopicCommentService topicCommentService;
    @Resource
    private UmsMemberMapper memberMapper;
    @Resource
    private UmsRewardLogMapper rewardLogMapper;
    @Resource
    private IUmsRewardLogService rewardLogService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IUmsMemberService memberService;

    @Resource
    private CmsSubjectProductRelationMapper subjectProductRelationMapper;



    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询打赏列表")
    @ApiOperation(value = "查询打赏列表")
    @GetMapping(value = "/reward/list")
    public Object rewardList(UmsRewardLog subject,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(rewardLogService.page(new Page<UmsRewardLog>(pageNum, pageSize), new QueryWrapper<>(subject).orderByDesc("create_time")));
    }

    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询文章列表")
    @ApiOperation(value = "查询文章列表")
    @GetMapping(value = "/subject/list")
    public Object subjectList(CmsSubject subject,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        if (ValidatorUtils.empty(subject.getType())) {
            return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject).select(ConstansValue.sampleSubjectList).orderByDesc("create_time")));
        }
        if (subject.getType() == 1) {
            return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject).select(ConstansValue.sampleSubjectList).orderByDesc("read_count")));
        } else if (subject.getType() == 2) {
            return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject).select(ConstansValue.sampleSubjectList).orderByDesc("collect_count")));
        } else if (subject.getType() == 3) {
            return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject).select(ConstansValue.sampleSubjectList).orderByDesc("comment_count")));
        }
        return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject).select(ConstansValue.sampleSubjectList).orderByDesc("create_time")));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章分类列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章分类列表")
    @GetMapping(value = "/subjectCategory/list")
    public Object cateList(CmsSubjectCategory subjectCategory,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(subjectCategoryService.page(new Page<CmsSubjectCategory>(pageNum, pageSize), new QueryWrapper<>(subjectCategory)));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章评论列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章评论列表")
    @GetMapping(value = "/subjectComment/list")
    public Object subjectList(CmsSubjectComment subjectComment,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(commentService.page(new Page<CmsSubjectComment>(pageNum, pageSize), new QueryWrapper<>(subjectComment).orderByDesc("create_time")));
    }

    @SysLog(MODULE = "pms", REMARK = "查询首页推荐文章")
    @IgnoreAuth
    @ApiOperation(value = "查询首页推荐文章")
    @GetMapping(value = "/recommendSubjectList/list")
    public Object getRecommendSubjectList(
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(subjectService.getRecommendSubjectList(1, 1));
    }

    @SysLog(MODULE = "cms", REMARK = "查询专题列表")
    @IgnoreAuth
    @ApiOperation(value = "查询公益列表")
    @GetMapping(value = "/topic/list")
    public Object subjectList(CmsTopic topic,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(topicService.page(new Page<CmsTopic>(pageNum, pageSize), new QueryWrapper<>(topic).orderByDesc("create_time")));
    }

    @SysLog(MODULE = "cms", REMARK = "查询专题列表")
    @IgnoreAuth
    @ApiOperation(value = "查询公益评论列表")
    @GetMapping(value = "/topicComment/list")
    public Object subjectList(CmsTopicComment topic,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(topicCommentService.page(new Page<CmsTopicComment>(pageNum, pageSize), new QueryWrapper<>(topic).orderByDesc("create_time")));
    }

    @SysLog(MODULE = "pms", REMARK = "查询专题详情信息")
    @IgnoreAuth
    @GetMapping(value = "/topic/detail")
    @ApiOperation(value = "查询公益详情信息")
    public Object topicDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CmsTopic productResult = topicService.getById(id);
        productResult.setReadCount(productResult.getReadCount() + 1);
        topicService.updateById(productResult);
        return new CommonResult().success(productResult);
    }

    @SysLog(MODULE = "pms", REMARK = "查询文章详情信息")
    @IgnoreAuth
    @GetMapping(value = "/subject/detail")
    @ApiOperation(value = "查询文章详情信息")
    public Object subjectDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CmsSubject productResult = subjectService.getById(id);
        List<CmsSubjectProductRelation> list = subjectProductRelationMapper.selectList(new QueryWrapper<CmsSubjectProductRelation>().eq("subject_id", id));

        if (list != null && list.size() > 0) {
            List<Long> ids = list.stream()
                    .map(CmsSubjectProductRelation::getProductId)
                    .collect(Collectors.toList());
            List<PmsProduct> products = (List<PmsProduct>) pmsProductService.list(new QueryWrapper<PmsProduct>().in("id", ids).select(ConstansValue.sampleGoodsList));
            productResult.setProducts(products);
        }

        //记录浏览量到redis,然后定时更新到数据库
        String key = Rediskey.ARTICLE_VIEWCOUNT_CODE + id;
        //找到redis中该篇文章的点赞数，如果不存在则向redis中添加一条
        Map<Object, Object> viewCountItem = redisUtil.hGetAll(Rediskey.ARTICLE_VIEWCOUNT_KEY);
        Integer viewCount = 0;
        if (!viewCountItem.isEmpty()) {
            if (viewCountItem.containsKey(key)) {
                viewCount = Integer.parseInt(viewCountItem.get(key).toString()) + 1;
                redisUtil.hPut(Rediskey.ARTICLE_VIEWCOUNT_KEY, key, viewCount + "");
            } else {
                viewCount = 1;
                redisUtil.hPut(Rediskey.ARTICLE_VIEWCOUNT_KEY, key, 1 + "");
            }
        } else {
            viewCount = 1;
            redisUtil.hPut(Rediskey.ARTICLE_VIEWCOUNT_KEY, key, 1 + "");
        }
        productResult.setReadCount(viewCount);
        return new CommonResult().success(productResult);
    }

    @SysLog(MODULE = "cms", REMARK = "创建文章")
    @ApiOperation(value = "创建文章")
    @PostMapping(value = "/createSubject")
    public Object createSubject(CmsSubject subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();
        if (member != null) {
            subject.setMemberId(member.getId());
            subject.setMemberName(member.getNickname());
        } else {
            return new CommonResult().failed("请先登录");
        }
        if (member.getMemberLevelId() > 0) {
            UmsMemberLevel memberLevel = memberLevelService.getById(member.getMemberLevelId());

            int subjectCounts = subjectService.countByToday(member.getId());
            if (ValidatorUtils.empty(subjectCounts)) {
                subjectCounts = 0;
            }
            if (subjectCounts > memberLevel.getArticlecount()) {
                commonResult = new CommonResult().failed("你今天已经有发" + memberLevel.getArticlecount() + "篇文章");
                return commonResult;
            }
        }
        if (subject.getQsType() == 1) {
            subject.setSchoolName(member.getSchoolName());
            subject.setSchoolId(member.getSchoolId());
        } else {
            subject.setAreaName(member.getAreaName());
            subject.setAreaId(member.getAreaId());
        }

        subject.setReadCount(0);
        subject.setForwardCount(0);
        subject.setCollectCount(0);
        subject.setCreateTime(new Date());

        boolean count = subjectService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "创建公益")
    @ApiOperation(value = "发布公益")
    @PostMapping(value = "/createTopic")
    public Object createTopic(CmsTopic subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();
        if (member != null) {
            subject.setMemberId(member.getId());
            subject.setMemberName(member.getNickname());
        } else {
            return new CommonResult().failed("请先登录");
        }

        if (subject.getQsType() == 1) {
            subject.setSchoolName(member.getSchoolName());
            subject.setSchoolId(member.getSchoolId());
        } else {
            subject.setAreaName(member.getAreaName());
            subject.setAreaId(member.getAreaId());
        }

        subject.setReadCount(0);
        subject.setAttendCount(0);
        subject.setAttentionCount(0);
        subject.setCreateTime(new Date());
        boolean count = topicService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "参加公益")
    @ApiOperation(value = "参加公益 公益id")
    @PostMapping(value = "/attendTopic")
    public Object attendTopic(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();

        CmsTopic subject = topicService.getById(id);
        Date now = new Date();
        if (now.getTime() > subject.getEndTime().getTime() || now.getTime() < subject.getStartTime().getTime()) {
            return new CommonResult().failed("未开始或者已过期");
        }
        if (member != null) {
            if (ValidatorUtils.notEmpty(subject.getAtids())) {
                String[] values = subject.getAtids().split(",");
                List<String> list = Arrays.asList(values);
                if (list.contains(member.getId() + "")) {
                    return new CommonResult().failed("你已参加该活动");
                } else {
                    list.add(member.getId() + "");
                    subject.setAtids(Arrays.toString(list.toArray()));
                }
            }

        } else {
            return new CommonResult().failed("请先登录");
        }
        subject.setReadCount(subject.getReadCount() + 1);
        subject.setAttendCount(subject.getAttendCount() + 1);

        boolean count = topicService.updateById(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "参加公益")
    @ApiOperation(value = "取消参加公益 公益id")
    @PostMapping(value = "/canceTopic")
    public Object canceTopic(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();
        CmsTopic subject = topicService.getById(id);

        if (member != null) {
            if (ValidatorUtils.notEmpty(subject.getAtids())) {
                String[] values = subject.getAtids().split(",");
                List<String> list = Arrays.asList(values);
                if (list.contains(member.getId() + "")) {
                    list.remove(member.getId() + "");
                    subject.setAtids(Arrays.toString(list.toArray()));
                }
            }

        } else {
            return new CommonResult().failed("请先登录");
        }
        subject.setReadCount(subject.getReadCount() - 1);
        subject.setAttendCount(subject.getAttendCount() - 1);

        boolean count = topicService.updateById(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "参加公益")
    @ApiOperation(value = "关注公益 公益id，点一次关注按钮，记录一下关注数量，不去重 不需要登录")
    @PostMapping(value = "/favoriteTopic")
    public Object favoriteTopic(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CommonResult commonResult;
        CmsTopic subject = topicService.getById(id);
        subject.setAttentionCount(subject.getAttentionCount() + 1);
        boolean count = topicService.updateById(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "创建文章")
    @ApiOperation(value = "添加文章评论")
    @PostMapping(value = "/addSubjectCom")
    public Object addSubjectCom(CmsSubjectComment subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();
        if (member != null) {
            subject.setMemberIcon(member.getIcon());
            subject.setMemberNickName(member.getNickname());
        } else {
            return new CommonResult().failed("请先登录");
        }
        subject.setCreateTime(new Date());

        boolean count = commentService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @SysLog(MODULE = "cms", REMARK = "创建文章")
    @ApiOperation(value = "添加公益评论")
    @PostMapping(value = "/addTopicCom")
    public Object addTopicCom(CmsTopicComment subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = memberService.getNewCurrentMember();
        if (member != null) {
            subject.setMemberIcon(member.getIcon());
            subject.setMemberNickName(member.getNickname());
        } else {
            return new CommonResult().failed("请先登录");
        }
        subject.setCreateTime(new Date());

        boolean count = topicCommentService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }

    @ApiOperation(value = "打赏文章")
    @PostMapping(value = "/reward")
    @SysLog(MODULE = "ums", REMARK = "打赏文章")
    public Object reward(@RequestParam(value = "articlelId", required = true) Long articlelId,
                         @RequestParam(value = "coin", required = true) int coin) {
        return subjectService.reward(articlelId, coin);
    }

    @GetMapping("/listTimeline")
    public Object listTimeline() {
        return new CommonResult().success(subjectService.listTimeLine());
    }

    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询文章列表")
    @ApiOperation(value = "查询文章列表")
    @GetMapping(value = "/subjectListByTime/list")
    public Object subjectListByTime(CmsSubject subject,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                    @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(subjectMapper.loadArticleByArchive(subject.getCreateTimeVar()));
    }

    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询文章列表")
    @ApiOperation(value = "查询文章列表")
    @GetMapping(value = "/getTime/list")
    public Object getTime(CmsSubject subject,
                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                          @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(subjectMapper.articleArchiveList());
    }
}
