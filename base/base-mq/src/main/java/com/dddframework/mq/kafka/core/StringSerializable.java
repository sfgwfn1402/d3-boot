package com.dddframework.mq.kafka.core;

public interface StringSerializable {

    <T> T deserialize(String src, Class<T> dist) throws Exception;

    String serialize(Object src) throws Exception;

}
