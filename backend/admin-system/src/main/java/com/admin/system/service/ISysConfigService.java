package com.admin.system.service;

import com.admin.system.entity.SysConfig;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 参数配置 业务层
 *
 * @author Admin
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 登录页宠物类型配置键，允许值：cat、dog、owl
     */
    String LOGIN_PET_CONFIG_KEY = "sys.login.pet.type";

    /**
     * 分页查询参数配置列表
     */
    Page<SysConfig> selectConfigPage(Page<SysConfig> page, String configName, String configKey, String configType);

    /**
     * 根据参数键名查询参数值
     */
    String selectConfigByKey(String configKey);

    /**
     * 查询登录页宠物类型，配置缺失或非法时回退为 cat
     */
    String selectLoginPetType();

    /**
     * 更新登录页宠物类型，允许值：cat、dog、owl，非法值或更新失败时抛出业务异常
     */
    void updateLoginPetType(String type);

    /**
     * 新增参数配置
     */
    void insertConfig(SysConfig config);

    /**
     * 修改参数配置
     */
    void updateConfig(SysConfig config);

    /**
     * 删除参数配置
     */
    void deleteConfigById(Long configId);

    /**
     * 批量删除参数配置
     */
    void deleteConfigByIds(Long[] configIds);

    /**
     * 校验参数键名是否唯一
     */
    boolean checkConfigKeyUnique(String configKey, Long configId);
}
