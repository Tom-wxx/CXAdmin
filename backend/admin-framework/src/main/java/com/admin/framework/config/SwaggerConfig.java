package com.admin.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (springdoc) 配置
 *
 * <p>springdoc-openapi 采用零配置自动扫描，这里仅声明文档元信息与全局鉴权头。
 * Swagger UI 默认路径 /swagger-ui.html，OpenAPI 描述 /v3/api-docs（叠加 context-path /api）。
 *
 * @author Admin
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("后台管理系统 API")
                        .description("后台管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("Admin").email("admin@example.com")))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(SECURITY_SCHEME)))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }
}
