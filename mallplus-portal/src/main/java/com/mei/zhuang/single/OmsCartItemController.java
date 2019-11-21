package com.mei.zhuang.single;


import com.mei.zhuang.oms.service.IOmsCartItemService;
import com.mei.zhuang.oms.service.IOmsOrderService;
import com.mei.zhuang.pms.service.IPmsSkuStockService;
import com.mei.zhuang.sms.service.ISmsBasicMarkingService;
import com.mei.zhuang.ums.service.IUmsMemberService;
import com.mei.zhuang.vo.CartParam;
import com.zscat.mallplus.exception.ApiMallPlusException;
import com.zscat.mallplus.oms.entity.OmsCartItem;
import com.zscat.mallplus.oms.vo.CartMarkingVo;
import com.zscat.mallplus.oms.vo.CartProduct;
import com.zscat.mallplus.sms.entity.SmsBasicMarking;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车管理Controller
 * https://github.com/shenzhuan/mallplus on 2018/8/2.
 */
@RestController
@Api(tags = "OmsCartItemController", description = "购物车管理")
@RequestMapping("/api/cart")
public class OmsCartItemController {
    @Autowired
    private IOmsCartItemService cartItemService;
    @Autowired
    private IUmsMemberService memberService;

    @Autowired
    private IPmsSkuStockService pmsSkuStockService;

    @Resource
    private IOmsOrderService orderService;
    @Resource
    private ISmsBasicMarkingService smsBasicMarkingService;
    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/addCart")
    @ResponseBody
    public Object addCart(CartParam cartParam) {
        try {
            return orderService.addCart(cartParam);
        } catch (ApiMallPlusException e) {
            return new CommonResult().failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    @ApiOperation("获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list() {
        UmsMember umsMember = memberService.getNewCurrentMember();
        Map<String,Object> map = new HashMap<>();
        if (umsMember != null && umsMember.getId() != null) {
            List<OmsCartItem> cartItemList = cartItemService.list(umsMember.getId(), null);
            map.put("cartItemList",cartItemList);
            CartMarkingVo vo = new CartMarkingVo();
            vo.setCartList(cartItemList);
            SmsBasicMarking marking =smsBasicMarkingService.matchOrderBasicMarking(vo) ;
            if (marking!=null){
                map.put("promoteAmount",marking.getMinAmount());
            }else {
                map.put("promoteAmount",0);
            }

            return new CommonResult().success(map);
        }
        return new CommonResult().success(map);
    }



    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    @ResponseBody
    public Object updateQuantity(@RequestParam Long id,
                                 @RequestParam Integer quantity) {
        int count = cartItemService.updateQuantity(id, memberService.getNewCurrentMember().getId(), quantity);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getCartProduct(@PathVariable Long productId) {
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return new CommonResult().success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public Object updateAttr(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.updateAttr(cartItem);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(String cart_id_list) {
        if (StringUtils.isEmpty(cart_id_list)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(cart_id_list.split(",").length);
        for (String s : cart_id_list.split(",")) {
            resultList.add(Long.valueOf(s));
        }
        int count = cartItemService.delete(memberService.getNewCurrentMember().getId(), resultList);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public Object clear() {
        int count = cartItemService.clear(memberService.getNewCurrentMember().getId());
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }


}
