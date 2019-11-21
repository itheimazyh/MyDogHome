package com.mei.zhuang.service.order.impl;

import com.arvato.service.order.api.feigin.GoodsFegin;
import com.arvato.service.order.api.orm.dao.EsActivatySmallBeautyBoxGiftBoxMapper;
import com.arvato.service.order.api.orm.dao.EsActivatySmallBeautyBoxMapper;
import com.arvato.service.order.api.service.EsActivatySmallBeautyBoxGiftBoxService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mei.zhuang.entity.goods.EsShopGoods;
import com.mei.zhuang.entity.order.EsActivatySmallBeautyBox;
import com.mei.zhuang.entity.order.EsActivatySmallBeautyBoxGiftBox;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EsActivatySmallBeautyBoxGiftBoxServiceImpl extends ServiceImpl<EsActivatySmallBeautyBoxGiftBoxMapper, EsActivatySmallBeautyBoxGiftBox> implements EsActivatySmallBeautyBoxGiftBoxService {

    @Resource
    private EsActivatySmallBeautyBoxGiftBoxMapper esActivatySmallBeautyBoxGiftBoxMapper;
    @Resource
    private EsActivatySmallBeautyBoxMapper esActivatySmallBeautyBoxMapper;
    @Resource
    private GoodsFegin fegin;

    @Override
    public List<EsActivatySmallBeautyBoxGiftBox> selectSmall(List<String> list,Long id) {
        return esActivatySmallBeautyBoxGiftBoxMapper.selectGiftBox(list,id);
    }

    @Override
    public List<EsActivatySmallBeautyBoxGiftBox> select(EsActivatySmallBeautyBoxGiftBox entity) {
        //同步商品库存
        List<EsActivatySmallBeautyBoxGiftBox> list = esActivatySmallBeautyBoxGiftBoxMapper.select(entity);
        for (EsActivatySmallBeautyBoxGiftBox gox:list) {
            EsShopGoods goods = (EsShopGoods)fegin.goodsDetail(gox.getId());
            if(goods!= null){
                gox.setVituralStock(goods.getVituralStock());
                esActivatySmallBeautyBoxGiftBoxMapper.updateById(gox);
            }
            EsActivatySmallBeautyBox box= esActivatySmallBeautyBoxMapper.selectById(gox.getActivatyId());
            if(box != null){
                gox.setActivatyName(box.getActivatyName());
            }
        }
        return list;
    }
}
