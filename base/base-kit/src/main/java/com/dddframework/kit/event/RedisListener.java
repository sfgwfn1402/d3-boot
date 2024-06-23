package com.dddframework.kit.event;

import com.dddframework.core.context.BaseContext;
import com.dddframework.core.contract.constant.ContextConstants;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class RedisListener<T> implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisTemplate redisTemplate = BaseContext.get(ContextConstants.REDIS_TEMPLATE);
        onMessage((T) redisTemplate.getDefaultSerializer().deserialize(message.getBody()));
    }

    public abstract String topic();

    protected abstract void onMessage(T event);


}
