package com.admin.system.service;

import com.admin.system.entity.SysDictData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典数据 业务层
 *
 * @author Admin
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 分页查询字典数据列表
     */
    Page<SysDictData> selectDictDataPage(Page<SysDictData> page, String dictType, String dictLabel, String status);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 新增字典数据
     */
    void insertDictData(SysDictData dictData);

    /**
     * 修改字典数据
     */
    void updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    void deleteDictDataById(Long dictCode);

    /**
     * 批量删除字典数据
     */
    void deleteDictDataByIds(Long[] dictCodes);
}
