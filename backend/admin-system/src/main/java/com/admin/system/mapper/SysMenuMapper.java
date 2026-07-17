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

    List<SysMenu> selectMenuList(SysMenu menu);

    Set<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);

    SysMenu selectMenuById(@Param("menuId") Long menuId);

    int hasChildByMenuId(@Param("menuId") Long menuId);

    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);


    List<MenuVO> selectMenuVOList(@Param("menuName") String menuName, @Param("status") String status);

    MenuVO selectMenuVOById(@Param("menuId") Long menuId);

}
