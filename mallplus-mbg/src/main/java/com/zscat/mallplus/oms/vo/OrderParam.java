package com.zscat.mallplus.oms.vo;

import lombok.Data;

/**
 * 生成订单时传入的参数
 * https://github.com/shenzhuan/mallplus on 2018/8/30.
 */
@Data
public class OrderParam {
    long bargainId;
    long bargainRecordId ;
    String page;
    String formId;
    String platform = "2";
    String basicGiftsVar;
    private Integer total;
    //收货地址id
    private Long addressId;
    //优惠券id
    private Long couponId;
    private Long memberCouponId;
    private Long memberId;
    //使用的积分数
    private Integer useIntegration;
    //支付方式
    private Integer payType = 1;
    private Integer offline;// 0 送货 1 自取
    private String lading_mobile; //自取人电话
    private String lading_name; //自取人姓名
    private Integer shopId; //门店编号

    private String content;
    private String cartId;
    private String cartIds;
    private String type; // 1 商品详情 2 勾选购物车 3全部购物车的商品
    private Integer source = 1; ////订单来源：0->PC订单；5->app订单 2 h5 3微信小程序 4 支付宝小程序
    private Integer orderType = 1; // 1 普通订单 2 秒杀订单 3 团购订单 4 拼团订单 5 积分订单
    private Long skuId;
    private Long goodsId;
    private Long groupId;
    private Long groupActivityId;
    // 1 发起拼团 2 参与拼团
    private Integer groupType;
    // 参与拼团 团购记录id
    private Long mgId = 0l;
    private Long skillId = 0l; // 秒杀ID
    private Long inviteMemberId = 0l; //分佣商品 链接带过来的会员编号

}
