package com.admin.system.service.impl;

import com.admin.common.annotation.DataScope;
import com.admin.common.exception.ServiceException;
import com.admin.system.datascope.DataScopeChecker;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ISysUserService;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import com.admin.common.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * 用户 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserMapper userMapper;
    private final DataScopeChecker dataScopeChecker;

    /**
     * 分页查询用户列表
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Page<UserVO> selectUserPage(Page<SysUser> page, SysUser query) {
        return userMapper.selectUserPage(page, query);
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
        checkUserDataScope(userId);
        return userMapper.selectUserVOById(userId);
    }

    @Override
    public UserVO selectCurrentUserProfile() {
        return selectUserById(requireCurrentUserId());
    }

    /**
     * 新增用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(UserDTO userDTO) {
        if (!checkUsernameUnique(userDTO.getUsername(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，用户名已存在");
        }

        if (StringUtils.hasText(userDTO.getPhone()) && !checkPhoneUnique(userDTO.getPhone(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，手机号已存在");
        }

        if (StringUtils.hasText(userDTO.getEmail()) && !checkEmailUnique(userDTO.getEmail(), null)) {
            throw new ServiceException("新增用户'" + userDTO.getUsername() + "'失败，邮箱已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);

        // 处理字段名差异：phone -> phonenumber, gender -> sex
        user.setPhonenumber(userDTO.getPhone());
        user.setSex(userDTO.getGender());

        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(userDTO.getPassword()));
        } else {
            user.setPassword(SecurityUtils.encryptPassword(SystemConstants.DEFAULT_PASSWORD));
        }

        userMapper.insert(user);

        insertUserRole(user.getUserId(), userDTO.getRoleIds());

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
        // 垂直越权防护：禁止操作超级管理员；水平越权防护：目标用户须在当前用户数据范围内
        checkUserAllowed(userDTO.getUserId());
        checkUserDataScope(userDTO.getUserId());

        if (!checkUsernameUnique(userDTO.getUsername(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，用户名已存在");
        }

        if (StringUtils.hasText(userDTO.getPhone()) && !checkPhoneUnique(userDTO.getPhone(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，手机号已存在");
        }

        if (StringUtils.hasText(userDTO.getEmail()) && !checkEmailUnique(userDTO.getEmail(), userDTO.getUserId())) {
            throw new ServiceException("修改用户'" + userDTO.getUsername() + "'失败，邮箱已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);

        // 处理字段名差异
        user.setPhonenumber(userDTO.getPhone());
        user.setSex(userDTO.getGender());

        // 更新用户（密码不在此处更新）
        user.setPassword(null);
        userMapper.updateById(user);

        userMapper.deleteUserRoleByUserId(user.getUserId());
        insertUserRole(user.getUserId(), userDTO.getRoleIds());

        userMapper.deleteUserPostByUserId(user.getUserId());
        insertUserPost(user.getUserId(), userDTO.getPostIds());
    }

    /**
     * Update current logged-in user profile.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserProfile(UserDTO userDTO) {
        Long currentUserId = requireCurrentUserId();
        if (userDTO == null) {
            throw new ServiceException("Profile data cannot be empty");
        }
        if (!StringUtils.hasText(userDTO.getNickname())) {
            throw new ServiceException("Nickname cannot be empty");
        }
        if (StringUtils.hasText(userDTO.getPhone()) && !checkPhoneUnique(userDTO.getPhone(), currentUserId)) {
            throw new ServiceException("Phone already exists");
        }
        if (StringUtils.hasText(userDTO.getEmail()) && !checkEmailUnique(userDTO.getEmail(), currentUserId)) {
            throw new ServiceException("Email already exists");
        }

        SysUser user = new SysUser();
        user.setUserId(currentUserId);
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPhonenumber(userDTO.getPhone());
        user.setSex(userDTO.getGender());
        userMapper.updateById(user);
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
        // 垂直越权防护：禁止操作超级管理员；水平越权防护：目标用户须在当前用户数据范围内
        checkUserAllowed(userId);
        checkUserDataScope(userId);

        userMapper.deleteUserRoleByUserId(userId);
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
        // 垂直越权防护：禁止重置超级管理员密码；水平越权防护：目标用户须在当前用户数据范围内
        checkUserAllowed(userId);
        checkUserDataScope(userId);

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        userMapper.updateById(user);
    }

    /**
     * Update current logged-in user password.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserPassword(String oldPassword, String newPassword) {
        Long currentUserId = requireCurrentUserId();
        if (!StringUtils.hasText(oldPassword)) {
            throw new ServiceException("Old password cannot be empty");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new ServiceException("New password cannot be empty");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new ServiceException("Password length must be between 6 and 20 characters");
        }

        SysUser currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new ServiceException("Current user does not exist");
        }
        if (!SecurityUtils.matchesPassword(oldPassword, currentUser.getPassword())) {
            throw new ServiceException("Old password is incorrect");
        }

        SysUser user = new SysUser();
        user.setUserId(currentUserId);
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
        // 垂直越权防护：禁止停用/启用超级管理员；水平越权防护：目标用户须在当前用户数据范围内
        checkUserAllowed(userId);
        checkUserDataScope(userId);

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    private Long requireCurrentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException("Current user is not logged in");
        }
        return userId;
    }

    /**
     * 垂直越权防护：禁止对超级管理员（id=1）执行管理操作（改/删/重置密码/改状态）。
     */
    private void checkUserAllowed(Long userId) {
        if (userId != null && userId.equals(SystemConstants.SUPER_ADMIN_ID)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 水平越权防护：校验目标用户是否落在当前登录用户的数据范围内。
     * 复用与列表完全一致的 {@code @DataScope} 过滤（经独立 Bean 触发 AOP 代理）；
     * 超级管理员经切面放行。无登录上下文（系统内部调用）视为放行。
     */
    private void checkUserDataScope(Long userId) {
        if (userId == null) {
            return;
        }
        SysUser query = new SysUser();
        query.setUserId(userId);
        if (dataScopeChecker.countUserInScope(query) == 0) {
            throw new ServiceException("没有权限访问该用户数据");
        }
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

    /**
     * 查询用户列表（不分页，用于导出）
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<UserVO> selectUserList(SysUser query) {
        return userMapper.selectUserList(query);
    }

    /**
     * 导入用户数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importUsers(MultipartFile file, boolean updateSupport) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> failureMessages = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            // 从第二行开始读取（第一行是表头）
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                try {
                    String username = getCellValue(row.getCell(0));
                    String nickname = getCellValue(row.getCell(1));
                    String deptIdStr = getCellValue(row.getCell(2));
                    String phone = getCellValue(row.getCell(3));
                    String email = getCellValue(row.getCell(4));
                    String gender = getCellValue(row.getCell(5));
                    String password = getCellValue(row.getCell(6));

                    if (!StringUtils.hasText(username)) {
                        failureCount++;
                        failureMessages.add("第" + (i + 1) + "行: 用户名不能为空");
                        continue;
                    }

                    SysUser existUser = userMapper.checkUsernameUnique(username);

                    if (existUser != null) {
                        if (updateSupport) {
                            existUser.setNickname(StringUtils.hasText(nickname) ? nickname : existUser.getNickname());
                            existUser.setPhonenumber(StringUtils.hasText(phone) ? phone : existUser.getPhonenumber());
                            existUser.setEmail(StringUtils.hasText(email) ? email : existUser.getEmail());
                            existUser.setSex(StringUtils.hasText(gender) ? gender : existUser.getSex());
                            if (StringUtils.hasText(deptIdStr)) {
                                existUser.setDeptId(Long.parseLong(deptIdStr));
                            }
                            userMapper.updateById(existUser);
                            successCount++;
                        } else {
                            failureCount++;
                            failureMessages.add("第" + (i + 1) + "行: 用户名'" + username + "'已存在");
                        }
                    } else {
                        SysUser user = new SysUser();
                        user.setUsername(username);
                        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
                        user.setPhonenumber(phone);
                        user.setEmail(email);
                        user.setSex(StringUtils.hasText(gender) ? gender : SystemConstants.STATUS_NORMAL);
                        if (StringUtils.hasText(deptIdStr)) {
                            user.setDeptId(Long.parseLong(deptIdStr));
                        }
                        String pwd = StringUtils.hasText(password) ? password : SystemConstants.DEFAULT_PASSWORD;
                        user.setPassword(SecurityUtils.encryptPassword(pwd));
                        user.setStatus(SystemConstants.STATUS_NORMAL);

                        userMapper.insert(user);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    failureMessages.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new ServiceException("导入失败: " + e.getMessage());
        }

        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("failureMessages", failureMessages);

        return result;
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}
