package com.admin.system.service.impl;

import com.admin.system.common.PageResult;
import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysDept;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.IOnlineUserService;
import com.admin.system.service.ISysDeptService;
import com.admin.system.utils.RedisUtil;
import com.admin.system.vo.OnlineUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线用户 业务层处理
 *
 * @author Admin
 */
@Service
public class OnlineUserServiceImpl implements IOnlineUserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询在线用户列表
     */
    @Override
    public List<OnlineUserVO> selectOnlineUserList(String username, String ipaddr) {
        // 获取所有在线用户的key
        Set<String> keys = redisTemplate.keys("login_tokens:*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }

        List<OnlineUserVO> onlineUserList = new ArrayList<>();
        for (String key : keys) {
            Object obj = redisUtil.get(key);
            if (obj instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) obj;
                OnlineUserVO onlineUser = convertToVO(loginUser, key);

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
        }

        // 按登录时间降序排序
        return onlineUserList.stream()
                .sorted((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 查询在线用户列表（分页）
     */
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

        String key = "login_tokens:" + tokenId;
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
            keys.add("login_tokens:" + tokenId);
        }

        redisUtil.delete(keys);
    }

    /**
     * 转换为VO对象
     */
    private OnlineUserVO convertToVO(LoginUser loginUser, String key) {
        OnlineUserVO vo = new OnlineUserVO();

        // 提取token（去掉前缀 "login_tokens:"）
        String token = key.substring("login_tokens:".length());
        vo.setTokenId(token);

        // 用户信息
        SysUser user = loginUser.getUser();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());

        // 部门信息
        if (user.getDeptId() != null) {
            SysDept dept = deptService.getById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
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
