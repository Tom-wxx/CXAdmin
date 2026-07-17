package com.admin.system.service;

import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户 业务层
 *
 * @author Admin
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     * @param query 查询条件载体（同时承载数据权限过滤片段，须为 {@link SysUser} 以触发 {@code @DataScope}）
     */
    Page<UserVO> selectUserPage(Page<SysUser> page, SysUser query);

    SysUser selectUserByUsername(String username);

    UserVO selectUserById(Long userId);

    UserVO selectCurrentUserProfile();

    void insertUser(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void updateCurrentUserProfile(UserDTO userDTO);

    void deleteUserById(Long userId);

    void deleteUserByIds(Long[] userIds);

    void resetPassword(Long userId, String newPassword);

    void updateCurrentUserPassword(String oldPassword, String newPassword);

    void updateUserStatus(Long userId, String status);

    boolean checkUsernameUnique(String username, Long userId);

    boolean checkPhoneUnique(String phone, Long userId);

    boolean checkEmailUnique(String email, Long userId);

    List<Long> selectPostIdsByUserId(Long userId);

    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 查询用户列表（不分页，用于导出）
     */
    List<UserVO> selectUserList(SysUser query);

    Map<String, Object> importUsers(MultipartFile file, boolean updateSupport);
}
