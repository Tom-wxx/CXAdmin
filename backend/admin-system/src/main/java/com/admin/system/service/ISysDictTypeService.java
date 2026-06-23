package com.admin.system.service;

import com.admin.system.entity.SysDictType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典类型 业务层
 *
 * @author Admin
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 分页查询字典类型列表
     */
    Page<SysDictType> selectDictTypePage(Page<SysDictType> page, String dictName, String dictType, String status);

    /**
     * 查询所有字典类型
     */
    List<SysDictType> selectDictTypeList();

    /**
     * 根据字典类型查询字典
     */
    SysDictType selectDictTypeByType(String dictType);

    /**
     * 新增字典类型
     */
    void insertDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    void updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    void deleteDictTypeById(Long dictId);

    /**
     * 批量删除字典类型
     */
    void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 校验字典类型是否唯一
     */
    boolean checkDictTypeUnique(String dictType, Long dictId);
}
