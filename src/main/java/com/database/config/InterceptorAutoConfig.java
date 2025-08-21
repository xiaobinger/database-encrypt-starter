package com.database.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.database.interceptor.MyBatisEncryptionInterceptor;
import com.database.typeHandler.FieldTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.context.annotation.Bean;

/**
 * @author xiongbing
 * @date 2025/8/15 13:40
 * @description 拦截器自动配置
 */
public class InterceptorAutoConfig {

    /**
     * @see com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration
     */
    @Bean
    ConfigurationCustomizer configurationCustomizer(MybatisPlusInterceptor mybatisPlusInterceptor) {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.VARCHAR, new FieldTypeHandler());
            configuration.addInterceptor(new MyBatisEncryptionInterceptor());
        };
    }
}
