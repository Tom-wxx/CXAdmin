package com.admin.system.config;

import com.admin.system.common.BaseEntity;
import com.admin.system.security.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatis Plus配置
 *
 * @author Admin
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 自动填充
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                String username = SecurityUtils.getUsername();
                this.strictInsertFill(metaObject, "createBy", String.class, username);
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "updateBy", String.class, username);
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                String username = SecurityUtils.getUsername();
                this.strictUpdateFill(metaObject, "updateBy", String.class, username);
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
            }
        };
    }

}
