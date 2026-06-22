package com.admin.system.service;

import com.admin.system.entity.SysPost;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 岗位管理 业务层
 *
 * @author Admin
 */
public interface ISysPostService extends IService<SysPost> {

    /**
     * 查询岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 分页查询岗位列表
     */
    Page<SysPost> selectPostPage(Page<SysPost> page, String postCode, String postName, String status);

    /**
     * 根据岗位ID查询岗位信息
     */
    SysPost selectPostById(Long postId);

    /**
     * 新增岗位
     */
    void insertPost(SysPost post);

    /**
     * 修改岗位
     */
    void updatePost(SysPost post);

    /**
     * 批量删除岗位信息
     */
    void deletePostByIds(Long[] postIds);

    /**
     * 删除岗位信息
     */
    void deletePostById(Long postId);

    /**
     * 校验岗位名称是否唯一
     */
    boolean checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码是否唯一
     */
    boolean checkPostCodeUnique(SysPost post);

}
