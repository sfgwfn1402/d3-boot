package com.dddframework.data.mongodb.repository.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.dddframework.core.mongodb.context.ThreadContext;
import com.dddframework.core.mongodb.contract.BaseRepository;
import com.dddframework.core.mongodb.contract.Model;
import com.dddframework.core.mongodb.contract.Page;
import com.dddframework.core.mongodb.contract.Query;
import com.dddframework.core.mongodb.contract.constant.ContextConstants;
import com.dddframework.core.mongodb.utils.BeanKit;
import com.dddframework.core.mongodb.utils.MappingKit;
import com.dddframework.data.mongodb.annotation.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j(topic = "### MONGODB-BASE-DATA : MongoDBBaseRepository ###")
public class MongoBaseRepositoryImpl<M extends Model, P, Q extends Query> implements BaseRepository<M, Q>, Serializable {
    private TableScheme tableScheme;

    @Autowired
    public MongoTemplate mongoRepository;


    public MongoBaseRepositoryImpl() {
        final Class<M> modelClass = (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), 0);
        final Class<P> poClass = (Class<P>) ReflectionKit.getSuperClassGenericType(this.getClass(), 1);
        final Class<Q> queryClass = (Class<Q>) ReflectionKit.getSuperClassGenericType(this.getClass(), 2);
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
    public M save(M model) {
        return mongoRepository.save(model);
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
    public M findById(Long id) {
        mongoRepository.findById(id, this.getClass());
        return null;
    }

//    @Override
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
//    public IndexResponse save(String index, String id, Object dataValue) {
//        return this.saveOrUpdate(index, id, dataValue);
//    }




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
//                TableName table = poClass.getAnnotation(TableName.class);
//                if (table == null) {
//                    throw new IllegalArgumentException("PO class must annotated with @TableName(\"table_name\")");
//                } else {
//                    tableScheme.setTableName(table.value());
                    List<Field> poFields = FieldUtils.getAllFieldsList(poClass);
                    poFields = poFields.stream().filter((p) -> !Modifier.isStatic(p.getModifiers())).collect(Collectors.toList());
                    if (poFields.size() != 0) {
                        tableScheme.field2Column = new HashMap<>(poFields.size());
                        for (Field poField : poFields) {
                            poField.setAccessible(true);
                            String fieldName = poField.getName();
                            // 优先读取TableField.value字段，否则把字段从驼峰式转换为下划线
//                            TableField tableField = poField.getAnnotation(TableField.class);
//                            String column = tableField != null && tableField.value() != null && !tableField.value().isEmpty() ? tableField.value() : TableScheme.toUnderline(fieldName);
//                            tableScheme.field2Column.put(fieldName.toLowerCase(), column);
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
//            }
        }

    }
}
