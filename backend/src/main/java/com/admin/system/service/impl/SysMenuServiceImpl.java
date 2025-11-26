package com.admin.system.service.impl;

import com.admin.system.entity.SysMenu;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.service.ISysMenuService;
import com.admin.system.vo.MetaVo;
import com.admin.system.vo.RouterVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 *
 * @author Admin
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        return menuMapper.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = null;
        if (userId != null && userId == 1L) {
            menus = menuMapper.selectMenuList(new SysMenu());
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0L);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), "1".equals(menu.getIsCache())));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && "M".equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            }
            routers.add(router);
        }
        return routers;
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    @Override
    public List<SysMenu> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees;
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return menuMapper.selectMenuList(menu);
    }

    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateById(menu);
    }

    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (info != null && !info.getMenuId().equals(menuId)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 获取路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = menu.getMenuName();
        return routerName;
    }

    /**
     * 获取路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        if (menu.getParentId() != 0L && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (0L == menu.getParentId() && "M".equals(menu.getMenuType()) && "1".equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = "Layout";
        if (menu.getComponent() != null && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (menu.getComponent() == null && menu.getParentId() != 0L && isInnerLink(menu)) {
            component = "InnerLink";
        } else if (menu.getComponent() == null && isParentView(menu)) {
            component = "ParentView";
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId() == 0L && "C".equals(menu.getMenuType()) && menu.getIsFrame().equals(1);
    }

    /**
     * 是否为内链组件
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().equals(0) && ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId() != 0L && "M".equals(menu.getMenuType());
    }

    public boolean ishttp(String link) {
        return link.startsWith("http://") || link.startsWith("https://");
    }

    public String innerLinkReplaceEach(String path) {
        return path.replaceAll("/", "");
    }

    /**
     * 根据父节点的ID获取所有子节点
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, Long parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu t : list) {
            if (t.getParentId().equals(parentId)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    // ==================== 新增 DTO/VO 模式方法实现 ====================

    @Override
    public List<com.admin.system.vo.MenuVO> selectMenuTreeList(String menuName, String status) {
        List<com.admin.system.vo.MenuVO> menus = menuMapper.selectMenuVOList(menuName, status);
        return buildMenuVOTree(menus);
    }

    @Override
    public com.admin.system.vo.MenuVO selectMenuById(Long menuId) {
        return menuMapper.selectMenuVOById(menuId);
    }

    @Override
    public void insertMenu(com.admin.system.dto.MenuDTO menuDTO) {
        // 校验菜单名称唯一性
        if (!checkMenuNameUnique(menuDTO.getMenuName(), null, menuDTO.getParentId())) {
            throw new com.admin.system.common.exception.ServiceException("新增菜单'" + menuDTO.getMenuName() + "'失败，菜单名称已存在");
        }

        // DTO 转 Entity
        SysMenu menu = new SysMenu();
        org.springframework.beans.BeanUtils.copyProperties(menuDTO, menu);

        // 保存菜单
        menuMapper.insert(menu);
    }

    @Override
    public void updateMenu(com.admin.system.dto.MenuDTO menuDTO) {
        if (menuDTO.getMenuId() == null) {
            throw new com.admin.system.common.exception.ServiceException("菜单ID不能为空");
        }

        // 校验菜单名称唯一性
        if (!checkMenuNameUnique(menuDTO.getMenuName(), menuDTO.getMenuId(), menuDTO.getParentId())) {
            throw new com.admin.system.common.exception.ServiceException("修改菜单'" + menuDTO.getMenuName() + "'失败，菜单名称已存在");
        }

        // DTO 转 Entity
        SysMenu menu = new SysMenu();
        org.springframework.beans.BeanUtils.copyProperties(menuDTO, menu);

        // 更新菜单
        menuMapper.updateById(menu);
    }

    @Override
    public void deleteMenuById(Long menuId, boolean force) {
        if (menuId == null) {
            throw new com.admin.system.common.exception.ServiceException("菜单ID不能为空");
        }

        // 如果不是强制删除，检查是否有子菜单
        if (!force && hasChildByMenuId(menuId)) {
            throw new com.admin.system.common.exception.ServiceException("存在子菜单，不允许删除");
        }

        // 删除菜单
        menuMapper.deleteById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(String menuName, Long menuId, Long parentId) {
        Long mid = menuId == null ? -1L : menuId;
        SysMenu info = menuMapper.checkMenuNameUnique(menuName, parentId);
        if (info != null && !info.getMenuId().equals(mid)) {
            return false;
        }
        return true;
    }

    /**
     * 构建MenuVO树形结构
     */
    private List<com.admin.system.vo.MenuVO> buildMenuVOTree(List<com.admin.system.vo.MenuVO> menus) {
        List<com.admin.system.vo.MenuVO> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(com.admin.system.vo.MenuVO::getMenuId).collect(Collectors.toList());
        for (com.admin.system.vo.MenuVO menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (menu.getParentId() == null || !tempList.contains(menu.getParentId())) {
                recursionFnVO(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 递归列表（MenuVO版本）
     */
    private void recursionFnVO(List<com.admin.system.vo.MenuVO> list, com.admin.system.vo.MenuVO t) {
        // 得到子节点列表
        List<com.admin.system.vo.MenuVO> childList = getChildListVO(list, t);
        t.setChildren(childList);
        for (com.admin.system.vo.MenuVO tChild : childList) {
            if (hasChildVO(list, tChild)) {
                recursionFnVO(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表（MenuVO版本）
     */
    private List<com.admin.system.vo.MenuVO> getChildListVO(List<com.admin.system.vo.MenuVO> list, com.admin.system.vo.MenuVO t) {
        List<com.admin.system.vo.MenuVO> tlist = new ArrayList<>();
        for (com.admin.system.vo.MenuVO n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点（MenuVO版本）
     */
    private boolean hasChildVO(List<com.admin.system.vo.MenuVO> list, com.admin.system.vo.MenuVO t) {
        return getChildListVO(list, t).size() > 0;
    }

}
