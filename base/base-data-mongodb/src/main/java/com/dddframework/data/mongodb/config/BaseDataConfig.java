package com.dddframework.data.mongodb.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.dddframework.data.mongodb.plugin.InsertIgnorePlugin;
import com.dddframework.data.mongodb.plugin.PaginationPlugin;
import com.dddframework.data.mongodb.plugin.SqlMonitorPlugin;
import com.dddframework.data.mongodb.typehandlers.BaseTypeHandler;
import com.dddframework.data.mongodb.typehandlers.BigDecimalTypeHandler;
import lombok.AllArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BaseDataProperties.class)
@ConditionalOnBean(DataSource.class)
@AllArgsConstructor
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisConfiguration.class})
public class BaseDataConfig implements InitializingBean {
    final MybatisPlusProperties mybatisPlusProperties;
    final List<BaseTypeHandler> jsonStringTypeHandlers;

    /**
     * 分页统计插件
     */
    @Bean
    @ConditionalOnMissingBean
    public PaginationPlugin paginationPlugin() {
        return new PaginationPlugin();
    }

    /**
     * INSERT IGNORE 改写插件
     */
    @Bean
    @ConditionalOnMissingBean
    public InsertIgnorePlugin insertIgnorePlugin() {
        return new InsertIgnorePlugin();
    }

    /**
     * SQL监控插件
     */
    @Bean
    @ConditionalOnMissingBean
    public SqlMonitorPlugin sqlMonitorPlugin() {
        return new SqlMonitorPlugin();
    }

    @Override
    public void afterPropertiesSet() {
        MybatisConfiguration configuration = mybatisPlusProperties.getConfiguration();
        if (configuration == null) {
            configuration = new MybatisConfiguration();
            mybatisPlusProperties.setConfiguration(configuration);
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        typeHandlerRegistry.register(BigDecimal.class, new BigDecimalTypeHandler());
        typeHandlerRegistry.register(JdbcType.DECIMAL, new BigDecimalTypeHandler());
        // JavaObject、JavaArray
        if (jsonStringTypeHandlers != null && !jsonStringTypeHandlers.isEmpty()) {
            for (BaseTypeHandler baseTypeHandler : jsonStringTypeHandlers) {
                typeHandlerRegistry.register(baseTypeHandler.type(), baseTypeHandler);
            }
        }
    }

}
