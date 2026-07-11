package com.admin.system.service.impl;

import com.admin.common.exception.ServiceException;
import com.admin.system.entity.SysConfig;
import com.admin.system.enums.LoginPetType;
import com.admin.system.mapper.SysConfigMapper;
import com.admin.system.service.ISysConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.admin.common.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 参数配置 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private final SysConfigMapper configMapper;

    /**
     * 分页查询参数配置列表
     */
    @Override
    public Page<SysConfig> selectConfigPage(Page<SysConfig> page, String configName, String configKey, String configType) {
        return configMapper.selectConfigPage(page, configName, configKey, configType);
    }

    /**
     * 根据参数键名查询参数值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig config = configMapper.selectConfigByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String selectLoginPetType() {
        return LoginPetType.normalize(selectConfigByKey(LOGIN_PET_CONFIG_KEY));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginPetType(String type) {
        if (!LoginPetType.isValid(type)) {
            throw new ServiceException("不支持的登录页宠物类型");
        }

        SysConfig config = configMapper.selectConfigByKey(LOGIN_PET_CONFIG_KEY);
        if (config == null) {
            throw new ServiceException("登录页宠物配置不存在");
        }

        config.setConfigValue(type);
        if (configMapper.updateById(config) != 1) {
            throw new ServiceException("登录页宠物配置更新失败");
        }
    }

    /**
     * 新增参数配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertConfig(SysConfig config) {
        // 校验参数键名唯一性
        if (!checkConfigKeyUnique(config.getConfigKey(), null)) {
            throw new ServiceException("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        configMapper.insert(config);
    }

    /**
     * 修改参数配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysConfig config) {
        if (config.getConfigId() == null) {
            throw new ServiceException("参数ID不能为空");
        }

        // 校验参数键名唯一性
        if (!checkConfigKeyUnique(config.getConfigKey(), config.getConfigId())) {
            throw new ServiceException("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }

        configMapper.updateById(config);
    }

    /**
     * 删除参数配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigById(Long configId) {
        if (configId == null) {
            throw new ServiceException("参数ID不能为空");
        }

        SysConfig config = configMapper.selectById(configId);
        if (config == null) {
            throw new ServiceException("参数配置不存在");
        }

        // 系统内置参数不能删除
        if (SystemConstants.CONFIG_TYPE_BUILTIN.equals(config.getConfigType())) {
            throw new ServiceException("系统内置参数不能删除");
        }

        configMapper.deleteById(configId);
    }

    /**
     * 批量删除参数配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigByIds(Long[] configIds) {
        if (configIds == null || configIds.length == 0) {
            throw new ServiceException("参数ID不能为空");
        }

        for (Long configId : configIds) {
            deleteConfigById(configId);
        }
    }

    /**
     * 校验参数键名是否唯一
     */
    @Override
    public boolean checkConfigKeyUnique(String configKey, Long configId) {
        Long cid = configId == null ? -1L : configId;
        SysConfig info = configMapper.checkConfigKeyUnique(configKey);
        if (info != null && !info.getConfigId().equals(cid)) {
            return false;
        }
        return true;
    }
}
