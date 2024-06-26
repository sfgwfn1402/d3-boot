package com.dddframework.data.elasticsearch.demo;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import com.dddframework.data.elasticsearch.tools.ESLambdaWrapper;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例
 */
@RestController
@RequestMapping("/demo")
public class IndexController {


    @Autowired
    private ESBaseRepositoryImpl searchHelper;

    /**
     * 判断索引是否存在
     */
    @RequestMapping("/exists_index")
    public void existsIndex() {
        String index = "test_index123456";
        boolean exists = searchHelper.existsIndex(index);
        System.out.println(String.format("判断索引是否存在, index:%s , 是否存在:%b", index, exists));
    }

    /**
     * 创建索引
     */
    @RequestMapping("/create_index")
    public void createIndex() {
        String index = "test_index123456";
        //文档属性Mapping集
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");

        Map<String, Object> value = new HashMap<>();
        value.put("type", "keyword");

        Map<String, Object> properties = new HashMap<>();
        properties.put("message", message);
        properties.put("value", value);
        ESLambdaWrapper esw = new ESLambdaWrapper();
        esw.index(index);
        boolean createIndexFlag = searchHelper.createIndex(esw, properties);
        System.out.println(String.format("创建索引, index:%s , createIndexFlag:%b", index, createIndexFlag));
    }

    /**
     * 获取索引配置
     */
    @RequestMapping("/index_setting")
    public void getIndexSetting() {
        String index = "test_index2";
        GetSettingsResponse response = searchHelper.getIndexSetting(index);
        System.out.println(String.format("获取索引配置, index:%s", index));
        System.out.println(response);
    }


    /**
     * 删除索引
     */
    public void deleteIndex() {
        String index = "test_index2";
        boolean deleteIndexFlag = searchHelper.deleteIndex(index);
        System.out.println(String.format("删除索引, index:%s , deleteIndexFlag:%b", index, deleteIndexFlag));
    }

}
