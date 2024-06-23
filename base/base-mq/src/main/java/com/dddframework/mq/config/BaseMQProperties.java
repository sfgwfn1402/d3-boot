package com.dddframework.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MQ配置
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Data
@ConfigurationProperties(prefix = "base-mq")
public class BaseMQProperties {
    /**
     * N小时内重复消息则跳过
     */
    private Integer skipHours = 10;
}
