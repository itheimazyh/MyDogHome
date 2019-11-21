package com.mei.zhuang.dao.marking;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.zhuang.entity.marking.EsShopFriendGiftCard;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author arvato team
 * @since 2019-08-07
 */
public interface EsShopFriendGiftCardMapper extends BaseMapper<EsShopFriendGiftCard> {

    Integer updatecard(long giftId);



}
