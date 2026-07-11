package com.admin.system.controller;

import com.admin.common.Result;
import com.admin.system.dto.LoginPetTypeDTO;
import com.admin.system.service.ISysConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysConfigControllerTest {

    @InjectMocks
    private SysConfigController controller;

    @Mock
    private ISysConfigService configService;

    @Test
    void getLoginPet_returnsServiceValue() {
        when(configService.selectLoginPetType()).thenReturn("owl");
        Result<String> result = controller.getLoginPet();
        assertEquals("owl", result.getData());
    }

    @Test
    void updateLoginPet_delegatesValidatedType() {
        LoginPetTypeDTO body = new LoginPetTypeDTO();
        body.setType("dog");
        Result<Void> result = controller.updateLoginPet(body);
        assertEquals(200, result.getCode());
        verify(configService).updateLoginPetType("dog");
    }

    @Test
    void updateLoginPet_requiresConfigEditPermission() throws Exception {
        Method method = SysConfigController.class.getMethod("updateLoginPet", LoginPetTypeDTO.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
        assertNotNull(annotation);
        assertEquals("@ss.hasPermi('system:config:edit')", annotation.value());
    }
}
