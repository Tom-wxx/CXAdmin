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

    Set<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

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

    List<SysMenu> selectMenuList(SysMenu menu);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);

    boolean hasChildByMenuId(Long menuId);


    /**
     * 查询菜单列表（树形结构）
     */
    List<MenuVO> selectMenuTreeList(String menuName, String status);

    MenuVO selectMenuById(Long menuId);

    void insertMenu(MenuDTO menuDTO);

    void updateMenu(MenuDTO menuDTO);

    void deleteMenuById(Long menuId, boolean force);

    boolean checkMenuNameUnique(String menuName, Long menuId, Long parentId);

}
