package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysDictType;
import com.admin.system.mapper.SysDictTypeMapper;
import com.admin.system.service.ISysDictTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典类型 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;

    /**
     * 分页查询字典类型列表
     */
    @Override
    public Page<SysDictType> selectDictTypePage(Page<SysDictType> page, String dictName, String dictType, String status) {
        return dictTypeMapper.selectDictTypePage(page, dictName, dictType, status);
    }

    /**
     * 查询所有字典类型
     */
    @Override
    public List<SysDictType> selectDictTypeList() {
        return dictTypeMapper.selectDictTypeList();
    }

    /**
     * 根据字典类型查询字典
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 新增字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDictType(SysDictType dictType) {
        // 校验字典类型唯一性
        if (!checkDictTypeUnique(dictType.getDictType(), null)) {
            throw new ServiceException("新增字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        dictTypeMapper.insert(dictType);
    }

    /**
     * 修改字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(SysDictType dictType) {
        if (dictType.getDictId() == null) {
            throw new ServiceException("字典ID不能为空");
        }

        // 校验字典类型唯一性
        if (!checkDictTypeUnique(dictType.getDictType(), dictType.getDictId())) {
            throw new ServiceException("修改字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }

        dictTypeMapper.updateById(dictType);
    }

    /**
     * 删除字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeById(Long dictId) {
        if (dictId == null) {
            throw new ServiceException("字典ID不能为空");
        }

        SysDictType dictType = dictTypeMapper.selectById(dictId);
        if (dictType == null) {
            throw new ServiceException("字典类型不存在");
        }

        // 检查是否存在字典数据
        int count = dictTypeMapper.checkDictDataExist(dictType.getDictType());
        if (count > 0) {
            throw new ServiceException("该字典类型下存在字典数据，不能删除");
        }

        dictTypeMapper.deleteById(dictId);
    }

    /**
     * 批量删除字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(Long[] dictIds) {
        if (dictIds == null || dictIds.length == 0) {
            throw new ServiceException("字典ID不能为空");
        }

        for (Long dictId : dictIds) {
            deleteDictTypeById(dictId);
        }
    }

    /**
     * 校验字典类型是否唯一
     */
    @Override
    public boolean checkDictTypeUnique(String dictType, Long dictId) {
        Long did = dictId == null ? -1L : dictId;
        SysDictType info = dictTypeMapper.checkDictTypeUnique(dictType);
        if (info != null && !info.getDictId().equals(did)) {
            return false;
        }
        return true;
    }
}
