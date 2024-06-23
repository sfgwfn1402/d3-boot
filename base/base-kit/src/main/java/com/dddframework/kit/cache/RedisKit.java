package com.dddframework.kit.cache;

import com.dddframework.core.context.BaseContext;
import com.dddframework.core.contract.constant.ContextConstants;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
//@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisKit {
//    @Autowired
//    RedisConnectionFactory redisConnectionFactory;
//    @Autowired
//    RedisMessageListenerContainer redisMessageListenerContainer;
//
//    @Bean
//    @ConditionalOnMissingBean(name = {"redisMessageListenerContainer"})
//    public RedisMessageListenerContainer redisMessageListenerContainer(List<RedisListener> redisListeners) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//        // 注册消息阅监听器
//        if (CollUtil.isNotEmpty(redisListeners)) {
//            redisListeners.forEach(redisListener -> container.addMessageListener(redisListener, new ChannelTopic(redisListener.topic())));
//        }
//        return container;
//    }

    public static RedisTemplate getTemplate() {
        return BaseContext.get(ContextConstants.REDIS_TEMPLATE);
    }

    public static void set(Object key, Object value, long timeout, TimeUnit unit) {
        getTemplate().opsForValue().set(key, value, timeout, unit);
    }

    public static Boolean setIfAbsent(Object key, Object value, long timeout, TimeUnit unit) {
        return getTemplate().opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    public static void set(Object key, Object value) {
        getTemplate().opsForValue().set(key, value);
    }

    public static Boolean setIfAbsent(Object key, Object value) {
        return getTemplate().opsForValue().setIfAbsent(key, value);
    }

    public static void set(Object key, Object value, Duration duration) {
        getTemplate().opsForValue().set(key, value, duration);
    }

    public static Long increment(Object key) {
        return getTemplate().opsForValue().increment(key);
    }

    public static <T> T get(Object key) {
        return (T) getTemplate().opsForValue().get(key);
    }

    public static Boolean expire(Object key, long timeout, TimeUnit unit) {
        return getTemplate().expire(key, timeout, unit);
    }
}