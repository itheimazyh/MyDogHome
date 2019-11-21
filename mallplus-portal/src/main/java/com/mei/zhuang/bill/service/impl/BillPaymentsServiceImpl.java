package com.mei.zhuang.bill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.zhuang.bill.service.IBillPaymentsService;
import com.zscat.mallplus.bill.entity.BillPayments;
import com.zscat.mallplus.bill.mapper.BillPaymentsMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付单表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-09-16
 */
@Service
public class BillPaymentsServiceImpl extends ServiceImpl<BillPaymentsMapper, BillPayments> implements IBillPaymentsService {

}
