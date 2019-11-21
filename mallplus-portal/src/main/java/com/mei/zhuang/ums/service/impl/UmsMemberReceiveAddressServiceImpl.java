package com.mei.zhuang.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.zhuang.ums.service.IUmsMemberReceiveAddressService;
import com.mei.zhuang.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberReceiveAddress;
import com.zscat.mallplus.ums.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 会员收货地址表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class UmsMemberReceiveAddressServiceImpl extends ServiceImpl<UmsMemberReceiveAddressMapper, UmsMemberReceiveAddress> implements IUmsMemberReceiveAddressService {

    @Resource
    private UmsMemberReceiveAddressMapper addressMapper;
    @Autowired
    private IUmsMemberService memberService;
    @Override
    public UmsMemberReceiveAddress getDefaultItem() {

        UmsMember currentMember = memberService.getNewCurrentMember();
        UmsMemberReceiveAddress q = new UmsMemberReceiveAddress();
        q.setDefaultStatus(1);
        q.setMemberId(currentMember.getId());
        return this.getOne(new QueryWrapper<>(q));
    }

    @Transactional
    @Override
    public int setDefault(Long id) {
        UmsMember currentMember = memberService.getNewCurrentMember();
        addressMapper.updateStatusByMember(currentMember.getId());

        UmsMemberReceiveAddress def = new UmsMemberReceiveAddress();
        def.setId(id);
        def.setDefaultStatus(1);
        this.updateById(def);
        return 1;
    }
}
