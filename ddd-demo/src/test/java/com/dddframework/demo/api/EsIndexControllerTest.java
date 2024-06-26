package com.dddframework.demo.api;

import com.alibaba.fastjson.JSON;
import com.dddframework.demo.domain.contract.command.EsIndexCommand;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class EsIndexControllerTest {

    @Test
    void createIndex() {
        String index = "test_index123456";

        //文档属性Mapping集
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");

        Map<String, Object> value = new HashMap<>();
        value.put("type", "keyword");

        Map<String, Object> properties = new HashMap<>();
        properties.put("message", message);
        properties.put("value", value);

        EsIndexCommand esic = new EsIndexCommand(index, properties);
        System.out.println(JSON.toJSONString(esic));
    }
}