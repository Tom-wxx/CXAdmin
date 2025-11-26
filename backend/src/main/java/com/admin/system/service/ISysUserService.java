package com.admin.system.service;

import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author Admin
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    Page<UserVO> selectUserPage(Page<SysUser> page, String username, String phone, String status);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUsername(String username);

    /**
     * 根据用户ID查询用户详情
     */
    UserVO selectUserById(Long userId);

    /**
     * 新增用户
     */
    void insertUser(UserDTO userDTO);

    /**
     * 修改用户
     */
    void updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUserById(Long userId);

    /**
     * 批量删除用户
     */
    void deleteUserByIds(Long[] userIds);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 修改用户状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 校验用户名是否唯一
     */
    boolean checkUsernameUnique(String username, Long userId);

    /**
     * 校验手机号是否唯一
     */
    boolean checkPhoneUnique(String phone, Long userId);

    /**
     * 校验邮箱是否唯一
     */
    boolean checkEmailUnique(String email, Long userId);

    /**
     * 根据用户ID查询岗位ID列表
     */
    List<Long> selectPostIdsByUserId(Long userId);

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);
}
