package com.dddframework.web.auth.config;

import com.dddframework.web.auth.interceptor.ApiWebInterceptor;
import com.dddframework.web.auth.interceptor.BaseAuthWebInterceptor;
import com.dddframework.web.interceptor.BaseWebInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基础认证配置
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Configuration
public class BaseAuthConfig {

    @Bean
    @ConditionalOnMissingBean
    public BaseWebInterceptor apiInterceptor() {
        return new ApiWebInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public BaseWebInterceptor baseAuthInterceptor() {
        return new BaseAuthWebInterceptor();
    }
}