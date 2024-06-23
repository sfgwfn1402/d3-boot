package com.dddframework.core.contract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 分页数据对象
 * 实现集合接口，集合操作的是records对象
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> implements Iterable<T>, IPage<T> {
    // 列表数据
    private List<T> records;
    // 总记录数
    private long total;
    // 回写当前页
    private long current = 1L;
    // 回写每页大小
    private long size = 10L;

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
        this.total = 0L;
        this.records = Collections.emptyList();
    }

    public static <T> Page<T> succeed(List<T> records, long total, long current, long size) {
        return new Page<>(records, total, current, size);
    }

    public static <T> Page<T> empty() {
        return new Page<>(Collections.emptyList(), 0L, 0L, 0L);
    }

    @Override
    public Iterator<T> iterator() {
        return this.records != null && this.records.size() != 0 ? this.records.iterator() : null;
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.records != null ? this.records.spliterator() : null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        if (this.records != null) {
            this.records.forEach(action);
        }
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.records == null || this.records.isEmpty();
    }

    public boolean contains(Object o) {
        return o != null && this.records != null && this.records.contains(o);
    }

    public boolean add(T t) {
        return this.records != null && this.records.add(t);
    }

    public boolean remove(Object o) {
        return this.records != null && this.records.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return this.records != null && this.records.containsAll(c);
    }

    public boolean addAll(Collection<? extends T> c) {
        return this.records != null && this.records.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return this.records != null && this.records.removeAll(c);
    }

    public boolean removeIf(Predicate<? super T> filter) {
        return this.records != null && this.records.removeIf(filter);
    }

    public boolean retainAll(Collection<?> c) {
        return this.records != null && this.records.retainAll(c);
    }

    public Stream<T> stream() {
        return this.records != null ? this.records.stream() : new ArrayList<T>().stream();
    }

    public Page<T> peek(Consumer<? super T> action) {
        if (this.records != null) {
            this.records.forEach(action);
        }
        return this;
    }

    public IPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public List<OrderItem> orders() {
        return null;
    }

    @Override
    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    // 自定义分页后聚合的数据
    public Page<T> with(String... fields) {
        if (!this.isEmpty()) {
            Query query = ThreadContext.get(ContextConstants.QUERY);
            if (query != null) {
                query.repository().fill(query.fills(fields), (List<Model>) this.getRecords());
            }
        }
        ThreadContext.remove(ContextConstants.QUERY);
        return this;
    }
}