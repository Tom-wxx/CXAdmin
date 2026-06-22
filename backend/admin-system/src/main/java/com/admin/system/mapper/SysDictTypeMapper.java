package com.admin.system.mapper;

import com.admin.system.entity.SysDictType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典类型表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 分页查询字典类型列表
     */
    Page<SysDictType> selectDictTypePage(Page<SysDictType> page,
                                          @Param("dictName") String dictName,
                                          @Param("dictType") String dictType,
                                          @Param("status") String status);

    /**
     * 查询所有字典类型
     */
    List<SysDictType> selectDictTypeList();

    /**
     * 根据字典类型查询字典
     */
    SysDictType selectDictTypeByType(@Param("dictType") String dictType);

    /**
     * 校验字典类型是否唯一
     */
    SysDictType checkDictTypeUnique(@Param("dictType") String dictType);

    /**
     * 检查字典类型是否存在字典数据
     */
    int checkDictDataExist(@Param("dictType") String dictType);
}
