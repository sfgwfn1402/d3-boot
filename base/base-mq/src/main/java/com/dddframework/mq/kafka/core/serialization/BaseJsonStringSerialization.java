package com.dddframework.mq.kafka.core.serialization;

import com.dddframework.core.utils.JsonKit;
import com.dddframework.kit.lang.StrKit;
import com.dddframework.mq.kafka.core.StringSerializable;

public class BaseJsonStringSerialization implements StringSerializable {

    @Override
    public <T> T deserialize(String src, Class<T> dist) throws Exception {
        if (StrKit.isEmpty(src)) {
            return null;
        }
        return JsonKit.toObject(src, dist);
    }

    @Override
    public String serialize(Object src) throws Exception {
        return JsonKit.toJson(src);
    }
}
