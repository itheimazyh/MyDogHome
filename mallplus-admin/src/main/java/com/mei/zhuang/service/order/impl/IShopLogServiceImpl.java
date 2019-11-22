package com.mei.zhuang.service.order.impl;

import com.mei.zhuang.vo.order.EsCoreLogParam;
import com.mei.zhuang.vo.order.ExportParam;
import com.mei.zhuang.dao.order.EsCoreLogMapper;
import com.mei.zhuang.dao.order.EsCoreLogTypeMapper;
import com.mei.zhuang.service.order.IShopLogService;
import com.arvato.service.order.api.utils.ExportExcelUtil;
import com.arvato.service.order.api.utils.ExportExcelWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.zhuang.entity.order.EsCoreLog;
import com.mei.zhuang.entity.order.EsCoreLogType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: Tiger
 * @Date: 2019-06-21 16:00
 * @Description:
 */
@Service
public class IShopLogServiceImpl extends ServiceImpl<EsCoreLogMapper, EsCoreLog> implements IShopLogService {

    @Resource
    private EsCoreLogMapper esCoreLogMapper;
    @Resource
    private EsCoreLogTypeMapper esCoreLogTypeMapper;


    @Override
    public Page<EsCoreLog> selecPageList(EsCoreLogParam param) {
        Page<EsCoreLog> page = new Page<EsCoreLog>(param.getCurrent(), param.getSize());
        page.setAsc(param.getIsAsc() == 0 ? false : true);
        page.setRecords(esCoreLogMapper.selecPageList(param));
        page.setTotal(esCoreLogMapper.selectLogCount(param));
        return page;
    }

    @Override
    public boolean exportLogList(EsCoreLogParam entity, ExportParam exportParam, HttpServletResponse response) {
        Page page = new Page(entity.getCurrent(), entity.getSize());
        List<EsCoreLog> data = esCoreLogMapper.selecPageList(entity);
        try {
            ExportExcelWrapper<EsCoreLog> export = new ExportExcelWrapper<EsCoreLog>();
            export.exportExcel(exportParam.getFileName(), exportParam.getSheetName(), exportParam.getHeaders().split(","), exportParam.getColumns().split(","), data, response, ExportExcelUtil.EXCEl_FILE_2007);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<EsCoreLogType> getLogTypeList() {
        return esCoreLogTypeMapper.selectList(new QueryWrapper<>(new EsCoreLogType()));
    }
}
