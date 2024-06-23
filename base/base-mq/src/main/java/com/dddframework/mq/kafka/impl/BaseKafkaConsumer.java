package com.dddframework.mq.kafka.impl;

import com.dddframework.mq.kafka.core.BaseMQConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 通用Kafka消费者
 *
 * @Author: Jensen
 * @Date: 2021/11/11 11:11
 */
@Slf4j(topic = "### BASE-MQ : BaseKafkaConsumer ###")
public class BaseKafkaConsumer implements ApplicationListener<ApplicationStartedEvent> {
    private BaseKafkaListener baseKafkaListener;

    public BaseKafkaConsumer() {
        log.debug("Loading BaseKafkaConsumer");
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (Objects.isNull(baseKafkaListener)) {
            // 在启动后创建baseKafkaListener，避免因Component加载顺序问题无法动态获取到所有Topics
            baseKafkaListener = event.getApplicationContext().getBean(BaseKafkaListener.class);
        }
    }

    @Component
    @Scope("prototype")
    @Slf4j(topic = "### BASE-MQ : BaseKafkaListener ###")
    public static class BaseKafkaListener {
        public BaseKafkaListener() {
            log.debug("Loading BaseKafkaListener");
        }

        @KafkaListener(topics = "#{baseMQConsumer.topics()}", groupId = "#{baseMQConsumer.groupId()}")
        public void consume(ConsumerRecord<String, String> consumerMessage) {
            log.debug("接收kafka消息 => key:{},topic:{},message:{}", consumerMessage.key(), consumerMessage.topic(), consumerMessage.value());
            BaseMQConsumer.consume(consumerMessage.key(), consumerMessage.topic(), consumerMessage.value());
        }
    }
}
