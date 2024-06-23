package com.dddframework.data.repository.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.BaseRepository;
import com.dddframework.core.contract.Model;
import com.dddframework.core.contract.Page;
import com.dddframework.core.contract.Query;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.BeanKit;
import com.dddframework.core.utils.JsonKit;
import com.dddframework.core.utils.MappingKit;
import com.dddframework.data.annotation.*;
import com.dddframework.data.config.BaseDataProperties;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dddframework.core.contract.Query.*;

@Slf4j(topic = "### BASE-DATA : BaseRepository ###")
public abstract class BaseRepositoryImpl<MP extends BaseMapper<P>, M extends Model, P, Q extends Query> implements BaseRepository<M, Q>, Serializable {
    @Autowired
    @Getter
    private MP mapper;
    private final TableScheme tableScheme;
    @Autowired
    BaseDataProperties baseDataProperties;

    public BaseRepositoryImpl() {
        final Class<M> modelClass = (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), 1);
        final Class<P> poClass = (Class<P>) ReflectionKit.getSuperClassGenericType(this.getClass(), 2);
        final Class<Q> queryClass = (Class<Q>) ReflectionKit.getSuperClassGenericType(this.getClass(), 3);
        this.tableScheme = TableScheme.build(poClass);
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

    @Override
    public boolean save(M model) {
        P po = convert(model);
        insertFill(po);
        boolean result = SqlHelper.retBool(this.mapper.insert(po));
        if (result) {
            BeanKit.copy(po, model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(List<? extends Model> models) {
        if (models == null || models.size() == 0) {
            return false;
        }
        List<P> pos = convert(models);
        boolean result = insertBatch(pos);
        if (result) {
            BeanKit.copy(pos, models);
        }
        return result;
    }

    @Override
    public boolean update(M model) {
        P po = convert(model);
        updateFill(po);
        boolean result = SqlHelper.retBool(this.mapper.updateById(po));
        if (result) {
            BeanKit.copy(po, model);
        }
        return result;
    }

    @Override
    public boolean update(M model, Q query) {
        P po = convert(model);
        updateFill(po);
        boolean result = SqlHelper.retBool(this.mapper.update(po, this.getBaseWrapper(query)));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(result));
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean update(List<? extends Model> models) {
        if (models == null || models.size() == 0) {
            return false;
        }
        List<P> pos = convert(models);
        boolean result = updateBatch(pos, 100);
        if (result) {
            BeanKit.copy(pos, models);
        }
        return result;
    }

    @Override
    public boolean delete(Serializable id) {
        return id != null && SqlHelper.retBool(this.mapper.deleteById(id));
    }

    @Override
    public boolean delete(Q query) {
        boolean result = SqlHelper.retBool(this.mapper.delete(this.getBaseWrapper(query)));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(result));
        return result;
    }

    @Override
    public boolean delete(List<? extends Serializable> ids) {
        if (ids == null || ids.size() == 0) {
            log.warn("batch function query is empty or null");
            return false;
        }
        if (ids.size() >= 100) {
            throw new IllegalArgumentException("当前批量删除的ID不能大于100");
        }
        return SqlHelper.retBool(this.mapper.deleteBatchIds(ids));
    }

    @Override
    public List<Map<String, Object>> maps(Q query) {
        List<Map<String, Object>> maps = this.mapper.selectMaps(this.getBaseWrapper(query));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMapClean(query)), getTenantLine(), getSqlLine(String.format("%d rows", maps == null ? 0 : maps.size())));
        return maps;
    }

    @Override
    public List<M> list(List<? extends Serializable> ids) {
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("Error: ids must not be empty");
        }
        if (ids.size() >= 100) {
            throw new IllegalArgumentException("当前批量查询的ID不能大于100");
        }
        return convert(this.mapper.selectBatchIds(ids));
    }

    @Override
    public List<M> list(Q query) {
        List<M> models = convert(this.mapper.selectList(this.getBaseWrapper(query)));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(String.format("%d rows", models == null ? 0 : models.size())));
        if (models != null && models.size() != 0) {
            fill(query, models);
        }
        return models;
    }

    @Override
    public M first(Q query) {
        M model = convert(this.mapper.selectOne(this.getBaseWrapper(query).last("limit 1")));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(String.format("%d rows", model != null ? 1 : 0)));
        if (model != null) {
            fill(query, model);
        }
        return model;
    }

    @Override
    public M one(Q query) {
        M model = convert(this.mapper.selectOne(this.getBaseWrapper(query)));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(String.format("%d rows", model != null ? 1 : 0)));
        if (model != null) {
            fill(query, model);
        }
        return model;
    }

    @Override
    public M get(@NonNull Serializable id) {
        return convert(this.mapper.selectById(id));
    }

    @Override
    public int count(Q query) {
        Integer count = this.mapper.selectCount(this.getBaseWrapper(query));
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMap(query, false, "limit", "page")), getTenantLine(), getSqlLine(count));
        return count;
    }

    @Override
    public boolean exist(Q query) {
        return SqlHelper.retBool(count(query));
    }

    @Override
    public Page<M> page(Q query) {
        Page<P> poPage = this.mapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), this.getBaseWrapper(query));
        Page<M> modelPage = Page.succeed(convert(poPage.getRecords()), poPage.getTotal(), poPage.getCurrent(), poPage.getSize());
        log.info("{}: {}{}{}", query.getClass().getSimpleName(), JsonKit.toJson(BeanKit.toMapClean(query)), getTenantLine(), getSqlLine(String.format("%d rows", modelPage.getTotal())));
        if (modelPage.getRecords() != null && modelPage.getRecords().size() > 0) {
            fill(query, modelPage.getRecords());
        }
        return modelPage;
    }

    @Override
    public boolean deleteByKey(@NonNull Serializable key) {
        return SqlHelper.retBool(this.mapper.delete(this.getKeyWrapper(key)));
    }

    @Override
    public boolean deleteByKeys(List<Serializable> keys) {
        return keys != null && !keys.isEmpty() && SqlHelper.retBool(this.mapper.delete(this.getKeyWrapper(keys)));
    }

    @Override
    public boolean updateByKey(M model) {
        P po = convert(model);
        String key = this.getKeyValue(po);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("当前entity实体业务key字段为null，无法适用当前方法更新！");
        } else {
            updateFill(po);
            return SqlHelper.retBool(this.mapper.update(po, this.getKeyWrapper(key)));
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateByKey(List<? extends Model> models) {
        if (models == null || models.isEmpty()) {
            log.warn("batch function query is empty or null");
            return false;
        }
        if (models.size() >= 100) {
            throw new IllegalArgumentException("批量更新的业务key不能大于100");
        }
        Class<P> poClass = MappingKit.get("MODEL_PO", models.get(0).getClass());
        clearSqlSessionCache(poClass);
        SqlSession batchSqlSession = sqlSessionBatch(poClass);
        try {
            for (Model model : models) {
                this.updateByKey((M) model);
            }
            batchSqlSession.flushStatements();
        } finally {
            closeSqlSession(poClass, batchSqlSession);
        }
        return true;
    }

    @Override
    public M getByKey(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("The key annotated by @BizKey must not blank");
            return null;
        }
        return convert(this.mapper.selectOne(this.getKeyWrapper(key)));
    }

    @Override
    public List<M> listByKey(List<Serializable> keys) {
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        if (keys.size() >= 100) {
            throw new IllegalArgumentException("批量查询的业务key不能大于100");
        }
        return convert(this.mapper.selectList(this.getKeyWrapper(keys)));
    }

    @Override
    public void fill(Q query, M model) {
        fill(query, Collections.singletonList(model));
    }

    @Override
    public void fill(Q query, List<M> models) {
    }

    protected QueryWrapper<P> getDefaultWrapper() {
        QueryWrapper<P> wrapper = new QueryWrapper<>();
        if (tableScheme.getTenantId() != null && ThreadContext.contains(ContextConstants.TENANT_ID)) {
            wrapper.eq(TableScheme.getColumn(tableScheme.getTenantId()), ThreadContext.get(ContextConstants.TENANT_ID));
        }
        if (tableScheme.getSystemId() != null && ThreadContext.contains(ContextConstants.SYSTEM_ID)) {
            wrapper.eq(TableScheme.getColumn(tableScheme.getSystemId()), ThreadContext.get(ContextConstants.SYSTEM_ID));
        }
        return wrapper;
    }

    protected QueryWrapper<P> getKeyWrapper(Serializable key) {
        if (tableScheme.getBizKeyField() == null) {
            throw new IllegalArgumentException("当前entity实体没找到业务key字段，请在entity实体中使用@BizKey注解标记对应的字段！");
        } else {
            QueryWrapper<P> wrapper = getDefaultWrapper();
            wrapper.eq(TableScheme.getColumn(tableScheme.getBizKeyField()), key);
            return wrapper;
        }
    }

    protected QueryWrapper<P> getKeyWrapper(List<Serializable> keys) {
        if (tableScheme.getBizKeyField() == null) {
            throw new IllegalArgumentException("当前entity实体没找到业务key字段，请在entity实体中使用@BizKey注解标记对应的字段！");
        } else {
            QueryWrapper<P> wrapper = getDefaultWrapper();
            wrapper.in(TableScheme.getColumn(tableScheme.getBizKeyField()), keys);
            return wrapper;
        }
    }

    protected QueryWrapper<P> getBaseWrapper(Q query) {
        QueryWrapper<P> wrapper = getDefaultWrapper();
        this.select(query, wrapper).where(query, wrapper).groupBy(query, wrapper).having(query, wrapper).orderBy(query, wrapper);
        return wrapper;
    }

    protected static SqlSessionFactory sqlSessionFactory(Class<?> poClass) {
        return GlobalConfigUtils.currentSessionFactory(poClass);
    }

    protected SqlSession sqlSession(Class<?> poClass) {
        return SqlSessionUtils.getSqlSession(sqlSessionFactory(poClass));
    }

    protected static SqlSession sqlSessionBatch(Class<?> poClass) {
        return SqlHelper.sqlSessionBatch(poClass);
    }

    protected static void closeSqlSession(Class<?> poClass, SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(poClass));
    }

    protected static void clearSqlSessionCache(Class<?> poClass) {
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory(poClass));
        boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
        if (sqlSessionHolder != null) {
            SqlSession sqlSession = sqlSessionHolder.getSqlSession();
            sqlSession.commit(!transaction);
        }
    }

    protected static String sqlStatement(Class<?> poClass, SqlMethod sqlMethod) {
        return SqlHelper.table(poClass).getSqlStatement(sqlMethod.getMethod());
    }

    protected boolean insertBatch(List<P> pos) {
        Class<?> poClass = pos.get(0).getClass();
        clearSqlSessionCache(poClass);
        SqlSession batchSqlSession = sqlSessionBatch(poClass);
        int i = 0;
        String sqlStatement = sqlStatement(poClass, SqlMethod.INSERT_ONE);

        try {
            for (Iterator<P> iterator = pos.iterator(); iterator.hasNext(); ++i) {
                P po = iterator.next();
                insertFill(po);
                batchSqlSession.insert(sqlStatement, po);
                if (i >= 1 && i % 100 == 0) {
                    batchSqlSession.flushStatements();
                }
            }

            batchSqlSession.flushStatements();
            return true;
        } finally {
            closeSqlSession(poClass, batchSqlSession);
        }
    }

    public boolean updateBatch(List<P> pos, int batchSize) {
        if (batchSize < 1) {
            return false;
        }
        if (pos == null || pos.isEmpty()) {
            log.warn("batch function query is empty or null");
            return false;
        } else {
            Class<?> poClass = pos.get(0).getClass();
            clearSqlSessionCache(poClass);
            SqlSession batchSqlSession = sqlSessionBatch(poClass);
            int i = 0;
            String sqlStatement = sqlStatement(poClass, SqlMethod.UPDATE_BY_ID);

            try {
                for (Iterator<P> iterator = pos.iterator(); iterator.hasNext(); ++i) {
                    P po = iterator.next();
                    ParamMap<P> query = new ParamMap<>();
                    query.put("et", po);
                    updateFill(po);
                    batchSqlSession.update(sqlStatement, query);
                    if (i >= 1 && i % batchSize == 0) {
                        batchSqlSession.flushStatements();
                    }
                }
                batchSqlSession.flushStatements();
                return true;
            } finally {
                closeSqlSession(poClass, batchSqlSession);
            }
        }
    }

    protected boolean isCollectionType(Object o) {
        return o instanceof Collection;
    }

    protected boolean isDateType(Object o) {
        return o instanceof Date || o instanceof LocalDateTime || o instanceof LocalDate || o instanceof LocalTime;
    }

    private boolean isStringBlank(Object value) {
        return value == null || (value instanceof CharSequence && ((CharSequence) value).length() == 0);
    }

    private String getFieldName(String fieldName, String queryAction) {
        String replaceLast = replaceLast(fieldName, queryAction, "");
        return replaceLast != null && replaceLast.length() != 0 ? replaceLast.toLowerCase() : replaceLast;
    }

    protected void getColumnByField(String fieldName, Consumer<String> action) {
        if (this.tableScheme.containsField(fieldName)) {
            action.accept(this.tableScheme.getField(fieldName));
        }
    }

    protected String getKeyValue(P po) {
        try {
            Object value = this.tableScheme.bizKeyField.get(po);
            return value != null ? value.toString() : null;
        } catch (IllegalAccessException | IllegalArgumentException var3) {
            log.error("获取当前实体对象的key值出现错误！", var3);
            throw new IllegalArgumentException("获取当前实体对象的key值出现错误!");
        }
    }

    protected void setKeyValue(P po, String key) {
        try {
            this.tableScheme.bizKeyField.set(po, key);
        } catch (IllegalAccessException | IllegalArgumentException var4) {
            log.error("设置当前实体对象的key值出现错误！", var4);
            throw new IllegalArgumentException("设置当前实体对象的key值出现错误!");
        }
    }

    private BaseRepositoryImpl<MP, M, P, Q> select(Q query, QueryWrapper<P> baseWrapper) {
        if (query.getSelect() != null && query.getSelect().length != 0) {
            baseWrapper.select(query.getSelect());
        }
        return this;
    }

    private BaseRepositoryImpl<MP, M, P, Q> where(Q query, QueryWrapper<P> baseWrapper) {
        Map<String, Object> mapParams = BeanKit.toMapClean(query);
        if (mapParams != null) {
            mapParams.forEach((key, value) -> this.setCondition(baseWrapper, key, value));
        }
        // 处理多个关键字or查询
        if (query.getKeywords() != null && !query.getKeywords().isEmpty()) {
            Collection<Object> values = query.getKeywords().values();
            boolean canWrap = false;
            for (Object value : values) {
                if (value != null) {
                    if (value instanceof CharSequence) {
                        if (((CharSequence) value).length() != 0) {
                            canWrap = true;
                            break;
                        }
                    } else {
                        canWrap = true;
                        break;
                    }
                }
            }
            if (canWrap) {
                baseWrapper.and(w -> query.getKeywords().forEach((keyword, v) -> this.setCondition(w, keyword, v).or()));
            }
        }
        return this;
    }

    private QueryWrapper<P> setCondition(QueryWrapper<P> wrapper, String key, Object value) {
        if (value == null || key.equals(KEYWORDS_QUERY)) {
            return wrapper;
        }
        if (key.endsWith(START_QUERY) && this.isDateType(value)) {
            this.getColumnByField(this.getFieldName(key, START_QUERY), (p) -> wrapper.ge(p, value));
        } else if (key.endsWith(END_QUERY) && this.isDateType(value)) {
            this.getColumnByField(this.getFieldName(key, END_QUERY), (p) -> wrapper.le(p, value));
        } else if (key.endsWith(MIN_EQUALS_QUERY)) {
            this.getColumnByField(this.getFieldName(key, MIN_EQUALS_QUERY), (p) -> wrapper.ge(p, value));
        } else if (key.endsWith(MAX_EQUALS_QUERY)) {
            this.getColumnByField(this.getFieldName(key, MAX_EQUALS_QUERY), (p) -> wrapper.le(p, value));
        } else if (key.endsWith(MIN_QUERY)) {
            this.getColumnByField(this.getFieldName(key, MIN_QUERY), (p) -> wrapper.gt(p, value));
        } else if (key.endsWith(MAX_QUERY)) {
            this.getColumnByField(this.getFieldName(key, MAX_QUERY), (p) -> wrapper.lt(p, value));
        } else if (key.endsWith(NOT_IN_QUERY) && this.isCollectionType(value) && ((Collection<?>) value).size() != 0) {
            List<?> list = ((Collection<?>) value).stream().distinct().collect(Collectors.toList());
            this.getColumnByField(this.getFieldName(key, NOT_IN_QUERY), (p) -> wrapper.notIn(p, list));
        } else if (key.endsWith(IN_QUERY) && this.isCollectionType(value) && ((Collection<?>) value).size() != 0) {
            List<?> list = ((Collection<?>) value).stream().distinct().collect(Collectors.toList());
            this.getColumnByField(this.getFieldName(key, IN_QUERY), (p) -> wrapper.in(p, list));
        } else if (key.endsWith(LIKE_QUERY) && value instanceof CharSequence) {
            if (!this.isStringBlank(value)) {
                this.getColumnByField(this.getFieldName(key, LIKE_QUERY), (p) -> wrapper.like(p, value));
            }
        } else if (key.endsWith(LIKE_LEFT_QUERY) && value instanceof CharSequence) {
            if (!this.isStringBlank(value)) {
                this.getColumnByField(this.getFieldName(key, LIKE_LEFT_QUERY), (p) -> wrapper.likeLeft(p, value));
            }
        } else if (key.endsWith(LIKE_RIGHT_QUERY) && value instanceof CharSequence) {
            if (!this.isStringBlank(value)) {
                this.getColumnByField(this.getFieldName(key, LIKE_RIGHT_QUERY), (p) -> wrapper.likeRight(p, value));
            }
        } else if (key.endsWith(NOT_QUERY)) {
            this.getColumnByField(this.getFieldName(key, NOT_QUERY), (p) -> wrapper.ne(p, value));
        } else if (key.endsWith(NULL_QUERY)) {
            if (Objects.equals(Boolean.TRUE, value)) {
                this.getColumnByField(this.getFieldName(key, NULL_QUERY), wrapper::isNull);
            } else if (Objects.equals(Boolean.FALSE, value)) {
                this.getColumnByField(this.getFieldName(key, NULL_QUERY), wrapper::isNotNull);
            }
        } else {
            this.getColumnByField(key, (p) -> wrapper.eq(p, value));
        }
        return wrapper;
    }

    private BaseRepositoryImpl<MP, M, P, Q> groupBy(Q query, QueryWrapper<P> wrapper) {
        if (query != null && query.getGroupBy() != null && !query.getGroupBy().isEmpty()) {
            wrapper.groupBy(query.getGroupBy());
        }
        return this;
    }

    private BaseRepositoryImpl<MP, M, P, Q> having(Q query, QueryWrapper<P> wrapper) {
        if (query != null && query.getHaving() != null && !query.getHaving().isEmpty()) {
            wrapper.having(query.getHaving());
        }
        return this;
    }

    private BaseRepositoryImpl<MP, M, P, Q> orderBy(Q query, QueryWrapper<P> wrapper) {
        if (query.getOrderBy() == null || query.getOrderBy().length == 0) {
            // 设置默认排序
            query.setOrderBy(tableScheme.getDefaultOrderBy());
        }
        if (query.getOrderBy() != null) {
            for (String orderBy : query.getOrderBy()) {
                if (orderBy != null && !orderBy.isEmpty()) {
                    // 统一转成下划线形式，传参可以是驼峰式，也可以是下划线
                    String column = TableScheme.toUnderline(orderBy.replace("_asc", "").replace("_ASC", "").replace("_desc", "").replace("_DESC", ""));
                    wrapper.orderBy(this.tableScheme.containsColumn(column), !orderBy.toLowerCase().endsWith("_desc"), column);
                }
            }
        }
        return this;
    }

    protected boolean checkIsDataColumn(String fieldName, Class<P> tClass) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        } else {
            Field field = FieldUtils.getField(tClass, fieldName, true);
            return field != null;
        }
    }

    private void insertFill(P po) {
        try {
            if (tableScheme.getTenantId() != null && ThreadContext.contains(ContextConstants.TENANT_ID)) {
                String tenantId = ThreadContext.get(ContextConstants.TENANT_ID);
                if (tableScheme.getTenantId().getType() == Long.class) {
                    tableScheme.getTenantId().set(po, Long.valueOf(tenantId));
                } else if (tableScheme.getTenantId().getType() == Integer.class) {
                    tableScheme.getTenantId().set(po, Integer.valueOf(tenantId));
                } else if (tableScheme.getTenantId().getType() == String.class) {
                    tableScheme.getTenantId().set(po, tenantId);
                }
            }
            if (tableScheme.getSystemId() != null && ThreadContext.contains(ContextConstants.SYSTEM_ID)) {
                String systemId = ThreadContext.get(ContextConstants.SYSTEM_ID);
                if (tableScheme.getSystemId().getType() == Long.class) {
                    tableScheme.getSystemId().set(po, Long.valueOf(systemId));
                } else if (tableScheme.getSystemId().getType() == Integer.class) {
                    tableScheme.getSystemId().set(po, Integer.valueOf(systemId));
                } else if (tableScheme.getSystemId().getType() == String.class) {
                    tableScheme.getSystemId().set(po, systemId);
                }
            }
            if (tableScheme.getTableLogic() != null) {
                String defaultValue = tableScheme.getTableLogic().getAnnotation(TableLogic.class).value();
                if (defaultValue == null || defaultValue.isEmpty()) {
                    if (tableScheme.getTableLogic().getType().equals(Boolean.class) || tableScheme.getTableLogic().getType().equals(boolean.class)) {
                        tableScheme.getTableLogic().set(po, false);
                    } else if (tableScheme.getTableLogic().getType().equals(Integer.class) || tableScheme.getTableLogic().getType().equals(int.class)) {
                        tableScheme.getTableLogic().set(po, 0);
                    }
                } else {
                    if (tableScheme.getTableLogic().getType().equals(Boolean.class) || tableScheme.getTableLogic().getType().equals(boolean.class)) {
                        tableScheme.getTableLogic().set(po, defaultValue.equals("0"));
                    } else if (tableScheme.getTableLogic().getType().equals(Integer.class) || tableScheme.getTableLogic().getType().equals(int.class)) {
                        tableScheme.getTableLogic().set(po, Integer.valueOf(defaultValue));
                    }
                }
            }
            for (Field onCreateField : tableScheme.getOnCreateFields()) {
                if (onCreateField.getType().equals(LocalDateTime.class)) {
                    onCreateField.set(po, LocalDateTime.now());
                } else if (onCreateField.getType().equals(LocalDate.class)) {
                    onCreateField.set(po, LocalDate.now());
                }
            }
        } catch (IllegalAccessException ignore) {
        }
    }

    private void updateFill(P po) {
        try {
            for (Field onUpdateField : tableScheme.getOnUpdateFields()) {
                if (onUpdateField.getType().equals(LocalDateTime.class)) {
                    onUpdateField.set(po, LocalDateTime.now());
                } else if (onUpdateField.getType().equals(LocalDate.class)) {
                    onUpdateField.set(po, LocalDate.now());
                }
            }
        } catch (IllegalAccessException ignore) {
        }
    }

    private static String replaceLast(String raw, String match, String replace) {
        if (raw == null || raw.length() == 0 || null == replace) {
            //参数不合法，原样返回
            return raw;
        }
        StringBuilder sBuilder = new StringBuilder(raw);
        int lastIndexOf = sBuilder.lastIndexOf(match);
        if (-1 == lastIndexOf) {
            return raw;
        }

        return sBuilder.replace(lastIndexOf, lastIndexOf + match.length(), replace).toString();
    }

    public static <T, S> T convert(S source) {
        if (source == null) {
            return null;
        }
        Class<T> targetClass = MappingKit.get("MODEL_PO", source.getClass());
        return BeanKit.copy(source, targetClass);
    }

    public static <T, S> List<T> convert(List<S> source) {
        if (source == null || source.size() == 0) {
            return Collections.emptyList();
        }
        Class<T> targetClass = MappingKit.get("MODEL_PO", source.get(0).getClass());
        return BeanKit.copy(source, targetClass);
    }

    private String getTenantLine() {
        if (this.tableScheme.getTenantId() != null && ThreadContext.contains(ContextConstants.TENANT_ID)) {
            return ", tenantId: " + ThreadContext.get(ContextConstants.TENANT_ID);
        } else {
            return "";
        }
    }

    private String getSqlLine(Object object) {
        String lastSQL = "";
        if (baseDataProperties.getPrintSql()) {
            List<String> sqlParams = ThreadContext.get(ContextConstants.SQL_PARAMS);
            String parametersLine = sqlParams == null || sqlParams.size() == 0 ? "" : "\n==> Parameters: " + String.join(", ", sqlParams);
            lastSQL = String.format("\n==>  Preparing: %s%s\n<==     Return: %s in %s ms", ThreadContext.get(ContextConstants.PREPARING_SQL), parametersLine, object, ThreadContext.get(ContextConstants.LAST_SQL_SPENDS));
        }
        ThreadContext.remove(ContextConstants.PREPARING_SQL);
        ThreadContext.remove(ContextConstants.SQL_PARAMS);
        ThreadContext.remove(ContextConstants.LAST_SQL_SPENDS);
        return lastSQL;
    }

    @Data
    public static class TableScheme {
        private String tableName;
        private Field bizKeyField;
        private Map<String, String> field2Column;
        private Field tenantId;
        private Field systemId;
        private Field tableLogic;
        private List<Field> onCreateFields = new ArrayList<>();
        private List<Field> onUpdateFields = new ArrayList<>();
        private String[] defaultOrderBy;

        private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

        /**
         * 驼峰转下划线
         *
         * @return
         * @query humpString
         */
        protected static String toUnderline(String humpString) {
            if (humpString == null || humpString.isEmpty()) return humpString;
            Matcher matcher = HUMP_PATTERN.matcher(humpString);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        protected boolean containsField(String field) {
            return field2Column != null && field2Column.containsKey(field.toLowerCase());
        }

        protected String getField(String field) {
            return field2Column.get(field.toLowerCase());
        }

        protected boolean containsColumn(String column) {
            return field2Column != null && field2Column.containsValue(column.toLowerCase());
        }

        protected static String getColumn(Field field) {
            TableField tableField = field.getAnnotation(TableField.class);
            return tableField != null && tableField.value() != null && !tableField.value().isEmpty() ? tableField.value() : TableScheme.toUnderline(field.getName());
        }

        protected static TableScheme build(Class<?> poClass) {
            if (poClass == null) {
                return null;
            } else {
                TableScheme tableScheme = new TableScheme();
                TableName table = poClass.getAnnotation(TableName.class);
                if (table == null) {
                    throw new IllegalArgumentException("PO class must annotated with @TableName(\"table_name\")");
                } else {
                    tableScheme.setTableName(table.value());
                    List<Field> poFields = FieldUtils.getAllFieldsList(poClass);
                    poFields = poFields.stream().filter((p) -> !Modifier.isStatic(p.getModifiers())).collect(Collectors.toList());
                    if (poFields.size() != 0) {
                        tableScheme.field2Column = new HashMap<>(poFields.size());
                        for (Field poField : poFields) {
                            poField.setAccessible(true);
                            String fieldName = poField.getName();
                            // 优先读取TableField.value字段，否则把字段从驼峰式转换为下划线
                            TableField tableField = poField.getAnnotation(TableField.class);
                            String column = tableField != null && tableField.value() != null && !tableField.value().isEmpty() ? tableField.value() : TableScheme.toUnderline(fieldName);
                            tableScheme.field2Column.put(fieldName.toLowerCase(), column);
                            if (poField.getAnnotation(BizKey.class) != null) {
                                tableScheme.bizKeyField = poField;
                            }
                            if (poField.isAnnotationPresent(TenantId.class)) {
                                tableScheme.tenantId = poField;
                            }
                            if (poField.isAnnotationPresent(SystemId.class)) {
                                tableScheme.systemId = poField;
                            }
                            if (poField.isAnnotationPresent(TableLogic.class)) {
                                tableScheme.tableLogic = poField;
                            }
                            if (poField.isAnnotationPresent(OnCreate.class)) {
                                tableScheme.getOnCreateFields().add(poField);
                            }
                            if (poField.isAnnotationPresent(OnUpdate.class)) {
                                tableScheme.getOnUpdateFields().add(poField);
                            }
                        }

                    }
                    OrderBy orderBy = poClass.getAnnotation(OrderBy.class);
                    if (orderBy == null && poClass.getSuperclass() != null) {
                        orderBy = poClass.getSuperclass().getAnnotation(OrderBy.class);
                    }
                    if (orderBy != null && orderBy.value() != null && orderBy.value().length != 0) {
                        tableScheme.setDefaultOrderBy(orderBy.value());
                    }
                    return tableScheme;
                }
            }
        }

    }

}