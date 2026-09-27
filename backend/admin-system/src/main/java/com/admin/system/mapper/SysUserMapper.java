package com.admin.system.mapper;

import com.admin.system.entity.SysUser;
import com.admin.system.vo.GroupCountVO;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /** 按天统计 since 之后新增的用户数（groupKey = yyyy-MM-dd） */
    List<GroupCountVO> countCreatedByDay(@Param("since") LocalDateTime since);

    /** 按部门统计用户数（groupKey = dept_id） */
    List<GroupCountVO> countUsersGroupByDept();

    /** 按角色统计用户数（groupKey = role_id） */
    List<GroupCountVO> countUsersGroupByRole();

    List<SysUser> selectUsersByRoleIds(@Param("roleIds") List<Long> roleIds);

}
