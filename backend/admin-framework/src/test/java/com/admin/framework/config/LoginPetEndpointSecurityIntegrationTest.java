package com.admin.framework.config;

import com.admin.common.config.JwtProperties;
import com.admin.common.utils.RedisUtil;
import com.admin.framework.security.JwtAuthenticationFilter;
import com.admin.framework.web.GlobalExceptionHandler;
import com.admin.system.controller.SysConfigController;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.ISysConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = LoginPetEndpointSecurityIntegrationTest.TestConfig.class)
class LoginPetEndpointSecurityIntegrationTest {

    private static final String PUBLIC_LOGIN_PET_PATH = "/system/config/public/login-pet";
    private static final String UPDATE_LOGIN_PET_PATH = "/system/config/login-pet";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ISysConfigService configService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(configService);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void anonymousGet_returnsLoginPetAsJsonData() throws Exception {
        when(configService.selectLoginPetType()).thenReturn("owl");

        mockMvc.perform(get(PUBLIC_LOGIN_PET_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("owl"));

        verify(configService).selectLoginPetType();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "fox"})
    void invalidJsonType_returnsBusinessCode400WithoutUpdating(String type) throws Exception {
        mockMvc.perform(put(UPDATE_LOGIN_PET_PATH)
                        .with(authentication(loginUserAuthentication(Set.of("system:config:edit"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"" + type + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(configService, never()).updateLoginPetType(anyString());
    }

    @Test
    void anonymousPut_isRejectedWithoutUpdating() throws Exception {
        mockMvc.perform(put(UPDATE_LOGIN_PET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"dog\"}"))
                .andExpect(status().isForbidden());

        verify(configService, never()).updateLoginPetType(anyString());
    }

    @Test
    void authenticatedUserWithoutEditPermission_isRejected() throws Exception {
        mockMvc.perform(put(UPDATE_LOGIN_PET_PATH)
                        .with(authentication(loginUserAuthentication(Set.of("system:config:query"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"dog\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verify(configService, never()).updateLoginPetType(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"system:config:edit", "*:*:*"})
    void authenticatedUserWithAllowedPermission_updatesLoginPet(String permission) throws Exception {
        mockMvc.perform(put(UPDATE_LOGIN_PET_PATH)
                        .with(authentication(loginUserAuthentication(Set.of(permission))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"dog\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(configService).updateLoginPetType("dog");
    }

    @Test
    void anonymousGetOnAdjacentPath_isRejected() throws Exception {
        mockMvc.perform(get(PUBLIC_LOGIN_PET_PATH + "/adjacent"))
                .andExpect(status().isForbidden());

        verify(configService, never()).selectLoginPetType();
    }

    @Test
    void anonymousPostToPublicGetPath_isRejected() throws Exception {
        mockMvc.perform(post(PUBLIC_LOGIN_PET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        verify(configService, never()).selectLoginPetType();
    }

    private Authentication loginUserAuthentication(Set<String> permissions) {
        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setUsername("mvc-test-user");
        user.setStatus("0");

        LoginUser loginUser = new LoginUser(user, permissions);
        return UsernamePasswordAuthenticationToken.authenticated(
                loginUser, null, loginUser.getAuthorities());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    @Import({SecurityConfig.class, PermissionService.class, GlobalExceptionHandler.class})
    static class TestConfig {

        @Bean
        ISysConfigService configService() {
            return mock(ISysConfigService.class);
        }

        @Bean
        JwtProperties jwtProperties() {
            return mock(JwtProperties.class);
        }

        @Bean
        RedisUtil redisUtil() {
            return mock(RedisUtil.class);
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(JwtProperties jwtProperties, RedisUtil redisUtil) {
            return new JwtAuthenticationFilter(jwtProperties, redisUtil);
        }

        @Bean
        SysConfigController sysConfigController(ISysConfigService configService) {
            return new SysConfigController(configService);
        }
    }
}
