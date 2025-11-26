package com.admin.system.mapper;

import com.admin.system.entity.SysUser;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 分页查询用户列表（返回VO）
     */
    Page<UserVO> selectUserPage(Page<SysUser> page,
                                 @Param("username") String username,
                                 @Param("phone") String phone,
                                 @Param("status") String status);

    /**
     * 根据用户ID查询用户详情（返回VO）
     */
    UserVO selectUserVOById(@Param("userId") Long userId);

    /**
     * 校验用户名称是否唯一
     */
    SysUser checkUsernameUnique(@Param("username") String username);

    /**
     * 校验手机号码是否唯一
     */
    SysUser checkPhoneUnique(@Param("phonenumber") String phonenumber);

    /**
     * 校验email是否唯一
     */
    SysUser checkEmailUnique(@Param("email") String email);

    /**
     * 批量新增用户岗位信息
     */
    int batchUserPost(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    /**
     * 删除用户和岗位关联
     */
    int deleteUserPostByUserId(@Param("userId") Long userId);

    /**
     * 批量新增用户角色信息
     */
    int batchUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除用户和角色关联
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询岗位ID列表
     */
    List<Long> selectPostIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

}
