package com.admin.system.mapper;

import com.admin.system.entity.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 分页查询字典数据列表
     */
    Page<SysDictData> selectDictDataPage(Page<SysDictData> page,
                                          @Param("dictType") String dictType,
                                          @Param("dictLabel") String dictLabel,
                                          @Param("status") String status);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);

    /**
     * 查询字典数据列表
     */
    List<SysDictData> selectDictDataList(@Param("dictData") SysDictData dictData);
}
