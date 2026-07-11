package com.admin.system.service;

import com.admin.common.exception.ServiceException;
import com.admin.system.entity.SysConfig;
import com.admin.system.mapper.SysConfigMapper;
import com.admin.system.service.impl.SysConfigServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("登录页宠物配置服务测试")
@ExtendWith(MockitoExtension.class)
class SysConfigServiceImplTest {

    private static final String CONFIG_KEY = "sys.login.pet.type";

    @InjectMocks
    private SysConfigServiceImpl configService;

    @Mock
    private SysConfigMapper configMapper;

    @Test
    void selectLoginPetType_missingConfig_returnsCat() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(null);
        assertEquals("cat", configService.selectLoginPetType());
    }

    @Test
    void selectLoginPetType_invalidValue_returnsCat() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, "fox"));
        assertEquals("cat", configService.selectLoginPetType());
    }

    @Test
    void selectLoginPetType_validValues_arePreserved() {
        for (String type : new String[]{"cat", "dog", "owl"}) {
            when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, type));
            assertEquals(type, configService.selectLoginPetType());
        }
    }

    @Test
    void updateLoginPetType_validValue_updatesExistingConfig() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, "cat"));
        when(configMapper.updateById(any(SysConfig.class))).thenReturn(1);

        configService.updateLoginPetType("owl");

        verify(configMapper).updateById(argThat((SysConfig config) ->
                config.getConfigId().equals(4L) && "owl".equals(config.getConfigValue())));
    }

    @Test
    void updateLoginPetType_invalidValue_rejectsWithoutWrite() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> configService.updateLoginPetType("fox"));
        assertEquals("不支持的登录页宠物类型", exception.getMessage());
        verify(configMapper, never()).updateById(any(SysConfig.class));
    }

    @Test
    void updateLoginPetType_missingConfig_reportsClearError() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class,
                () -> configService.updateLoginPetType("dog"));
        assertEquals("登录页宠物配置不存在", exception.getMessage());
    }

    @Test
    void updateLoginPetType_existingConfigButUpdateReturnsZero_shouldThrowClearError() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, "cat"));
        when(configMapper.updateById(any(SysConfig.class))).thenReturn(0);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> configService.updateLoginPetType("dog"));

        assertEquals("登录页宠物配置更新失败", exception.getMessage());
    }

    private SysConfig config(Long id, String value) {
        SysConfig config = new SysConfig();
        config.setConfigId(id);
        config.setConfigKey(CONFIG_KEY);
        config.setConfigValue(value);
        config.setConfigType("Y");
        return config;
    }
}
