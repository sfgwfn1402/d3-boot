package com.dddframework.core.elasticsearch.context;

import com.dddframework.core.elasticsearch.contract.constant.ContextConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.*;
import java.util.function.Supplier;

/**
 * Spring上下文：显式获取SpringBean、注册Bean；SpringEvent事件发布
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Slf4j(topic = "### BASE-CORE : SpringContext ###")
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public SpringContext() {
        log.debug("Loading SpringContext");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.getMethodName().equals("main")) {
                try {
                    String projectPackage = Class.forName(element.getClassName()).getPackage().getName();
                    BaseContext.inject(ContextConstants.PROJECT_PACKAGE, projectPackage);
                    log.debug("PROJECT_PACKAGE: {}", projectPackage);
                } catch (ClassNotFoundException e) {
                    log.error("Cannot find class: {}", element.getClassName());
                }
                break;
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未注入");
        }
        return applicationContext;
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return getApplicationContext().getBean(name, requiredType);
    }

    public static <T> T register(Supplier<T> builder) {
        T bean = builder.get();
        Class<T> beanClass = (Class<T>) bean.getClass();
        try {
            return getApplicationContext().getBean(beanClass);
        } catch (NoSuchBeanDefinitionException exception) {
            // 注册到Bean工厂
            ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory().registerSingleton(Character.toLowerCase(beanClass.getSimpleName().charAt(0)) + beanClass.getSimpleName().substring(1), bean);
            return bean;
        }
    }

    public static <T> T getHandler(String name, Class<T> cls) {
        T t = null;
        if (name != null && !name.isEmpty()) {
            try {
                t = getApplicationContext().getBean(name, cls);
            } catch (Exception var4) {
                log.error("####################" + name + "未定义");
            }
        }

        return t;
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> Collection<T> getBeans(Class<T> clazz) {
        Map<String, T> beansOfType = getApplicationContext().getBeansOfType(clazz);
        if (beansOfType.isEmpty()) return Collections.emptyList();
        return beansOfType.values();
    }

    public static Environment getEnv() {
        return getApplicationContext().getEnvironment();
    }

    /**
     * 定时发布事件
     */
    public static void schedule(ApplicationEvent event, Date startTime) {
        ThreadPoolTaskScheduler taskScheduler = getApplicationContext().getBean("taskScheduler", ThreadPoolTaskScheduler.class);
        taskScheduler.schedule(() -> getApplicationContext().publishEvent(Objects.requireNonNull(event)), startTime);
    }

    /**
     * 定时发布事件
     */
    public static void schedule(ApplicationEvent event, long delayMillis) {
        schedule(event, new Date(System.currentTimeMillis() + delayMillis));
    }
}