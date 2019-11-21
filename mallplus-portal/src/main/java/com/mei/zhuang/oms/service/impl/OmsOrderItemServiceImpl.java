package com.mei.zhuang.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.zhuang.oms.service.IOmsOrderItemService;
import com.zscat.mallplus.oms.entity.OmsOrderItem;
import com.zscat.mallplus.oms.mapper.OmsOrderItemMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单中所包含的商品 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
public class OmsOrderItemServiceImpl extends ServiceImpl<OmsOrderItemMapper, OmsOrderItem> implements IOmsOrderItemService {

}
