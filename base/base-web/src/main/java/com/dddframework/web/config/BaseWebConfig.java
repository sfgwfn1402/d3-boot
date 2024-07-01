package com.dddframework.web.config;

import com.dddframework.core.context.SpringContext;
import com.dddframework.core.utils.JsonKit;
import com.dddframework.web.core.*;
import com.dddframework.web.interceptor.BaseWebInterceptor;
import com.dddframework.web.interceptor.FeignHeaderInterceptor;
import com.dddframework.web.utils.LocalDateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@Slf4j(topic = "### BASE-WEB : BaseWebConfig ###")
@EnableConfigurationProperties(BaseWebProperties.class)
@AllArgsConstructor
public class BaseWebConfig implements WebMvcConfigurer {
    final List<BaseWebInterceptor> baseWebInterceptors;
    final BaseWebProperties baseWebProperties;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(100000L); // 设置默认超时时间为5秒
    }

    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        log.debug("Loading localeResolver");
        return new BaseAcceptHeaderLocaleResolver();
    }

    @Bean
    @ConditionalOnClass(Feign.class)
    public RequestInterceptor feignHeaderInterceptor() {
        log.debug("Loading feignHeaderInterceptor");
        return new FeignHeaderInterceptor();
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
        registry.addFormatter(new LocalDateTimeFormatter());
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper mvcObjectMapper() {
        log.debug("Loading mvcObjectMapper");
        return JsonKit.buildObjectMapper(baseWebProperties.getMvc().getTimePattern());
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        log.debug("Loading jackson2HttpMessageConverter");
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setDefaultCharset(Charset.defaultCharset());
        converter.setObjectMapper(mvcObjectMapper());
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalRestExceptionAdvice globalRestExceptionAdvice() {
        log.debug("Loading globalRestExceptionAdvice");
        return new GlobalRestExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalRequestAdvice globalRequestAdvice() {
        log.debug("Loading globalRequestAdvice");
        return new GlobalRequestAdvice();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalResponseRAdvice globalResponseRAdvice() {
        log.debug("Loading globalResponseRAdvice");
        return new GlobalResponseRAdvice();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalFeignErrorAdvice globalFeignErrorAdvice() {
        log.debug("Loading globalFeignErrorAdvice");
        return new GlobalFeignErrorAdvice();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        if (baseWebInterceptors != null && !baseWebInterceptors.isEmpty()) {
            baseWebInterceptors.forEach(baseInterceptor -> {
                log.debug("Loading {}", baseInterceptor.getClass().getSimpleName());
                registry.addInterceptor(baseInterceptor).addPathPatterns(baseInterceptor.pathPatterns()).excludePathPatterns(baseInterceptor.excludePathPatterns());
            });
        } else {
            log.warn("baseWebInterceptors is empty!");
        }
    }
}
