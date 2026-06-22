package com.admin.system.service;

import com.admin.system.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 部门管理 业务层
 *
 * @author Admin
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门管理数据（树形结构）
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 构建前端所需要的树形结构
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 根据部门ID查询信息
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在子节点
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 新增部门信息
     */
    void insertDept(SysDept dept);

    /**
     * 修改部门信息
     */
    void updateDept(SysDept dept);

    /**
     * 删除部门管理信息
     */
    void deleteDeptById(Long deptId);

}
