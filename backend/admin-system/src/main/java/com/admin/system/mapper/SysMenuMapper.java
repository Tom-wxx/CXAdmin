package com.admin.system.mapper;

import com.admin.system.entity.SysMenu;
import com.admin.system.vo.MenuVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询系统菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户ID查询权限
     */
    Set<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单树信息
     */
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID查询信息
     */
    SysMenu selectMenuById(@Param("menuId") Long menuId);

    /**
     * 是否存在菜单子节点
     */
    int hasChildByMenuId(@Param("menuId") Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    // ==================== 新增 MenuVO 方法 ====================

    /**
     * 查询菜单列表（返回VO）
     */
    List<MenuVO> selectMenuVOList(@Param("menuName") String menuName, @Param("status") String status);

    /**
     * 根据菜单ID查询菜单详情（返回VO）
     */
    MenuVO selectMenuVOById(@Param("menuId") Long menuId);

}
