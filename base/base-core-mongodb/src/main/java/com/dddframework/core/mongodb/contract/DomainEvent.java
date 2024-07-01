package com.dddframework.core.mongodb.contract;

import com.dddframework.core.mongodb.context.SpringContext;
import com.dddframework.core.mongodb.context.ThreadContext;
import com.dddframework.core.mongodb.contract.constant.ContextConstants;
import org.springframework.context.ApplicationEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.*;

/**
 * 领域事件
 * 1. 异步事件透传线程变量
 * 2. 租户策略
 * 3. 条件策略
 */
public abstract class DomainEvent extends ApplicationEvent {
    // 本地线程变量池，用于存储跨线程信息
    private final Map<String, Object> THREAD_LOCAL_DATAS = ThreadContext.getValues();

    /**
     * 领域事件构造器
     *
     * @param source 事件内容
     * @param <T>    任意类型
     */
    public <T> DomainEvent(T source) {
        super(source);
    }

    /**
     * 获取事件内容
     *
     * @param <T> 任意类型
     * @return 事件内容
     */
    public <T> T get() {
        ThreadContext.setValues(THREAD_LOCAL_DATAS);
        return (T) super.getSource();
    }

    /**
     * 租户判断
     * 使用方式：监听方法标注@EventListener(condition = "#event.tenantIn('xxx', 'xxx')")
     *
     * @param tenantIds 指定租户ID才能订阅
     * @return 该租户能否监听
     */
    public boolean tenantIn(String... tenantIds) {
        if (tenantIds == null) return false;
        ThreadContext.setValues(THREAD_LOCAL_DATAS);
        String tenantId = ThreadContext.getOrDefault(ContextConstants.TENANT_ID, "");
        return Arrays.asList(tenantIds).contains(tenantId);
    }

    // 监听者能否执行的条件，用于控制事件监听器能否执行（策略模式）
    private Collection supports;

    /**
     * 领域事件构造器
     *
     * @param source   事件内容
     * @param supports 支持执行的条件，配合supports方法使用
     * @param <T>      任意类型
     */
    public <T> DomainEvent(T source, Collection supports) {
        super(source);
        this.supports = supports;
    }

    /**
     * 条件判断（策略模式）
     * 使用方式：监听方法标注@EventListener(condition = "#event.supports('xxx', 'xxx')")
     *
     * @param supports 支持的类型
     * @param <T>      任意类型
     * @return 该条件下能否监听
     */
    public <T> boolean supports(T... supports) {
        if (this.supports == null || supports == null) return false;
        ThreadContext.setValues(THREAD_LOCAL_DATAS);
        List<T> supportList = Arrays.asList(supports);
        for (Object support : this.supports) {
            if (supportList.contains(support)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 立即发布事件
     * 使用方便，副作用：会降低代码可读性
     * 建议使用原生的SpringContext.getApplicationContext().publishEvent()方法
     */
    public void publish() {
        SpringContext.getApplicationContext().publishEvent(this);
    }

    /**
     * 定时发布事件
     *
     * @param sendTime 发送时间
     */
    public void publishAt(Date sendTime) {
        ThreadPoolTaskScheduler taskScheduler = SpringContext.getBean("taskScheduler", ThreadPoolTaskScheduler.class);
        taskScheduler.schedule(this::publish, sendTime);
    }

    /**
     * 延迟发布事件
     *
     * @param delayMillis 延迟毫秒数
     */
    public void publishIn(long delayMillis) {
        publishAt(new Date(System.currentTimeMillis() + delayMillis));
    }
}