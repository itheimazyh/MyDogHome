package com.zscat.mallplus.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zscat.mallplus.pms.entity.PmsProductCategory;
import com.zscat.mallplus.pms.vo.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * <p>
 * 产品分类 Mapper 接口
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
public interface PmsProductCategoryMapper extends BaseMapper<PmsProductCategory> {

    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
