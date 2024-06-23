package com.dddframework.kit.lang;

import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射工具类
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Slf4j(topic = "### BASE-KIT : ReflectKit ###")
@UtilityClass
public class ReflectKit extends ReflectUtil {

    public <T> Class<T> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            throw new RuntimeException(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    log.warn(String.format("Warn: %s not set the actual class on superclass generic parameter", clazz.getSimpleName()));
                    throw new RuntimeException(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
                } else {
                    return (Class<T>) params[index];
                }
            } else {
                log.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(), params.length));
                throw new RuntimeException(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            }
        }
    }
}
