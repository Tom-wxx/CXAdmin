package com.admin.system.service;

import com.admin.system.common.PageResult;
import com.admin.system.vo.OnlineUserVO;

import java.util.List;

/**
 * 在线用户 业务层
 *
 * @author Admin
 */
public interface IOnlineUserService {

    /**
     * 查询在线用户列表
     */
    List<OnlineUserVO> selectOnlineUserList(String username, String ipaddr);

    /**
     * 查询在线用户列表（分页）
     */
    PageResult<OnlineUserVO> selectOnlineUserListPage(String username, String ipaddr, Integer current, Integer size);

    /**
     * 强制退出用户
     */
    void forceLogout(String tokenId);

    /**
     * 批量强制退出用户
     */
    void batchForceLogout(String[] tokenIds);
}
