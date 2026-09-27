package com.admin.system.service.impl;

import com.admin.common.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.system.entity.SysDept;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.IOnlineUserService;
import com.admin.common.constants.SystemConstants;
import com.admin.system.service.ISysDeptService;
import com.admin.common.utils.RedisUtil;
import com.admin.system.vo.OnlineUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线用户 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements IOnlineUserService {

    private final RedisUtil redisUtil;
    private final ISysDeptService deptService;

    /**
     * 查询在线用户列表
     */
    @Override
    public List<OnlineUserVO> selectOnlineUserList(String username, String ipaddr) {
        List<String> keys = new ArrayList<>(redisUtil.scanKeys(SystemConstants.LOGIN_TOKEN_KEY + "*"));
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        // 一次 MGET 取回全部会话，避免逐键往返
        List<Object> values = redisUtil.multiGet(keys);
        List<LoginUser> loginUsers = new ArrayList<>();
        List<String> loginKeys = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            if (values.get(i) instanceof LoginUser loginUser) {
                loginUsers.add(loginUser);
                loginKeys.add(keys.get(i));
            }
        }

        Map<Long, String> deptNames = loadDeptNames(loginUsers);

        List<OnlineUserVO> onlineUserList = new ArrayList<>();
        for (int i = 0; i < loginUsers.size(); i++) {
            OnlineUserVO onlineUser = convertToVO(loginUsers.get(i), loginKeys.get(i), deptNames);

            // 筛选条件
            if (username != null && !username.isEmpty()
                && !onlineUser.getUsername().contains(username)) {
                continue;
            }
            if (ipaddr != null && !ipaddr.isEmpty()
                && (onlineUser.getIpaddr() == null || !onlineUser.getIpaddr().contains(ipaddr))) {
                continue;
            }

            onlineUserList.add(onlineUser);
        }

        // 按登录时间降序排序
        return onlineUserList.stream()
                .sorted((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 统计在线用户数
     */
    @Override
    public long countOnlineUsers() {
        List<String> keys = new ArrayList<>(redisUtil.scanKeys(SystemConstants.LOGIN_TOKEN_KEY + "*"));
        if (keys.isEmpty()) {
            return 0L;
        }

        return redisUtil.multiGet(keys).stream()
                .filter(LoginUser.class::isInstance)
                .count();
    }

    @Override
    public PageResult<OnlineUserVO> selectOnlineUserListPage(String username, String ipaddr, Integer current, Integer size) {
        // 获取全部在线用户
        List<OnlineUserVO> allUsers = selectOnlineUserList(username, ipaddr);

        // 计算分页
        long total = allUsers.size();
        int fromIndex = (current - 1) * size;
        int toIndex = Math.min(fromIndex + size, allUsers.size());

        // 获取当前页数据
        List<OnlineUserVO> pageData;
        if (fromIndex >= allUsers.size()) {
            pageData = new ArrayList<>();
        } else {
            pageData = allUsers.subList(fromIndex, toIndex);
        }

        return PageResult.build(pageData, total, current, size);
    }

    /**
     * 强制退出用户
     */
    @Override
    public void forceLogout(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            throw new ServiceException("会话编号不能为空");
        }

        String key = SystemConstants.LOGIN_TOKEN_KEY + tokenId;
        if (!redisUtil.hasKey(key)) {
            throw new ServiceException("用户已退出或会话已过期");
        }

        redisUtil.delete(key);
    }

    /**
     * 批量强制退出用户
     */
    @Override
    public void batchForceLogout(String[] tokenIds) {
        if (tokenIds == null || tokenIds.length == 0) {
            throw new ServiceException("会话编号不能为空");
        }

        List<String> keys = new ArrayList<>();
        for (String tokenId : tokenIds) {
            keys.add(SystemConstants.LOGIN_TOKEN_KEY + tokenId);
        }

        redisUtil.delete(keys);
    }

    /**
     * 一次查询取回所有在线用户的部门名称（避免逐个用户查部门的 N+1）
     */
    private Map<Long, String> loadDeptNames(List<LoginUser> loginUsers) {
        Set<Long> deptIds = loginUsers.stream()
                .map(LoginUser::getUser)
                .filter(Objects::nonNull)
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (deptIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return deptService.listByIds(deptIds).stream()
                .filter(dept -> dept.getDeptName() != null)
                .collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
    }

    /**
     * 转换为VO对象
     */
    private OnlineUserVO convertToVO(LoginUser loginUser, String key, Map<Long, String> deptNames) {
        OnlineUserVO vo = new OnlineUserVO();

        // 提取token（去掉前缀 "login_tokens:"）
        String token = key.substring(SystemConstants.LOGIN_TOKEN_KEY.length());
        vo.setTokenId(token);

        // 用户信息
        SysUser user = loginUser.getUser();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());

        // 部门信息
        if (user.getDeptId() != null) {
            vo.setDeptName(deptNames.get(user.getDeptId()));
        }

        // 登录信息
        vo.setIpaddr(user.getLoginIp());
        vo.setLoginLocation(""); // 可以根据IP解析地址

        // 时间信息
        if (loginUser.getLoginTime() != null) {
            vo.setLoginTime(new Date(loginUser.getLoginTime()));
        }
        if (loginUser.getExpireTime() != null) {
            vo.setExpireTime(new Date(loginUser.getExpireTime()));
        }

        // 浏览器和操作系统信息（可以通过User-Agent解析，这里先设置默认值）
        vo.setBrowser("Unknown");
        vo.setOs("Unknown");

        return vo;
    }
}
