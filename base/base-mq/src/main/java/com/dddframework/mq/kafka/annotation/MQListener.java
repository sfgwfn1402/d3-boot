package com.dddframework.mq.kafka.annotation;

import com.dddframework.mq.kafka.core.BaseMessagePersistence;
import com.dddframework.mq.kafka.core.StringSerializable;
import com.dddframework.mq.kafka.core.serialization.BaseJsonStringSerialization;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * MQ监听器
 * 配合Topic执行器使用，通过TopicExecutor.exec执行一批被@MQListener注解的方法
 * 默认定义topic即可反序列化解析到第一个对象参数，无参不解析
 *
 * @author Jensen
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MQListener {
    // 本地主题
    @AliasFor("value")
    String topic() default "LOCAL";

    // 本地主题
    @AliasFor("topic")
    String value() default "LOCAL";

    // 分组ID
    String groupId() default "DEFAULT";

    // 是否允许重复，根据Topic+MD5(value)判断是否重复，默认：不允许重复
    boolean allowRepeat() default false;

    // 反序列化器
    Class<? extends StringSerializable> deserializer() default BaseJsonStringSerialization.class;

    // 是否持久化消息
    boolean persist() default false;

    // 持久化处理器
    Class<? extends BaseMessagePersistence> messagePersistenceProcess() default BaseMessagePersistence.class;
}
