package com.dddframework.core.elasticsearch.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础模型，支持增删改的充血模型
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
public class Model implements Serializable {

    // 索引名称
    public String index;

    /**
     * 创建索引
     * @param map
     * @return
     * @param <M>
     */
    public <M extends Map<String, Object>> boolean createIndex(M map) {
        return BaseRepository.of(this.getClass()).createIndex(this, map);
    }

    public boolean save() {
        return BaseRepository.of(this.getClass()).save(this);
    }

    public boolean update() {
        return BaseRepository.of(this.getClass()).update(this);
    }

    public <Q extends Query> boolean update(Q query) {
        return BaseRepository.of(query.getClass()).update(this, query);
    }

    public boolean updateByKey() {
        return BaseRepository.of(this.getClass()).updateByKey(this);
    }

    public <Q extends Query> void fill(Q query) {
        BaseRepository.of(this.getClass()).fill(query, this);
    }

    public static <M extends Model> boolean save(List<M> models) {
        if (models == null || models.isEmpty()) {
            return false;
        }
        return BaseRepository.of(models.get(0).getClass()).save(models);
    }

    public static <M extends Model> boolean update(List<M> models) {
        if (models == null || models.isEmpty()) {
            return false;
        }
        return BaseRepository.of(models.get(0).getClass()).update(models);
    }

    public static <M extends Model> boolean updateByKey(List<M> models) {
        if (models == null || models.isEmpty()) {
            return false;
        }
        return BaseRepository.of(models.get(0).getClass()).updateByKey(models);
    }

    public static <Q extends Query> boolean delete(Q query) {
        return BaseRepository.of(query.getClass()).delete(query);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}