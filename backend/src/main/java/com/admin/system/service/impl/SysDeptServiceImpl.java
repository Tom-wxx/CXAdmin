package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysDept;
import com.admin.system.mapper.SysDeptMapper;
import com.admin.system.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.admin.system.common.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 业务层实现
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    private final SysDeptMapper deptMapper;

    /**
     * 查询部门管理数据
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 构建前端所需要的树形结构
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());

        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }

        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId() != null && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 根据部门ID查询信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        Long deptId = dept.getDeptId() == null ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (info != null && !info.getDeptId().equals(deptId)) {
            return false;
        }
        return true;
    }

    /**
     * 新增部门信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDept(SysDept dept) {
        // 校验部门名称是否唯一
        if (!checkDeptNameUnique(dept)) {
            throw new ServiceException("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }

        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!SystemConstants.STATUS_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }

        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        deptMapper.insert(dept);
    }

    /**
     * 修改部门信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(SysDept dept) {
        if (dept.getDeptId() == null) {
            throw new ServiceException("部门ID不能为空");
        }

        // 校验部门名称是否唯一
        if (!checkDeptNameUnique(dept)) {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }

        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (newParentDept != null && oldDept != null) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }

        int result = deptMapper.updateById(dept);
        if (result > 0) {
            if (!SystemConstants.STATUS_NORMAL.equals(dept.getStatus())) {
                // 如果该部门是停用状态，则将其所有正常状态的子部门状态修改为停用
                updateDeptStatusNormal(dept);
            }
        }
    }

    /**
     * 修改该部门的父级部门状态
     */
    private void updateDeptStatusNormal(SysDept dept) {
        // 查询该部门下的所有正常子部门
        int count = deptMapper.selectNormalChildrenDeptById(dept.getDeptId());
        if (count > 0) {
            // 如果存在正常的子部门，更新其状态为停用
            List<SysDept> children = deptMapper.selectChildrenDeptById(dept.getDeptId());
            for (SysDept child : children) {
                if (SystemConstants.STATUS_NORMAL.equals(child.getStatus())) {
                    child.setStatus(SystemConstants.STATUS_DISABLE);
                    deptMapper.updateById(child);
                }
            }
        }
    }

    /**
     * 修改子元素关系
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeptById(Long deptId) {
        if (deptId == null) {
            throw new ServiceException("部门ID不能为空");
        }

        // 判断是否存在子部门
        if (hasChildByDeptId(deptId)) {
            throw new ServiceException("存在下级部门，不允许删除");
        }

        // 判断部门是否存在用户
        if (checkDeptExistUser(deptId)) {
            throw new ServiceException("部门存在用户，不允许删除");
        }

        deptMapper.deleteById(deptId);
    }

}
