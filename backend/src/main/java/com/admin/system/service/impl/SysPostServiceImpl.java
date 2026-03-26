package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysPost;
import com.admin.system.mapper.SysPostMapper;
import com.admin.system.service.ISysPostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 岗位管理 业务层实现
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {

    private final SysPostMapper postMapper;

    /**
     * 查询岗位列表
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    /**
     * 分页查询岗位列表
     */
    @Override
    public Page<SysPost> selectPostPage(Page<SysPost> page, String postCode, String postName, String status) {
        return postMapper.selectPostPage(page, postCode, postName, status);
    }

    /**
     * 根据岗位ID查询岗位信息
     */
    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    /**
     * 新增岗位
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertPost(SysPost post) {
        // 校验岗位名称是否唯一
        if (!checkPostNameUnique(post)) {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }

        // 校验岗位编码是否唯一
        if (!checkPostCodeUnique(post)) {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }

        postMapper.insert(post);
    }

    /**
     * 修改岗位
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(SysPost post) {
        if (post.getPostId() == null) {
            throw new ServiceException("岗位ID不能为空");
        }

        // 校验岗位名称是否唯一
        if (!checkPostNameUnique(post)) {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }

        // 校验岗位编码是否唯一
        if (!checkPostCodeUnique(post)) {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }

        postMapper.updateById(post);
    }

    /**
     * 批量删除岗位信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePostByIds(Long[] postIds) {
        if (postIds == null || postIds.length == 0) {
            throw new ServiceException("岗位ID不能为空");
        }

        for (Long postId : postIds) {
            deletePostById(postId);
        }
    }

    /**
     * 删除岗位信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePostById(Long postId) {
        if (postId == null) {
            throw new ServiceException("岗位ID不能为空");
        }

        // 查询岗位是否存在用户
        int count = postMapper.checkPostExistUser(postId);
        if (count > 0) {
            throw new ServiceException("该岗位已分配用户，不能删除");
        }

        postMapper.deleteById(postId);
    }

    /**
     * 校验岗位名称是否唯一
     */
    @Override
    public boolean checkPostNameUnique(SysPost post) {
        Long postId = post.getPostId() == null ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (info != null && !info.getPostId().equals(postId)) {
            return false;
        }
        return true;
    }

    /**
     * 校验岗位编码是否唯一
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        Long postId = post.getPostId() == null ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (info != null && !info.getPostId().equals(postId)) {
            return false;
        }
        return true;
    }

}
