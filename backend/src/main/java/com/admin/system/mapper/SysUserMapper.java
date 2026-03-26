package com.admin.system.mapper;

import com.admin.system.entity.SysUser;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User data layer
 *
 * @author Admin
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * Select user by username
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * Select user list (Entity)
     */
    List<SysUser> selectUserEntityList(SysUser user);

    /**
     * Select user page (VO)
     */
    Page<UserVO> selectUserPage(Page<SysUser> page,
                                 @Param("username") String username,
                                 @Param("phone") String phone,
                                 @Param("status") String status);

    /**
     * Select user by ID (VO)
     */
    UserVO selectUserVOById(@Param("userId") Long userId);

    /**
     * Check username unique
     */
    SysUser checkUsernameUnique(@Param("username") String username);

    /**
     * Check phone unique
     */
    SysUser checkPhoneUnique(@Param("phonenumber") String phonenumber);

    /**
     * Check email unique
     */
    SysUser checkEmailUnique(@Param("email") String email);

    /**
     * Batch insert user post
     */
    int batchUserPost(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    /**
     * Delete user post by user ID
     */
    int deleteUserPostByUserId(@Param("userId") Long userId);

    /**
     * Batch insert user role
     */
    int batchUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * Delete user role by user ID
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * Select post IDs by user ID
     */
    List<Long> selectPostIdsByUserId(@Param("userId") Long userId);

    /**
     * Select role IDs by user ID
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * Select user list (VO, for export)
     */
    List<UserVO> selectUserList(@Param("username") String username,
                                 @Param("phone") String phone,
                                 @Param("status") String status);

    /**
     * Count users by role ID
     */
    Long countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * Select users by role IDs
     */
    List<SysUser> selectUsersByRoleIds(@Param("roleIds") List<Long> roleIds);

}
