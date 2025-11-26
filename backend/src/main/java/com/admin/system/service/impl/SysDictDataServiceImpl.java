package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysDictData;
import com.admin.system.mapper.SysDictDataMapper;
import com.admin.system.service.ISysDictDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典数据 业务层处理
 *
 * @author Admin
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 分页查询字典数据列表
     */
    @Override
    public Page<SysDictData> selectDictDataPage(Page<SysDictData> page, String dictType, String dictLabel, String status) {
        return dictDataMapper.selectDictDataPage(page, dictType, dictLabel, status);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        return dictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 新增字典数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDictData(SysDictData dictData) {
        dictDataMapper.insert(dictData);
    }

    /**
     * 修改字典数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(SysDictData dictData) {
        if (dictData.getDictCode() == null) {
            throw new ServiceException("字典编码不能为空");
        }
        dictDataMapper.updateById(dictData);
    }

    /**
     * 删除字典数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataById(Long dictCode) {
        if (dictCode == null) {
            throw new ServiceException("字典编码不能为空");
        }
        dictDataMapper.deleteById(dictCode);
    }

    /**
     * 批量删除字典数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataByIds(Long[] dictCodes) {
        if (dictCodes == null || dictCodes.length == 0) {
            throw new ServiceException("字典编码不能为空");
        }

        for (Long dictCode : dictCodes) {
            deleteDictDataById(dictCode);
        }
    }
}
