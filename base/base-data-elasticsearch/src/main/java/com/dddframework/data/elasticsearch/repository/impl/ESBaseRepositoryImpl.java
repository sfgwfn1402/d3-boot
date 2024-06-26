package com.dddframework.data.elasticsearch.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.core.elasticsearch.contract.Model;
import com.dddframework.core.elasticsearch.contract.Page;
import com.dddframework.core.elasticsearch.contract.Query;
import com.dddframework.core.elasticsearch.utils.MappingKit;
import com.dddframework.data.elasticsearch.model.ElasticSearchDocModel;
import com.dddframework.data.elasticsearch.tools.ESLambdaWrapper;
import com.dddframework.data.elasticsearch.tools.PageUtils;
import com.dddframework.data.elasticsearch.tools.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings("ALL")
@Slf4j(topic = "### ES-BASE-DATA : ESBaseRepository ###")
public class ESBaseRepositoryImpl<M extends Model, P, Q extends Query> implements BaseRepository<M, Q>, Serializable {

    @Autowired
    private RestHighLevelClient highLevelClient;

    /**
     * 默认主分片数
     */
    private static final int DEFAULT_SHARDS = 3;
    /**
     * 默认副本分片数
     */
    private static final int DEFAULT_REPLICAS = 2;

    public ESBaseRepositoryImpl() {
        final Class<M> modelClass = (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), 0);
        final Class<P> poClass = (Class<P>) ReflectionKit.getSuperClassGenericType(this.getClass(), 1);
        final Class<Q> queryClass = (Class<Q>) ReflectionKit.getSuperClassGenericType(this.getClass(), 2);
        BaseRepository.inject(modelClass, this.getClass());
        BaseRepository.inject(queryClass, this.getClass());
        MappingKit.map("MODEL_PO", modelClass, poClass);
        MappingKit.map("MODEL_PO", poClass, modelClass);
        MappingKit.map("MODEL_QUERY", modelClass, queryClass);
        MappingKit.map("MODEL_QUERY", queryClass, modelClass);
        // 首字母设为小写
        String modelClassName = modelClass.getSimpleName().toLowerCase().substring(0, 1) + modelClass.getSimpleName().substring(1);
        MappingKit.map("MODEL_NAME", modelClassName, modelClass);
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引
     * @return 返回 true，表示存在
     */
    public boolean existsIndex(String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            request.local(false);
            request.humanReadable(true);
            request.includeDefaults(false);

            return highLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> get index exists exception ,index:{} ", index, e);
            throw new RuntimeException("[ elasticsearch ] >> get index exists exception {}", e);
        }
    }

    /**
     * 创建 ES 索引
     *
     * @param model      model
     * @param properties 文档属性集合
     * @return 返回 true，表示创建成功
     */
    @Override
    public boolean createIndex(M model, Map<String, Object> properties) {
        String index = model.getIndex();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            // 注：ES 7.x 后的版本中，已经弃用 type
            builder.startObject()
                    .startObject("mappings")
                    .field("properties", properties)
                    .endObject()
                    .startObject("settings")
                    //分片数
                    .field("number_of_shards", DEFAULT_SHARDS)
                    //副本数
                    .field("number_of_replicas", DEFAULT_REPLICAS)
                    .endObject()
                    .endObject();
            CreateIndexRequest request = new CreateIndexRequest(index).source(builder);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            log.error("[ elasticsearch ] >> createIndex exception ,index:{},properties:{}", index, properties, e);
            throw new RuntimeException("[ elasticsearch ] >> createIndex exception ");
        }
    }

    /**
     * 获取索引配置
     *
     * @param index 索引
     * @return 返回索引配置内容
     */
    public GetSettingsResponse getIndexSetting(String index) {
        try {
            GetSettingsRequest request = new GetSettingsRequest().indices(index);
            return highLevelClient.indices().getSettings(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            //其它未知异常
            log.error("[ elasticsearch ] >> getIndexSetting exception ,index:{}", index, e);
            throw new RuntimeException("[ elasticsearch ] >>  getIndexSetting exception {}", e);
        }
    }

    /**
     * 删除索引
     *
     * @param index 索引
     * @return 返回 true，表示删除成功
     */
    public boolean deleteIndex(String index) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = highLevelClient.indices().delete(request, RequestOptions.DEFAULT);

            return response.isAcknowledged();
        } catch (ElasticsearchException e) {
            //索引不存在-无需删除
            if (e.status() == RestStatus.NOT_FOUND) {
                log.error("[ elasticsearch ] >>  deleteIndex >>  index:{}, Not found ", index, e);
                return false;
            }
            log.error("[ elasticsearch ] >> deleteIndex exception ,index:{}", index, e);
            throw new RuntimeException("elasticsearch deleteIndex exception ");
        } catch (IOException e) {
            //其它未知异常
            log.error("[ elasticsearch ] >> deleteIndex exception ,index:{}", index, e);
            throw new RuntimeException("[ elasticsearch ] >>  deleteIndex exception {}", e);
        }
    }

    /**
     * 判断文档是否存在
     *
     * @param index 索引
     * @return 返回 true，表示存在
     */
    public boolean existsDocument(String index, String id) {
        try {
            GetRequest request = new GetRequest(index, id);
            //禁用获取_source
            request.fetchSourceContext(new FetchSourceContext(false));
            //禁用获取存储的字段。
            request.storedFields("_none_");

            return highLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> get document exists exception ,index:{} ", index, e);
            throw new RuntimeException("[ elasticsearch ] >> get document  exists exception {}", e);
        }
    }

    /**
     * 保存数据-随机生成数据ID
     *
     * @param index     索引
     * @param dataValue 数据内容
     */
    public IndexResponse save(String index, M dataValue) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.source(JSON.toJSONString(dataValue), XContentType.JSON);
            return highLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("[ elasticsearch ] >> save exception ,index = {},dataValue={} ,stack={}", index, dataValue, e);
            throw new RuntimeException("[ elasticsearch ] >> save exception {}", e);
        }
    }


    /**
     * 批量处理数据
     * DocWriteRequest？
     * ->IndexRequest
     * ->UpdateRequest
     * ->DeleteRequest
     *
     * @param requests
     * @return
     */
    public BulkResponse bulkDocument(List<DocWriteRequest<?>> requests) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (DocWriteRequest writeRequest : requests) {
            bulkRequest.add(writeRequest);
        }
        return highLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }


    public void indexDocument(IndexRequest indexRequest) throws IOException {
        highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 异步批量数据处理
     * DocWriteRequest？
     * ->IndexRequest
     * ->UpdateRequest
     * ->DeleteRequest
     *
     * @param requests
     */
    public void bulkAsyncDocument(List<DocWriteRequest<?>> requests) {
        bulkAsyncListenerDocument(requests, actionListener());
    }

    /**
     * 异步批量数据处理-自定义响应
     *
     * @param requests
     * @param actionListener
     */
    public void bulkAsyncListenerDocument(List<DocWriteRequest<?>> requests, ActionListener<BulkResponse> actionListener) {
        BulkRequest bulkRequest = new BulkRequest();
        for (DocWriteRequest writeRequest : requests) {
            bulkRequest.add(writeRequest);
        }
        highLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, actionListener);
    }


    private ActionListener<BulkResponse> actionListener() {
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {
                if (bulkResponse.hasFailures()) {
                    log.error("Increased resource failure causes：{}", bulkResponse.buildFailureMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                log.error("Asynchronous batch increases data exceptions：{}", e.getLocalizedMessage());
            }
        };
        return listener;
    }


    /**
     * 检索
     *
     * @param searchRequest
     * @return
     */
    public SearchResult searchDocument(SearchRequest searchRequest) {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchResponse searchResponse;
        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHit = searchResponse.getHits().getHits();
            searchResponse.getTook().getMillis();
            long totalHits = searchResponse.getHits().getTotalHits().value;
            long took = searchResponse.getTook().getMillis();
            for (SearchHit document : searchHit) {
                Map<String, Object> item = document.getSourceAsMap();
                if (item == null) {
                    continue;
                }
                Map<String, HighlightField> highlightFields = document.getHighlightFields();
                if (!highlightFields.isEmpty()) {
                    for (String key : highlightFields.keySet()) {
                        Text[] fragments = highlightFields.get(key).fragments();
                        if (item.containsKey(key)) {
                            item.put(key, fragments[0].string());
                        }
                        String[] fieldArray = key.split("[.]");
                        if (fieldArray.length > 1) {
                            item.put(fieldArray[0], fragments[0].string());
                        }
                    }
                }
                list.add(item);
            }
            Map<String, Map<String, Long>> aggregations = getAggregation(searchResponse.getAggregations());
            return new SearchResult(totalHits, list, took, aggregations);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SearchResult();
    }


    private Map<String, Map<String, Long>> getAggregation(Aggregations aggregations) {
        if (aggregations == null) {
            return Collections.EMPTY_MAP;
        }
        Map<String, Map<String, Long>> result = new HashMap<>();
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        aggregationMap.forEach((k, v) -> {
            Map<String, Long> agg = new HashMap<>();
            List<? extends Terms.Bucket> buckets = ((ParsedStringTerms) v).getBuckets();
            for (Terms.Bucket bucket : buckets) {
                agg.put(bucket.getKeyAsString(), bucket.getDocCount());
            }
            result.put(k, agg);
        });
        return result;
    }


    /**
     * 删除
     *
     * @param request
     * @return
     */
    public Boolean deleteDocument(DeleteRequest request) throws IOException {
        DeleteResponse deleteResponse = highLevelClient.delete(request, RequestOptions.DEFAULT);
        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            log.info("not found doc id:{}", deleteResponse.getId());
            return false;
        }
        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            return true;
        }
        log.info("deleteResponse Status:{}", deleteResponse.status());
        return false;
    }


    /**
     * 异步查询更新
     *
     * @param request
     */
    public void updateByQueryDocument(UpdateByQueryRequest request) {
        try {
            highLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("updateByQuery Exception {}", e.getLocalizedMessage());
        }
    }


    private ActionListener<BulkByScrollResponse> bulkByScrolllistener() {
        return new ActionListener<BulkByScrollResponse>() {
            @Override
            public void onResponse(BulkByScrollResponse bulkResponse) {
                List<BulkItemResponse.Failure> failures = bulkResponse.getBulkFailures();
                if (!failures.isEmpty()) {
                    log.error("BulkByScrollResponse failures:{}", StringUtils.join(failures, "@"));
                }
                List<ScrollableHitSource.SearchFailure> searchFailures = bulkResponse.getSearchFailures();
                if (!failures.isEmpty()) {
                    log.error("BulkByScrollResponse searchFailures:{}", StringUtils.join(searchFailures, "@@"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                log.error("BulkByScrollResponse Exceptions：{}", e.getLocalizedMessage());
            }
        };
    }

    /**
     * 个数查询
     *
     * @param countRequest
     * @return
     */
    public Long countDocument(CountRequest countRequest) {
        try {
            CountResponse countResponse = highLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            log.error("CountResponse Exceptions：{}", e.getLocalizedMessage());
            return 0L;
        }
    }


    public boolean updateDocument(UpdateRequest updateRequest) {
        try {
            highLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("UpdateRequest Exception:{}", e.getLocalizedMessage());
            return false;
        }
    }


    public boolean lambdaUpdateDocument(ESLambdaWrapper<?> updateWrapper) {
        if (StringUtils.isBlank(updateWrapper.getDocId())) {
            log.warn("id does not exist:{}", updateWrapper.getDocId());
            return false;
        }
        if (updateWrapper.getFieldMap().isEmpty()) {
            log.warn("content is null");
            return false;
        }
        try {
            UpdateRequest request = new UpdateRequest(updateWrapper.getIndex(), updateWrapper.getDocId());
            request.doc(updateWrapper.getFieldMap());
            highLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (IOException io) {
            log.error("lambdaUpdateDocument Exception:{}", io.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean save(List<? extends Model> models) {
        return false;
    }

    @Override
    public boolean update(M model) {
        return false;
    }

    @Override
    public boolean update(M model, Q query) {
        return false;
    }

    @Override
    public boolean update(List<? extends Model> list) {
        return false;
    }

    @Override
    public boolean delete(Serializable id) {
        return false;
    }

    @Override
    public boolean delete(Q query) {
        return false;
    }

    @Override
    public boolean delete(List<? extends Serializable> ids) {
        return false;
    }

    @Override
    public boolean deleteByKey(Serializable key) {
        return false;
    }

    @Override
    public boolean deleteByKeys(List<Serializable> keys) {
        return false;
    }

    @Override
    public List<M> list(List<? extends Serializable> ids) {
        return null;
    }

    @Override
    public List<M> list(Q query) {
        return null;
    }

    @Override
    public M first(Q query) {
        return null;
    }

    @Override
    public M one(Q query) {
        return null;
    }

    @Override
    public M get(Serializable id) {
        return null;
    }

    @Override
    public int count(Q query) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> maps(Q query) {
        return null;
    }

    @Override
    public boolean exist(Q query) {
        return false;
    }

    @Override
    public Page<M> page(Q query) {
        return null;
    }

    @Override
    public boolean updateByKey(M model) {
        return false;
    }

    @Override
    public boolean updateByKey(List<? extends Model> models) {
        return false;
    }

    @Override
    public M getByKey(String key) {
        return null;
    }

    @Override
    public List<M> listByKey(List<Serializable> keys) {
        return null;
    }

    @Override
    public void fill(Q query, M model) {

    }

    @Override
    public void fill(Q query, List<M> models) {

    }


    /**
     * 保存文档-自定义数据ID
     *
     * @param index     索引
     * @param id        数据ID
     * @param dataValue 数据内容
     */
    public IndexResponse save(String index, String id, Object dataValue) {
        return this.saveOrUpdate(index, id, dataValue);
    }

    /**
     * 保存文档-自定义数据ID
     * <p>
     * 如果文档存在，则更新文档；如果文档不存在，则保存文档。
     *
     * @param index     索引
     * @param id        数据ID
     * @param dataValue 数据内容
     */
    public IndexResponse saveOrUpdate(String index, String id, Object dataValue) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.id(id);
            request.source(JSON.toJSONString(dataValue), XContentType.JSON);
            return highLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> save exception ,index = {},dataValue={} ,stack={}", index, dataValue, e);
            throw new RuntimeException("[ elasticsearch ] >> save exception {}", e);
        }
    }

    /**
     * 批量-新增或保存文档
     * <p>
     * 如果集合中有些文档已经存在，则更新文档；不存在，则保存文档。
     *
     * @param index        索引
     * @param documentList 文档集合
     */
    public void batchSaveOrUpdate(String index, List<ElasticSearchDocModel<?>> documentList) {
        if (CollectionUtils.isEmpty(documentList)) {
            return;
        }
        try {
            // 批量请求
            BulkRequest bulkRequest = new BulkRequest();
            documentList.forEach(doc -> bulkRequest.add(new IndexRequest(index)
                    .id(doc.getId())
                    .source(JSON.toJSONString(doc.getData()), XContentType.JSON)));
            highLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> batchSave exception ,index = {},documentList={} ,stack={}", index, documentList, e);
            throw new RuntimeException("[ elasticsearch ] >> batchSave exception {}", e);
        }
    }

    /**
     * 根据ID修改
     *
     * @param index     索引
     * @param id        数据ID
     * @param dataValue 数据内容
     */
    public UpdateResponse updateById(String index, String id, Object dataValue) {
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.doc(JSON.toJSONString(dataValue), XContentType.JSON);
            return highLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> updateById exception ,index = {},dataValue={} ,stack={}", index, dataValue, e);
            throw new RuntimeException("[ elasticsearch ] >> updateById exception {}", e);
        }
    }

    /**
     * 部分修改()
     * 注：1).可变更已有字段值，可新增字段，删除字段无效
     * 2).若当前ID数据不存在则新增
     *
     * @param index     索引
     * @param id        数据ID
     * @param dataValue 数据内容
     */
    public UpdateResponse updateByIdSelective(String index, String id, Object dataValue) {
        try {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(dataValue));
            UpdateRequest request = new UpdateRequest(index, id)
                    .doc(jsonObject)
                    .upsert(jsonObject);
            return highLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> updateByIdSelective exception ,index = {},dataValue={} ,stack={}", index, dataValue, e);
            throw new RuntimeException("[ elasticsearch ] >> updateByIdSelective exception {}", e);
        }
    }

    /**
     * 根据id查询
     *
     * @param index 索引
     * @param id    数据ID
     * @return T
     */
    public <T> T getById(String index, String id, Class<T> clazz) {
        GetResponse getResponse = this.getById(index, id);
        if (null == getResponse) {
            return null;
        }
        return JSON.parseObject(getResponse.getSourceAsString(), clazz);
    }

    /**
     * 根据id集批量获取数据
     *
     * @param index  索引
     * @param idList 数据ID集
     * @return T
     */
    public <T> List<T> getByIdList(String index, List<String> idList, Class<T> clazz) {
        MultiGetItemResponse[] responses = this.getByIdList(index, idList);
        if (null == responses || responses.length == 0) {
            return new ArrayList<>(0);
        }

        List<T> resultList = new ArrayList<>(responses.length);
        for (MultiGetItemResponse response : responses) {
            GetResponse getResponse = response.getResponse();
            if (!getResponse.isExists()) {
                continue;
            }
            resultList.add(JSON.parseObject(getResponse.getSourceAsString(), clazz));
        }

        return resultList;
    }

    /**
     * 根据多条件查询--分页
     * 注：from-size -[ "浅"分页 ]
     *
     * @param index    索引
     * @param pageNo   页码（第几页）
     * @param pageSize 页容量- Elasticsearch默认配置单次最大限制10000
     */
    public <T> List<T> searchPageByIndex(String index, Integer pageNo, Integer pageSize, Class<T> clazz) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(PageUtils.getStartRow(pageNo, pageSize));
        searchSourceBuilder.size(pageSize);

        return this.searchByQuery(index, searchSourceBuilder, clazz);
    }

    /**
     * 条件查询
     *
     * @param index         索引
     * @param sourceBuilder 条件查询构建起
     * @param <T>           数据类型
     * @return T 类型的集合
     */
    public <T> List<T> searchByQuery(String index, SearchSourceBuilder sourceBuilder, Class<T> clazz) {
        try {
            // 构建查询请求
            SearchRequest searchRequest = new SearchRequest(index).source(sourceBuilder);
            // 获取返回值
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            if (null == hits || hits.length == 0) {
                return new ArrayList<>(0);
            }

            List<T> resultList = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                resultList.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            }
            return resultList;
        } catch (ElasticsearchStatusException e) {
            //索引不存在
            if (e.status() == RestStatus.NOT_FOUND) {
                log.error("[ elasticsearch ] >>  searchByQuery exception >>  index:{}, Not found ", index, e);
                return new ArrayList<>(0);
            }
            throw new RuntimeException("[ elasticsearch ] >> searchByQuery exception {}", e);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> searchByQuery exception ,index = {},sourceBuilder={} ,stack={}", index, sourceBuilder, e);
            throw new RuntimeException("[ elasticsearch ] >> searchByQuery exception {}", e);
        }
    }


    /**
     * 根据ID删除文档
     *
     * @param index 索引
     * @param id    文档ID
     * @return 是否删除成功
     */
    public boolean deleteById(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse response = highLevelClient.delete(request, RequestOptions.DEFAULT);
            //未找到文件
            if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                log.error("[ elasticsearch ] >> deleteById document is not found , index:{},id:{}", index, id);
                return false;
            }
            return RestStatus.OK.equals(response.status());
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> deleteById exception ,index:{},id:{} ,stack:{}", index, id, e);
            throw new RuntimeException("[ elasticsearch ] >> deleteById exception {}", e);
        }
    }

    /**
     * 根据查询条件删除文档
     *
     * @param index        索引
     * @param queryBuilder 查询条件构建器
     */
    public void deleteByQuery(String index, QueryBuilder queryBuilder) {
        try {
            DeleteByQueryRequest request = new DeleteByQueryRequest(index).setQuery(queryBuilder);
            request.setConflicts("proceed");
            highLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> deleteByQuery exception ,index = {},queryBuilder={} ,stack={}", index, queryBuilder, e);
            throw new RuntimeException("[ elasticsearch ] >> deleteByQuery exception {}", e);
        }
    }

    /**
     * 根据文档 ID 批量删除文档
     *
     * @param index  索引
     * @param idList 文档 ID 集合
     */
    public void deleteByIdList(String index, List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        try {
            BulkRequest bulkRequest = new BulkRequest();
            idList.forEach(id -> bulkRequest.add(new DeleteRequest(index, id)));
            highLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> deleteByIdList exception ,index = {},idList={} ,stack={}", index, idList, e);
            throw new RuntimeException("[ elasticsearch ] >> deleteByIdList exception {}", e);
        }
    }

    /**
     * 根据id查询
     *
     * @param index 索引
     * @param id    文档ID
     * @return GetResponse
     */
    private GetResponse getById(String index, String id) {
        try {
            GetRequest request = new GetRequest(index, id);
            return highLevelClient.get(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                log.error("[ elasticsearch ] >> getById document not found ,index = {},id={} ,stack={}", index, id, e);
                return null;
            }
            throw new RuntimeException("[ elasticsearch ] >> getById exception {}", e);
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> getById exception ,index = {},id={} ,stack={}", index, id, e);
            throw new RuntimeException("[ elasticsearch ] >> getById exception {}", e);
        }
    }

    /**
     * 根据id集-批量获取数据
     *
     * @param index  索引
     * @param idList 数据文档ID集
     * @return MultiGetItemResponse[]
     */
    private MultiGetItemResponse[] getByIdList(String index, List<String> idList) {
        try {
            MultiGetRequest request = new MultiGetRequest();
            for (String id : idList) {
                request.add(new MultiGetRequest.Item(index, id));
            }

            //同步执行
            MultiGetResponse responses = highLevelClient.mget(request, RequestOptions.DEFAULT);
            return responses.getResponses();
        } catch (IOException e) {
            log.error("[ elasticsearch ] >> getByIdList exception ,index = {},idList={} ,stack={}", index, idList, e);
            throw new RuntimeException("[ elasticsearch ] >> getByIdList exception {}", e);
        }
    }
}
