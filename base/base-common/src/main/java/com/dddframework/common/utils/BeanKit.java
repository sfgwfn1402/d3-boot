package com.dddframework.common.utils;

import com.dddframework.common.contract.exception.ServiceException;
import lombok.experimental.UtilityClass;
import org.springframework.beans.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@UtilityClass
public class BeanKit {
    private final String REFLECT_ERROR = "反射获取转换对象异常";
    private final String COMMA = ",";

    public void mapToObject(Map map, Object object) {
        if (object != null && map != null) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(object);
            PropertyValues propertyValues = new MutablePropertyValues(map);
            beanWrapper.setPropertyValues(propertyValues, true, true);
        }

    }

    public String changeColumnToFieldName(String columnName) {
        String[] array = columnName.split("_");
        StringBuilder sb = null;
        String[] var3 = array;
        int var4 = array.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String cn = var3[var5];
            cn = cn.toLowerCase();
            if (sb == null) {
                sb = new StringBuilder(cn);
            } else {
                sb.append(cn.substring(0, 1).toUpperCase()).append(cn.substring(1));
            }
        }

        return sb != null ? sb.toString() : null;
    }

    public Map<String, Object> toMap(Object obj) {
        return toMap(obj, true, null);
    }

    public Map<String, Object> toMapClean(Object obj) {
        return toMap(obj, false, null);
    }

    public Map<String, Object> toMap(Object obj, boolean withNull, String... ignoreFields) {
        if (obj == null) {
            return null;
        } else {
            Map<String, Object> map = new HashMap<>();
            if (obj instanceof String) {
                map.put((String) obj, obj);
            } else {
                toMap(obj, map, withNull, ignoreFields);
            }
            return map;
        }
    }

    public void mapsToObjects(List<Map<String, Object>> mapList, List objectList, Class clazz) {
        if (mapList != null && !mapList.isEmpty() && objectList != null) {
            Iterator var3 = mapList.iterator();

            while (var3.hasNext()) {
                Map map = (Map) var3.next();
                Object object = gainInstanceByReflect(clazz);
                mapToObject(map, object);
                objectList.add(object);
            }
        }

    }

    public void objectsToObjects(List sourceList, List targetList, Class targetClass) {
        if (sourceList != null && !sourceList.isEmpty() && targetList != null) {
            Iterator var3 = sourceList.iterator();

            while (var3.hasNext()) {
                Object source = var3.next();
                Object target = gainInstanceByReflect(targetClass);
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            }
        }

    }

    public String listToString(List<String> list) {
        return listToString(list, COMMA);
    }

    public String listToString(List<String> list, String separator) {
        return listToString(list, separator, null);
    }

    public String listToString(List<String> list, String separator, String surround) {
        StringBuilder builder = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            int i = 0;
            Iterator var5 = list.iterator();

            while (var5.hasNext()) {
                String str = (String) var5.next();
                if (i++ > 0) {
                    builder.append(separator);
                }

                if (surround != null) {
                    builder.append(surround).append(str).append(surround);
                } else {
                    builder.append(str);
                }
            }
        }

        return builder.toString();
    }

    public Object gainInstanceByReflect(Class clazz) {
        String className = clazz.getName();
        return gainInstanceByReflect(className);
    }

    private Object gainInstanceByReflect(String className) {
        Object target = null;

        try {
            target = Class.forName(className).newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            throw new ServiceException("{} {}", REFLECT_ERROR, e.getLocalizedMessage());
        }

        return target;
    }

    public void objectToObject(Object source, Object target) {
        objectToObject(source, target, false, true, true);
    }

    public void objectToObject(Object source, Object target, boolean ignoreNull) {
        objectToObject(source, target, ignoreNull, false, false);
    }

    public void objectToObject(Object source, Object target, boolean ignoreNull, boolean withCreateInfo, boolean withUpdateInfo) {
        if (source != null && target != null) {
            List<String> ignorePropertiesList = new ArrayList();
            if (!withCreateInfo) {
                ignorePropertiesList.add("creator");
                ignorePropertiesList.add("creatorCode");
                ignorePropertiesList.add("createTime");
            }

            if (!withUpdateInfo) {
                ignorePropertiesList.add("updater");
                ignorePropertiesList.add("updaterCode");
                ignorePropertiesList.add("updateTime");
            }

            if (ignoreNull) {
                ignoreNull(source, ignorePropertiesList);
            }

            String[] ignorePropertiesArray = new String[ignorePropertiesList.size()];
            BeanUtils.copyProperties(source, target, (String[]) ignorePropertiesList.toArray(ignorePropertiesArray));
        }
    }

    private void ignoreNull(Object source, List<String> ignorePropertiesList) {
        Field[] fields = source.getClass().getDeclaredFields();

        try {
            Field[] var3 = fields;
            int var4 = fields.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                field.setAccessible(true);
                if (field.get(source) == null) {
                    ignorePropertiesList.add(field.getName());
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ServiceException("{} {}", REFLECT_ERROR, e.getLocalizedMessage());
        }

    }

    public void objectToObject(Object source, Object target, boolean ignoreNull, boolean isUpdate) {
        objectToObject(source, target, ignoreNull, !isUpdate, true);
    }

    public void transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            PropertyDescriptor[] var4 = propertyDescriptors;
            int var5 = propertyDescriptors.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                PropertyDescriptor property = var4[var6];
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("Map --> Bean Error {} {} {}", map, obj, e.getLocalizedMessage());
        }

    }

    public <T, Q> T of(Q q, Class<T> targetClass) {
        if (Objects.isNull(q)) {
            return null;
        } else {
            T t = BeanUtils.instantiate(targetClass);
            objectToObject(q, t, true, true, true);
            return t;
        }
    }

    public <T> T ofMap(Map<String, Object> map, Class<T> targetClass) {
        if (!Objects.isNull(map) && !map.isEmpty()) {
            T t = BeanUtils.instantiate(targetClass);
            mapToObject(map, t);
            return t;
        } else {
            return null;
        }
    }

    public <T, Q> List<T> ofList(List<Q> sources, Class<T> targetClass) {
        if (sources == null || sources.isEmpty()) {
            return new ArrayList();
        } else {
            List<T> targetList = new ArrayList();
            objectsToObjects(sources, targetList, targetClass);
            return targetList;
        }
    }

    public <T> List<T> ofMapList(List<Map<String, Object>> sources, Class<T> targetClass) {
        if (sources == null || sources.isEmpty()) {
            return new ArrayList();
        } else {
            List<T> targetList = new ArrayList();
            mapsToObjects(sources, targetList, targetClass);
            return targetList;
        }
    }

    public String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet();
        PropertyDescriptor[] var4 = pds;
        int var5 = pds.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor pd = var4[var6];
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public void copy(Object source, Object target) {
        if (!isEmpty(source) && !isEmpty(target)) {
            if (source instanceof Collection && target instanceof Collection) {
                // 拷贝集合
                Iterator itSource = ((Collection) source).iterator();
                Iterator itTarget = ((Collection) target).iterator();
                while (itSource.hasNext()) {
                    BeanKit.copy(itSource.next(), itTarget.next());
                }
            } else {
                // 拷贝对象
                String[] nullPropertyNames = getNullPropertyNames(source);
                BeanUtils.copyProperties(source, target, nullPropertyNames);
            }
        }
    }

    public <T> T copy(Object source, Class<T> target) {
        return copy(source, target, (Class) null);
    }

    public <T> T copy(Object source, Class<T> target, Class<?> targetSuper) {
        if (source == null) {
            return null;
        } else {
            Object targetObject = null;

            try {
                targetObject = target.newInstance();
                BeanUtils.copyProperties(source, targetObject, targetSuper);
            } catch (Exception e) {
                throw new ServiceException("Convert Error {} {} {} {}", source, target, targetSuper, e.getLocalizedMessage());
            }

            return (T) targetObject;
        }
    }

    public <T> List<T> copy(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) {
            return null;
        } else {
            ArrayList targetList = new ArrayList(sourceList.size());

            try {
                Iterator var3 = sourceList.iterator();

                while (var3.hasNext()) {
                    Object source = var3.next();
                    T targetObject = target.newInstance();
                    BeanUtils.copyProperties(source, targetObject);
                    targetList.add(targetObject);
                }
            } catch (Exception e) {
                throw new ServiceException("Map --> Bean Error {} {} {}", sourceList, target, e.getLocalizedMessage());
            }

            return targetList;
        }
    }

    public <T> T copy(Object source, Class<T> target, String... ignoreProperties) {
        if (source == null) {
            return null;
        } else {
            Object targetObject = null;

            try {
                targetObject = target.newInstance();
                BeanUtils.copyProperties(source, targetObject, ignoreProperties);
            } catch (Exception e) {
                throw new ServiceException("Convert Error {} {} {}", source, target, e.getLocalizedMessage());
            }

            return (T) targetObject;
        }
    }

    public <T> List<T> copy(Collection<?> sourceList, Class<T> target, String... ignoreProperties) {
        if (sourceList == null) {
            return null;
        } else {
            ArrayList targetList = new ArrayList(sourceList.size());

            try {

                for (Object source : sourceList) {
                    T targetObject = target.newInstance();
                    BeanUtils.copyProperties(source, targetObject, ignoreProperties);
                    targetList.add(targetObject);
                }
            } catch (Exception e) {
                throw new ServiceException("Convert Error {} {} {}", sourceList, target, e.getLocalizedMessage());
            }

            return targetList;
        }
    }

    public void toMap(Object source, Map target, boolean withNull, String... ignoredFieldCol) {
        if (source == null) {
            throw new ServiceException("source cannot be null");
        }
        if (target == null) {
            throw new ServiceException("target cannot be null");
        }
        Set<String> ignores = new HashSet<>();
        if (ignoredFieldCol != null) {
            ignores.addAll(Arrays.asList(ignoredFieldCol));
        }
        try {
            /**
             * 使用Java的内省机制（Introspector）来获取一个对象的属性描述符（PropertyDescriptor）。通过调用getPropertyDescriptors()方法，
             * 它将返回一个包含所有可读写属性的数组。然后，通过遍历这个数组，我们可以获取每个属性的写方法（setter方法）。
             *
             * 在这段代码中，source是要检查的对象，它的类必须符合JavaBean的规范，即具有公共的getter和setter方法
             * 。getWriteMethod()方法将返回与该属性关联的写方法（如果有的话）。
             * 注意，Query的子类不要加@Accessors(chain = true)注解，否则会导致Introspector.getBeanInfo()方法获取不到属性,从而导致条件获取失败
             */
            PropertyDescriptor[] pds = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                Method method = pd.getWriteMethod();
                if (method != null) {
                    String fieldName = pd.getName();
                    if (!ignores.contains(fieldName)) {
                        Method getter = pd.getReadMethod();
                        if (getter != null) {
                            target.put(fieldName, getter.invoke(source));
                        } else if (withNull) {
                            target.put(fieldName, null);
                        }
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | IntrospectionException e) {
            throw new ServiceException("{} {}", REFLECT_ERROR, e.getLocalizedMessage());
        }

    }

    protected boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else {
            return obj instanceof Map ? ((Map) obj).isEmpty() : false;
        }
    }

}
