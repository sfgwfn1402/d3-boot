package com.dddframework.data.elasticsearch.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dddframework.data.elasticsearch.model.UserInfo;
import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import com.dddframework.data.elasticsearch.tools.ESLambdaWrapper;
import lombok.Data;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 示例
 */
@RestController
@RequestMapping("/demo")
public class CrudController {


    @Autowired
    private ESBaseRepositoryImpl searchHelper;
    private static final String INDEX = "test_index123456";


    /**
     * 判断索引是否存在
     */
    public void existsIndex() {
        String index = "test_index123456";
        boolean exists = searchHelper.existsIndex(index);
        System.out.println(String.format("判断索引是否存在, index:%s , 是否存在:%b", index, exists));
    }

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
        //创建索引
        ESLambdaWrapper esw = new ESLambdaWrapper();
        esw.index(index);
        boolean createIndexFlag = searchHelper.createIndex(esw, properties);
        System.out.println(String.format("创建索引, index:%s , createIndexFlag:%b", index, createIndexFlag));
    }

    public void save() {
        String userId = getUuId();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserName("lisi2");
        userInfo.setRealName("李四2");
        userInfo.setMobile("13725353777");
        userInfo.setAge(21);
        userInfo.setGrades(new BigDecimal("550.09"));
        userInfo.setCreateTime(new Date());
        IndexResponse response = searchHelper.save(INDEX, userId, userInfo);
        System.out.println(String.format("数据保存完毕, id:%s", response.getId()));

        UserInfo userInfo1 = (UserInfo) searchHelper.getById(INDEX, userId, UserInfo.class);
        System.out.println(String.format("查询结果:%s", response.getId()));
        System.out.println(JSON.toJSONString(userInfo1, SerializerFeature.PrettyFormat));
    }
    public String getUuId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取索引配置
     */
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

//    /**
//     * 检索示例
//     * @param queryBean
//     * @param filterBean
//     * @return
//     */
//    public SearchResult keyword(QueryBean queryBean, FilterBean filterBean){
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//
//        /*过滤*/
//        List<QueryBuilder> filterList = boolQueryBuilder.filter();
//        filterList.addAll(new ESFilterWrapper().filter(filterBean));
//
//        List<QueryBuilder> boolmustList = boolQueryBuilder.must();
//        /*查询*/
//        boolmustList.add(QueryBuilders.matchQuery("title",queryBean.getKeyword()));
//
//        searchSourceBuilder.query(boolQueryBuilder);
//
//        return requestResult(queryBean,searchSourceBuilder);
//    }


    /**
     * 单条更新示例
     *
     * @throws IOException
     */
    public void lambdaUpdateDocument() {
        ESBean bean = new ESBean();
        bean.setCode("1");
        bean.setName("2");
        ESLambdaWrapper<ESBean> lambdaWrapper = new ESLambdaWrapper();
        lambdaWrapper.docId("1").add(ESBean::getCode, "1").add(ESBean::getName, "2");
        searchHelper.lambdaUpdateDocument(lambdaWrapper);
    }


    /**
     * 单条创建示例
     */
    @RequestMapping("/create_document")
    public void lambdaCreateDocument() {
        ESBean bean = new ESBean();
        bean.setCode("1");
        bean.setName("2");

        ESLambdaWrapper<ESBean> lambdaWrapper = new ESLambdaWrapper();
        lambdaWrapper.docId("1").add(ESBean::getCode, "1").add(ESBean::getName, "2");
//        searchHelper.save(lambdaWrapper);
    }


    /**
     * 删除
     *
     * @throws IOException
     */
    public void delete() throws IOException {
        searchHelper.deleteDocument(new DeleteRequest().id("1"));
    }

    /**
     * 批量操作
     */
    public void bulkDocument() {
        List<DocWriteRequest<?>> requests = new ArrayList<>();
    }


//    private SearchResult requestResult(QueryBean queryBean, SearchSourceBuilder searchSourceBuilder) {
//        SearchRequest firstSearchRequest = new SearchRequest("indexName");
//        searchSourceBuilder.trackTotalHits(true); //设置返回总数
//        searchSourceBuilder.trackScores(true);
//        searchSourceBuilder.size(queryBean.getSize()).from((queryBean.getPage()-1)*queryBean.getSize());
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        /*返回字段*/
//        searchSourceBuilder.fetchSource(new String[]{"title"}, Strings.EMPTY_ARRAY);
//        /*排序*/
//        searchSourceBuilder.sort(new FieldSortBuilder(queryBean.getSortField()).order(queryBean.getSortOrder()));
//        /*学科聚合*/
//        searchSourceBuilder.aggregation(AggregationBuilders.terms("suject_count").field("suject_code").size(100000).order(BucketOrder.key(true)));
//        firstSearchRequest.source(searchSourceBuilder);
//        SearchResult searchResult = searchHelper.searchDocument(firstSearchRequest);
//        return searchResult;
//    }


    @Data
    public class ESBean {
        private String name;

        private String code;
    }


}
