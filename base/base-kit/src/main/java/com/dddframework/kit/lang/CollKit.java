package com.dddframework.kit.lang;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * 集合工具类
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@UtilityClass
public class CollKit extends CollUtil {

    public <T> T[] convert(Collection<T> coll) {
        if (coll == null || coll.size() == 0) {
            return null;
        }
        Class tClass = null;
        for (T t : coll) {
            tClass = t.getClass();
            break;
        }
        return coll.toArray((T[]) Array.newInstance(tClass, 0));
    }

    public <T> List<T> convert(T[] array) {
        if (isEmpty(array)) {
            return null;
        }
        return Lists.newArrayList(array);
    }

    public <T> boolean isNotEmpty(T[] array) {
        return null != array && array.length != 0;
    }

    public <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }

}