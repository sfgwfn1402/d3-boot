package com.dddframework.mq.kafka.core;

import com.dddframework.core.context.SpringContext;
import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.kit.cache.GCacheKit;
import com.dddframework.kit.lang.StrKit;
import com.dddframework.mq.config.BaseMQProperties;
import com.dddframework.mq.kafka.annotation.MQListener;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础MQ消费者
 *
 * @author Jensen
 */
@Slf4j(topic = "### BASE-MQ : BaseMQConsumer ###")
public final class BaseMQConsumer implements BeanPostProcessor, ApplicationListener<ApplicationStartedEvent> {
    /**
     * 消费者容器
     */
    private static final Map<String, List<Consumer>> consumersMap = new ConcurrentHashMap<>();

    public BaseMQConsumer() {
        log.debug("Loading BaseMQConsumer");
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // 默认10小时内防重
        GCacheKit.buildAfterWrite("RECENT_MSGS", SpringContext.getBean(BaseMQProperties.class).getSkipHours() * 60 * 60);
    }

    /**
     * 由@KafkaListener获取topic列表进行监听
     *
     * @return topic 集合
     */
    public String[] topics() {
        String[] topics = consumersMap.keySet().toArray(new String[0]);
        log.info("Inject MQ Topics => {}", Arrays.toString(topics));
        return topics;
    }

    /**
     * 按应用名分组
     *
     * @return
     */
    public String groupId() {
        return SpringContext.getEnv().getProperty("spring.application.name");
    }

    /**
     * 消费消息
     *
     * @param topic 主题
     * @param value 待解析的Json
     */
    public static void consume(String key, String topic, String value) {
        MQMessage mqMessage = MQMessage.builder().key(StrKit.isBlank(key) ? UUID.randomUUID().toString() : key).topic(topic).value(value).build();
        List<Consumer> consumers = consumersMap.get(topic);
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (Consumer consumer : consumers) {
            // 重复消息则跳过
            if (whenRepeat(mqMessage.getKey(), value, consumer)) {
                continue;
            }
            consume(consumer, mqMessage);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有被@MQConsumer注解的方法
        Method[] methods = ReflectionUtils.getDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            MQListener mqListener = AnnotationUtils.findAnnotation(method, MQListener.class);
            if (mqListener == null) {
                continue;
            }
            // 放入Topic消费者容器
            final Consumer consumer = Consumer.builder().bean(bean).method(method).mqListener(mqListener).build();
            java.util.function.Consumer<MQMessage> mqMessageConsumer = (MQMessage) -> {
                // 先持久化
                if (mqListener.persist()) {
                    try {
                        BaseMessagePersistence messagePersistence = mqListener.messagePersistenceProcess().newInstance();
                        if (method.getParameters().length == 0) {
                            // 方法无参，不传参调用
                            messagePersistence.persist(mqListener.topic(), null);
                        } else if (MQMessage.getValue() != null && MQMessage.getValue().length() != 0) {
                            // 根据第一个参数类型解析Json
                            Class<? extends StringSerializable> serialization = mqListener.deserializer();
                            Object object = serialization.newInstance().deserialize(MQMessage.getValue(), method.getParameters()[0].getType());
                            messagePersistence.persist(mqListener.topic(), object);
                        }
                    } catch (Exception e) {
                        log.error("保存持久化消息异常", e);
                    }
                }
                // 再执行业务
                consume(MQMessage.getValue(), consumer);
            };
            consumer.setMqMessageConsumer(mqMessageConsumer);
            BaseMQConsumer.put(mqListener.topic(), consumer);
        }
        return bean;
    }

    public static void consume(String json, Consumer consumer) {
        Object proxyBean = SpringContext.getBean(consumer.getBean().getClass());
        final Method method = consumer.getMethod();
        final MQListener mqListener = consumer.getMqListener();
        // 实际执行的地方
        try {
            if (method.getParameters().length == 0) {
                // 方法无参，不传参调用
                ReflectionUtils.invokeMethod(method, proxyBean);
            } else if (json != null && json.length() != 0) {
                // 根据第一个参数类型解析Json
                Class<? extends StringSerializable> serialization = mqListener.deserializer();
                Object arg = serialization.newInstance().deserialize(json, method.getParameters()[0].getType());
                if (arg != null) {
                    ReflectionUtils.invokeMethod(method, proxyBean, arg);
                }
            }
        } catch (Exception e) {
            log.error("Consume topic:{} failed: {}", mqListener.topic(), json, e);
            throw new ServiceException(e);
        }
    }

    public static void put(String topic, Consumer consumer) {
        List<Consumer> consumers = consumersMap.computeIfAbsent(topic, k -> new ArrayList<>());
        consumers.add(consumer);
    }

    private static boolean whenRepeat(String key, String value, Consumer consumer) {
        if (consumer.getMqListener().allowRepeat()) {
            return false;
        }
        String msgId = GCacheKit.get("RECENT_MSGS", key);
        if (msgId != null) {
            log.warn("Repeat Consume! topic:{} value:{}", consumer.getMqListener().topic(), value);
            return true;
        }
        GCacheKit.put("RECENT_MSGS", key, value);
        return false;
    }

    /**
     * 执行，如果执行异常需要释放Key
     *
     * @param consumer
     * @param mqMessage
     */
    private static void consume(Consumer consumer, MQMessage mqMessage) {
        // TODO 防止 threadLocal 污染
        try {
            consumer.getMqMessageConsumer().accept(mqMessage);
        } catch (Exception e) {
            log.error("exec fail, topic:{}, value:{} ", mqMessage.getTopic(), mqMessage.getValue(), e);
            // 异常释放UniKey
            evictUniKey(consumer.getMqListener().topic(), mqMessage.getKey(), mqMessage.getValue());
        }
    }

    /**
     * 失效Key
     *
     * @param topic
     * @param id
     * @param value
     */
    private static void evictUniKey(String topic, String id, String value) {
        // 自动失效
    }

    @Setter
    @Getter
    @Builder
    public static class Consumer {
        private java.util.function.Consumer<MQMessage> mqMessageConsumer;
        private MQListener mqListener;
        private Object bean;
        private Method method;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MQMessage {
        private String key;
        private String topic;
        private String value;
    }
}