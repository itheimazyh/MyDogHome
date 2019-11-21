package com.mei.zhuang.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.zhuang.ums.service.IUmsMemberProductCategoryRelationService;
import com.zscat.mallplus.ums.entity.UmsMemberProductCategoryRelation;
import com.zscat.mallplus.ums.mapper.UmsMemberProductCategoryRelationMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员与产品分类关系表（用户喜欢的分类） 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class UmsMemberProductCategoryRelationServiceImpl extends ServiceImpl<UmsMemberProductCategoryRelationMapper, UmsMemberProductCategoryRelation> implements IUmsMemberProductCategoryRelationService {

}
