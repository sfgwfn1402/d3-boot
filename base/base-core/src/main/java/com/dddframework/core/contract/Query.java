package com.dddframework.core.contract;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.BizAssert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 基础查询，PO依赖此类用于条件查询数据库
 * <p>
 * 注意子类不要添加@Accessors(chain = true)注解，否则会导致后面BeanKit#toMap(Object, Map, Collection) 获取不到属性
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Query extends Page {
    // 查询条件后缀：以该后缀结尾的参数，可以自动补充查询条件
    public static final String NOT_QUERY = "Not";// 对应数据库的!=语句
    public static final String IN_QUERY = "In";// 对应数据库的in语句
    public static final String NOT_IN_QUERY = "NotIn";// 对应数据库的not in语句
    public static final String LIKE_QUERY = "Like";// 对应数据库的like '%xxx%'语句
    public static final String LIKE_LEFT_QUERY = "LikeLeft";// 对应数据库的like 'xxx%'语句
    public static final String LIKE_RIGHT_QUERY = "LikeRight";// 对应数据库的like '%xxx'语句
    public static final String MIN_QUERY = "Min";// 对应数据库的>语句，一般用于数值区间最小值筛选
    public static final String MAX_QUERY = "Max";// 对应数据库的<语句，一般用于数值区间最大值筛选
    public static final String MIN_EQUALS_QUERY = "MinEq";// 对应数据库的>=语句，一般用于数值区间最小值筛选
    public static final String MAX_EQUALS_QUERY = "MaxEq";// 对应数据库的<=语句，一般用于数值区间最大值筛选
    public static final String START_QUERY = "Start";// 对应数据库的>=语句，一般用于开始时间筛选
    public static final String END_QUERY = "End";// 对应数据库的<=语句，一般用于结束时间筛选
    public static final String NULL_QUERY = "IsNull";// 对应数据库的is null或is not null语句。true: xxx is null, false: xxx is not null
    public static final String KEYWORDS_QUERY = "keywords";// 关键字查询 a=xxx or b=xxx
    // select字段列表
    protected String[] select;
    // 分组字段列表
    protected String groupBy;
    // having过滤条件
    protected String having;
    // 排序字段列表：["aField_DESC","bField_ASC"]
    protected String[] orderBy;
    // 关键字：or查询
    protected Map<String, Object> keywords;
    // 聚合参数集
    protected List<String> fills;

    public <Q extends Query> Q select(String... columns) {
        this.setSelect(columns);
        return (Q) this;
    }

    public <Q extends Query> Q groupBy(String groupBy) {
        this.setGroupBy(groupBy);
        return (Q) this;
    }

    public <Q extends Query> Q having(String having) {
        this.setHaving(having);
        return (Q) this;
    }

    public <Q extends Query> Q current(long current) {
        this.setCurrent(current);
        return (Q) this;
    }

    public <Q extends Query> Q size(long size) {
        this.setSize(size);
        return (Q) this;
    }

    public <Q extends Query> Q orderBy(String... orderBy) {
        this.setOrderBy(orderBy);
        return (Q) this;
    }

    // 聚合哪些数据
    public <Q extends Query> Q fills(String... fills) {
        if (fills != null) {
            this.setFills(Arrays.asList(fills));
        }
        return (Q) this;
    }

    // 是否聚合
    public Boolean fill(String fill) {
        if (this.fills == null || this.fills.size() == 0) {
            return false;
        }
        return fills.contains(fill);
    }

    @JsonIgnore
    public long getStartIndex() {
        return (getCurrent() - 1L) * getSize();
    }

    @JsonIgnore
    public long getEndIndex() {
        return getStartIndex() + getSize();
    }

    @Deprecated
    public long getPage() {
        return getCurrent();
    }

    @Deprecated
    public void setPage(long page) {
        setCurrent(page);
    }

    @Deprecated
    public long getLimit() {
        return getSize();
    }

    @Deprecated
    public void setLimit(long limit) {
        setSize(limit);
    }

    public <T extends BaseRepository<M, Q>, M extends Model, Q extends Query> T repository() {
        ThreadContext.set(ContextConstants.QUERY, this);
        return BaseRepository.of(this.getClass());
    }

    public <MP> MP mapper() {
        return repository().getMapper();
    }

    public List<Map<String, Object>> maps() {
        return repository().maps(this);
    }

    public Map<String, Object> firstMap() {
        List<Map<String, Object>> result = maps();
        if (result == null || result.isEmpty()) return Collections.emptyMap();
        return result.get(0);
    }

    public <M extends Model> M one() {
        return (M) repository().one(this);
    }

    public <M extends Model> M one(String ifNull, Object... params) {
        M one = one();
        return BizAssert.notNull(one, ifNull, params);
    }

    public <M extends Model> M first() {
        return (M) repository().first(this);
    }

    public <M extends Model> M first(String ifNull, Object... params) {
        M first = first();
        BizAssert.notNull(first, ifNull, params);
        return first;
    }

    public int count() {
        return repository().count(this);
    }

    public boolean exist() {
        return repository().exist(this);
    }

    public void exist(String ifNotExist, Object... params) {
        BizAssert.isTrue(exist(), ifNotExist, params);
    }

    public boolean notExist() {
        return !this.exist();
    }

    public void notExist(String ifExist, Object... params) {
        BizAssert.isTrue(notExist(), ifExist, params);
    }

    public <M extends Model> Page<M> page() {
        return (Page<M>) repository().page(this);
    }

    public <M extends Model> Page<M> page(String ifEmpty, Object... params) {
        Page<M> page = page();
        BizAssert.notEmpty(page.getRecords(), ifEmpty, params);
        return page;
    }

    public <M extends Model> List<M> list() {
        return (List<M>) repository().list(this);
    }

    public <M extends Model> List<M> list(String ifEmpty, Object... params) {
        List<M> list = list();
        BizAssert.notEmpty(list, ifEmpty, params);
        return list;
    }

}