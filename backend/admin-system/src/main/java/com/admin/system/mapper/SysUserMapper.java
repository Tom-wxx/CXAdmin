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

    SysUser selectUserByUsername(@Param("username") String username);

    List<SysUser> selectUserEntityList(SysUser user);

    Page<UserVO> selectUserPage(@Param("page") Page<SysUser> page, @Param("query") SysUser query);

    UserVO selectUserVOById(@Param("userId") Long userId);

    SysUser checkUsernameUnique(@Param("username") String username);

    SysUser checkPhoneUnique(@Param("phonenumber") String phonenumber);

    SysUser checkEmailUnique(@Param("email") String email);

    int batchUserPost(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    int deleteUserPostByUserId(@Param("userId") Long userId);

    int batchUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int deleteUserRoleByUserId(@Param("userId") Long userId);

    List<Long> selectPostIdsByUserId(@Param("userId") Long userId);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    List<UserVO> selectUserList(@Param("query") SysUser query);

    /**
     * Count whether a target user is visible within the current user's data scope
     * (data-scope SQL fragment appended via {@code @DataScope}). Used for by-id authz checks.
     */
    long countUserInScope(@Param("query") SysUser query);

    Long countUsersByRoleId(@Param("roleId") Long roleId);

    List<SysUser> selectUsersByRoleIds(@Param("roleIds") List<Long> roleIds);

}
