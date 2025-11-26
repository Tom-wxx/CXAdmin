package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ISysUserService;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 用户 业务层处理
 *
 * @author Admin
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 分页查询用户列表
     */
    @Override
    public Page<UserVO> selectUserPage(Page<SysUser> page, String username, String phone, String status) {
        return userMapper.selectUserPage(page, username, phone, status);
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public SysUser selectUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    /**
     * 根据用户ID查询用户详情
     */
    @Override
    public UserVO selectUserById(Long userId) {
        return userMapper.selectUserVOById(userId);
    }

    /**
     * 新增用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(UserDTO userDTO) {
        // 校验用户名唯一性
        if (!checkUsernameUnique(userDTO.getUsername(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，用户名已存在");
        }

        // 校验手机号唯一性
        if (StringUtils.hasText(userDTO.getPhone()) && !checkPhoneUnique(userDTO.getPhone(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，手机号已存在");
        }

        // 校验邮箱唯一性
        if (StringUtils.hasText(userDTO.getEmail()) && !checkEmailUnique(userDTO.getEmail(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，邮箱已存在");
        }

        // DTO 转 Entity
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);

        // 处理字段名差异：phone -> phonenumber, gender -> sex
        user.setPhonenumber(userDTO.getPhone());
        user.setSex(userDTO.getGender());

        // 加密密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(userDTO.getPassword()));
        } else {
            // 默认密码
            user.setPassword(SecurityUtils.encryptPassword("123456"));
        }

        // 保存用户
        userMapper.insert(user);

        // 保存用户与角色关联
        insertUserRole(user.getUserId(), userDTO.getRoleIds());

        // 保存用户与岗位关联
        insertUserPost(user.getUserId(), userDTO.getPostIds());
    }

    /**
     * 修改用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            throw new ServiceException("用户ID不能为空");
        }

        // 校验用户名唯一性
        if (!checkUsernameUnique(userDTO.getUsername(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，用户名已存在");
        }

        // 校验手机号唯一性
        if (StringUtils.hasText(userDTO.getPhone()) && !checkPhoneUnique(userDTO.getPhone(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，手机号已存在");
        }

        // 校验邮箱唯一性
        if (StringUtils.hasText(userDTO.getEmail()) && !checkEmailUnique(userDTO.getEmail(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，邮箱已存在");
        }

        // DTO 转 Entity
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);

        // 处理字段名差异
        user.setPhonenumber(userDTO.getPhone());
        user.setSex(userDTO.getGender());

        // 更新用户（密码不在此处更新）
        user.setPassword(null);
        userMapper.updateById(user);

        // 删除用户与角色关联
        userMapper.deleteUserRoleByUserId(user.getUserId());
        // 新增用户与角色关联
        insertUserRole(user.getUserId(), userDTO.getRoleIds());

        // 删除用户与岗位关联
        userMapper.deleteUserPostByUserId(user.getUserId());
        // 新增用户与岗位关联
        insertUserPost(user.getUserId(), userDTO.getPostIds());
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserById(Long userId) {
        if (userId == null) {
            throw new ServiceException("用户ID不能为空");
        }

        // TODO: 检查用户是否为超级管理员

        // 删除用户与角色关联
        userMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位关联
        userMapper.deleteUserPostByUserId(userId);

        userMapper.deleteById(userId);
    }

    /**
     * 批量删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByIds(Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            throw new ServiceException("用户ID不能为空");
        }

        for (Long userId : userIds) {
            deleteUserById(userId);
        }
    }

    /**
     * 重置密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        if (userId == null) {
            throw new ServiceException("用户ID不能为空");
        }

        if (!StringUtils.hasText(newPassword)) {
            throw new ServiceException("新密码不能为空");
        }

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        userMapper.updateById(user);
    }

    /**
     * 修改用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, String status) {
        if (userId == null) {
            throw new ServiceException("用户ID不能为空");
        }

        if (!StringUtils.hasText(status)) {
            throw new ServiceException("用户状态不能为空");
        }

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 校验用户名是否唯一
     */
    @Override
    public boolean checkUsernameUnique(String username, Long userId) {
        Long uid = userId == null ? -1L : userId;
        SysUser info = userMapper.checkUsernameUnique(username);
        if (info != null && !info.getUserId().equals(uid)) {
            return false;
        }
        return true;
    }

    /**
     * 校验手机号是否唯一
     */
    @Override
    public boolean checkPhoneUnique(String phone, Long userId) {
        Long uid = userId == null ? -1L : userId;
        SysUser info = userMapper.checkPhoneUnique(phone);
        if (info != null && !info.getUserId().equals(uid)) {
            return false;
        }
        return true;
    }

    /**
     * 校验邮箱是否唯一
     */
    @Override
    public boolean checkEmailUnique(String email, Long userId) {
        Long uid = userId == null ? -1L : userId;
        SysUser info = userMapper.checkEmailUnique(email);
        if (info != null && !info.getUserId().equals(uid)) {
            return false;
        }
        return true;
    }

    /**
     * 新增用户角色信息
     */
    private void insertUserRole(Long userId, Long[] roleIds) {
        if (roleIds != null && roleIds.length > 0) {
            List<Long> roleIdList = Arrays.asList(roleIds);
            userMapper.batchUserRole(userId, roleIdList);
        }
    }

    /**
     * 新增用户岗位信息
     */
    private void insertUserPost(Long userId, Long[] postIds) {
        if (postIds != null && postIds.length > 0) {
            List<Long> postIdList = Arrays.asList(postIds);
            userMapper.batchUserPost(userId, postIdList);
        }
    }

    /**
     * 根据用户ID查询岗位ID列表
     */
    @Override
    public List<Long> selectPostIdsByUserId(Long userId) {
        return userMapper.selectPostIdsByUserId(userId);
    }

    /**
     * 根据用户ID查询角色ID列表
     */
    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return userMapper.selectRoleIdsByUserId(userId);
    }

}
