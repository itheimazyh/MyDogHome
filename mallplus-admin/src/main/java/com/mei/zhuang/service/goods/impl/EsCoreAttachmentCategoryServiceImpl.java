package com.mei.zhuang.service.goods.impl;

import com.arvato.service.goods.api.orm.dao.EsCoreAttachmentCategoryMapper;
import com.arvato.service.goods.api.service.EsCoreAttachmentCategoryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mei.zhuang.entity.goods.EsCoreAttachmentCategory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EsCoreAttachmentCategoryServiceImpl extends ServiceImpl<EsCoreAttachmentCategoryMapper, EsCoreAttachmentCategory> implements EsCoreAttachmentCategoryService {

    @Resource
    private EsCoreAttachmentCategoryMapper esCoreAttachmentCategoryMapper;

    @Override
    public List<EsCoreAttachmentCategory> selectLists(EsCoreAttachmentCategory entity) {
        return esCoreAttachmentCategoryMapper.selectLists(entity);
    }
}
