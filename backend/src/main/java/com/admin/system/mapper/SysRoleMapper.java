package com.admin.system.mapper;

import com.admin.system.entity.SysRole;
import com.admin.system.vo.RoleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 分页查询角色列表（返回VO）
     */
    Page<RoleVO> selectRolePage(Page<SysRole> page,
                                 @Param("roleName") String roleName,
                                 @Param("roleKey") String roleKey,
                                 @Param("status") String status);

    /**
     * 根据角色ID查询角色详情（返回VO）
     */
    RoleVO selectRoleVOById(@Param("roleId") Long roleId);

    /**
     * 查询所有角色列表（返回VO）
     */
    List<RoleVO> selectRoleList();

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 校验角色名称是否唯一
     */
    SysRole checkRoleNameUnique(@Param("roleName") String roleName);

    /**
     * 校验角色权限是否唯一
     */
    SysRole checkRoleKeyUnique(@Param("roleKey") String roleKey);

    /**
     * 查询角色已分配的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色与菜单关联
     */
    int deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增角色菜单信息
     */
    int batchRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    /**
     * 查询角色列表（用于导出）
     */
    List<RoleVO> selectRoleListForExport(@Param("roleName") String roleName,
                                          @Param("roleKey") String roleKey,
                                          @Param("status") String status);
}
