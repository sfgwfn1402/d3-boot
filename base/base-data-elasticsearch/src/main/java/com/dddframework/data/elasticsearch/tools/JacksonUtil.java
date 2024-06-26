package com.dddframework.data.elasticsearch.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @description: json工具类
 */
@Slf4j
public class JacksonUtil {

    // 日起格式化
    private static final String STANDARD_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static ObjectMapper objectMapper = new ObjectMapper();
    public static ObjectMapper prettyObjectMapper = new ObjectMapper();
    public static ObjectMapper snakeCaseObjectMapper = new ObjectMapper();
    public static ObjectMapper redisObjectMapper = new ObjectMapper();

    static {

        baseSet(objectMapper);

        baseSet(prettyObjectMapper);
        prettyObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        baseSet(redisObjectMapper);
        redisObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);


        baseSet(snakeCaseObjectMapper);
        snakeCaseObjectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    }


    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    /**
     * 字符串转换为自定义对象
     *
     * @param str   要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }


    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String obj2String(T obj, ObjectMapper objectMapper) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    /**
     * 字符串转换为自定义对象
     *
     * @param str   要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz, ObjectMapper objectMapper) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }


    public static <T> Map obj2snakeCaseMap(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return snakeCaseObjectMapper.convertValue(obj, Map.class);
        } catch (Exception e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }

    public static <T> Map obj2map(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    public static <T> T map2objSnakeCase(Map map, Class<T> clazz) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        try {
            return snakeCaseObjectMapper.convertValue(map, clazz);
        } catch (Exception e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    public static <T> T map2obj(Map map, Class<T> clazz) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        try {
            return objectMapper.convertValue(map, clazz);
        } catch (Exception e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     *
     * @param obj 对象
     * @return 美化的Json格式字符串
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }


    public static <T> T string2ObjSnakeCase(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : snakeCaseObjectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }

    public static <T> T string2ObjSnakeCase(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = snakeCaseObjectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return snakeCaseObjectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error : {}" + e.getMessage());
            return null;
        }
    }

    /**
     * @return T
     * @Description str转集合
     * @Param [str, typeReference]
     * @Author lifubo
     * @Date 2021/5/26 10:48
     **/
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error : {}" + e.getMessage());
            return null;
        }
    }


    private static void baseSet(ObjectMapper objectMapper) {


        //1.序列化配置

        //属性值为null的不参与序列化
        //@JsonInclude(Include.NON_NULL) 这个注解放在类头上就可以解决
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 在序列化一个空对象时时不抛出异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 转换为格式化的json
        //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        //2.反序列化配置

        // 忽略反序列化时在json字符串中存在, 但在java对象中不存在的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //通过设置FAIL_ON_NULL_FOR_PRIMITIVES属性是true, 你会在试图把null值的JSON属性解析为Java原生类型属性时抛出异常
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错 ；JSON数据的属性要多于你的Java对象的属性
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //null值 转成 原生类型的属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);


        //bigDecimal 问题
        //objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);

        //是否允许/* */或者//这种类型的注释出现
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

        //是否允许属性名支持单引号，也就是使用''包裹，形如这样：
        //objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);


        //3.日期 Module配置

        //Date类型的序列化  所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_PATTERN));

        // 初始化JavaTimeModule
        // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  局部配置
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        //处理LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(STANDARD_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        //处理LocalDate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        //处理LocalTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        //注册时间模块, 支持支持jsr310, 即新的时间类(java.timsetSerializationInclusione包下的时间类)
        objectMapper.registerModule(javaTimeModule);

    }

}
