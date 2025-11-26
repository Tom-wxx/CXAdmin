package com.admin.system.mapper;

import com.admin.system.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据部门ID查询信息
     */
    SysDept selectDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询所有子部门
     */
    List<SysDept> selectChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    int selectNormalChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 是否存在子节点
     */
    int hasChildByDeptId(@Param("deptId") Long deptId);

    /**
     * 查询部门是否存在用户
     */
    int checkDeptExistUser(@Param("deptId") Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 修改子元素关系
     */
    int updateDeptChildren(@Param("depts") List<SysDept> depts);

}
