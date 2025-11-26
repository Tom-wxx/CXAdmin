package com.admin.system.mapper;

import com.admin.system.entity.SysPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 查询岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 分页查询岗位列表
     */
    Page<SysPost> selectPostPage(Page<SysPost> page,
                                  @Param("postCode") String postCode,
                                  @Param("postName") String postName,
                                  @Param("status") String status);

    /**
     * 根据岗位ID查询岗位信息
     */
    SysPost selectPostById(@Param("postId") Long postId);

    /**
     * 查询岗位是否存在用户
     */
    int checkPostExistUser(@Param("postId") Long postId);

    /**
     * 校验岗位名称是否唯一
     */
    SysPost checkPostNameUnique(@Param("postName") String postName);

    /**
     * 校验岗位编码是否唯一
     */
    SysPost checkPostCodeUnique(@Param("postCode") String postCode);

}
