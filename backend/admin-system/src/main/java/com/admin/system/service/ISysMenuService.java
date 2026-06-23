package com.admin.system.service;

import com.admin.system.dto.MenuDTO;
import com.admin.system.entity.SysMenu;
import com.admin.system.vo.MenuVO;
import com.admin.system.vo.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author Admin
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID查询权限
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单树信息
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     */
    List<SysMenu> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 是否存在菜单子节点
     */
    boolean hasChildByMenuId(Long menuId);

    // ==================== 新增 DTO/VO 模式方法 ====================

    /**
     * 查询菜单列表（树形结构）
     */
    List<MenuVO> selectMenuTreeList(String menuName, String status);

    /**
     * 根据菜单ID查询菜单详情
     */
    MenuVO selectMenuById(Long menuId);

    /**
     * 新增菜单
     */
    void insertMenu(MenuDTO menuDTO);

    /**
     * 修改菜单
     */
    void updateMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     */
    void deleteMenuById(Long menuId, boolean force);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(String menuName, Long menuId, Long parentId);

}
