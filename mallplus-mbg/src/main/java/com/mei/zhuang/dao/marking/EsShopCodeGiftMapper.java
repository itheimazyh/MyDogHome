package com.mei.zhuang.dao.marking;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.zhuang.entity.marking.EsShopCodeGift;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author meizhuang team
 * @since 2019-05-02
 */
public interface EsShopCodeGiftMapper extends BaseMapper<EsShopCodeGift> {


    Integer count(EsShopCodeGift entity);

}
