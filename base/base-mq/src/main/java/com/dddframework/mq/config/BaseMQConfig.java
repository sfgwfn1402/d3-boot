package com.dddframework.mq.config;

import com.dddframework.mq.kafka.core.BaseMQConsumer;
import com.dddframework.mq.kafka.impl.BaseKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BaseMQProperties.class)
@Slf4j
public class BaseMQConfig {

    @Bean
    public BaseMQConsumer baseMQConsumer() {
        return new BaseMQConsumer();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.consumer.auto-startup", havingValue = "true")
    public BaseKafkaConsumer baseKafkaConsumer() {
        return new BaseKafkaConsumer();
    }

}
