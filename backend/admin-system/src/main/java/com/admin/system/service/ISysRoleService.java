package com.admin.system.service;

import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.vo.RoleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色 业务层
 *
 * @author Admin
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    Page<RoleVO> selectRolePage(Page<SysRole> page, String roleName, String roleKey, String status);

    /**
     * 根据角色ID查询角色详情
     */
    RoleVO selectRoleById(Long roleId);

    /**
     * 查询所有角色列表
     */
    List<RoleVO> selectRoleList();

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 新增角色
     */
    void insertRole(RoleDTO roleDTO);

    /**
     * 修改角色
     */
    void updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     */
    void deleteRoleById(Long roleId);

    /**
     * 批量删除角色
     */
    void deleteRoleByIds(Long[] roleIds);

    /**
     * 修改角色状态
     */
    void updateRoleStatus(Long roleId, String status);

    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(String roleName, Long roleId);

    /**
     * 校验角色权限是否唯一
     */
    boolean checkRoleKeyUnique(String roleKey, Long roleId);

    /**
     * 查询角色已分配的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /**
     * 保存角色菜单权限
     */
    void saveRoleMenus(Long roleId, Long[] menuIds);

    /**
     * 查询角色列表（用于导出）
     */
    List<RoleVO> selectRoleListForExport(String roleName, String roleKey, String status);
}
