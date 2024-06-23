package com.dddframework.kit.event;

import com.dddframework.core.context.BaseContext;
import com.dddframework.core.contract.constant.ContextConstants;
import lombok.experimental.UtilityClass;
import org.springframework.data.redis.core.RedisTemplate;

@UtilityClass
public class RedisPublisher {

    public <T> void publish(String topic, T body) {
        RedisTemplate redisTemplate = BaseContext.get(ContextConstants.REDIS_TEMPLATE);
        redisTemplate.convertAndSend(topic, body);
    }

}
