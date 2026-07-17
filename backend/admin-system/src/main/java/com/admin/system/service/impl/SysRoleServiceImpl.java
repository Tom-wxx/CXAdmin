package com.admin.system.service.impl;

import com.admin.common.exception.ServiceException;
import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.mapper.SysRoleMapper;
import com.admin.system.service.ISysRoleService;
import com.admin.system.vo.RoleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.admin.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;

    /**
     * 分页查询角色列表
     */
    @Override
    public Page<RoleVO> selectRolePage(Page<SysRole> page, String roleName, String roleKey, String status) {
        return roleMapper.selectRolePage(page, roleName, roleKey, status);
    }

    /**
     * 根据角色ID查询角色详情
     */
    @Override
    public RoleVO selectRoleById(Long roleId) {
        return roleMapper.selectRoleVOById(roleId);
    }

    /**
     * 查询所有角色列表
     */
    @Override
    public List<RoleVO> selectRoleList() {
        return roleMapper.selectRoleList();
    }

    /**
     * 根据用户ID查询角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    /**
     * 新增角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRole(RoleDTO roleDTO) {
        if (!checkRoleNameUnique(roleDTO.getRoleName(), null)) {
            throw new ServiceException("新增角色'" + roleDTO.getRoleName() + "'失败，角色名称已存在");
        }

        if (!checkRoleKeyUnique(roleDTO.getRoleKey(), null)) {
            throw new ServiceException("新增角色'" + roleDTO.getRoleName() + "'失败，角色权限已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);

        roleMapper.insert(role);

        if (roleDTO.getMenuIds() != null && roleDTO.getMenuIds().length > 0) {
            saveRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
        }
    }

    /**
     * 修改角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO roleDTO) {
        if (roleDTO.getRoleId() == null) {
            throw new ServiceException("角色ID不能为空");
        }

        if (!checkRoleNameUnique(roleDTO.getRoleName(), roleDTO.getRoleId())) {
            throw new ServiceException("修改角色'" + roleDTO.getRoleName() + "'失败，角色名称已存在");
        }

        if (!checkRoleKeyUnique(roleDTO.getRoleKey(), roleDTO.getRoleId())) {
            throw new ServiceException("修改角色'" + roleDTO.getRoleName() + "'失败，角色权限已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);

        roleMapper.updateById(role);

        roleMapper.deleteRoleMenuByRoleId(role.getRoleId());

        if (roleDTO.getMenuIds() != null && roleDTO.getMenuIds().length > 0) {
            saveRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
        }
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleById(Long roleId) {
        if (roleId == null) {
            throw new ServiceException("角色ID不能为空");
        }

        Long userCount = userMapper.countUsersByRoleId(roleId);
        if (userCount != null && userCount > 0) {
            SysRole role = roleMapper.selectById(roleId);
            String roleName = role != null ? role.getRoleName() : String.valueOf(roleId);
            throw new ServiceException("角色'" + roleName + "'已分配给" + userCount + "个用户，不能删除");
        }

        roleMapper.deleteRoleMenuByRoleId(roleId);

        roleMapper.deleteById(roleId);
    }

    /**
     * 批量删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleByIds(Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new ServiceException("角色ID不能为空");
        }

        for (Long roleId : roleIds) {
            deleteRoleById(roleId);
        }
    }

    /**
     * 修改角色状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long roleId, String status) {
        if (roleId == null) {
            throw new ServiceException("角色ID不能为空");
        }

        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    /**
     * 校验角色名称是否唯一
     */
    @Override
    public boolean checkRoleNameUnique(String roleName, Long roleId) {
        Long rid = roleId == null ? -1L : roleId;
        SysRole info = roleMapper.checkRoleNameUnique(roleName);
        if (info != null && !info.getRoleId().equals(rid)) {
            return false;
        }
        return true;
    }

    /**
     * 校验角色权限是否唯一
     */
    @Override
    public boolean checkRoleKeyUnique(String roleKey, Long roleId) {
        Long rid = roleId == null ? -1L : roleId;
        SysRole info = roleMapper.checkRoleKeyUnique(roleKey);
        if (info != null && !info.getRoleId().equals(rid)) {
            return false;
        }
        return true;
    }

    /**
     * 查询角色已分配的菜单ID列表
     */
    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 保存角色菜单权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenus(Long roleId, Long[] menuIds) {
        if (roleId == null) {
            throw new ServiceException("角色ID不能为空");
        }

        roleMapper.deleteRoleMenuByRoleId(roleId);

        if (menuIds != null && menuIds.length > 0) {
            roleMapper.batchRoleMenu(roleId, menuIds);
        }
    }

    /**
     * 查询角色列表（用于导出）
     */
    @Override
    public List<RoleVO> selectRoleListForExport(String roleName, String roleKey, String status) {
        return roleMapper.selectRoleListForExport(roleName, roleKey, status);
    }
}
