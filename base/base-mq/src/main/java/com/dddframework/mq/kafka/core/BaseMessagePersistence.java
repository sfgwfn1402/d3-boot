package com.dddframework.mq.kafka.core;

import javax.annotation.Nullable;

/**
 * @author xuzhuohao
 * @version 1.0.0
 * @date 2022-04-11
 */
public abstract class BaseMessagePersistence {

    /**
     * 消息持久化
     *
     * @param msg
     */
    public abstract <T> void persist(String topic, @Nullable T msg);
}
