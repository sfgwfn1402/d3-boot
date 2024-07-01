package com.dddframework.demo.api.mongodb;

import com.alibaba.fastjson.JSON;
import com.dddframework.demo.domain.contract.command.MongoDocCityCommand;
import org.junit.jupiter.api.Test;

class MongoCollectionCityControllerTest {

    @Test
    void test() {
        MongoDocCityCommand command = new MongoDocCityCommand();
        command.setCityName("Beijing");
        command.setId(100001L);
        command.setProvinceId(100001L);
        command.setDescription("北京市");
        System.out.println(JSON.toJSONString(command));
    }
}