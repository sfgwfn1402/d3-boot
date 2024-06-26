package com.dddframework.core.elasticsearch.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Json工具类
 */
@Slf4j(topic = "### BASE-CORE : JsonKit ###")
@UtilityClass
public class JsonKit {
    private final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final ObjectMapper DEFAULT_OBJECT_MAPPER = defaultObjectMapper();

    public ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setDateFormat(new BaseSimpleDateFormat());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        // SimpleModule simpleModule = new SimpleModule();
        // simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = defaultObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, DefaultTyping.NON_FINAL, As.WRAPPER_ARRAY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
        return objectMapper;
    }

    public ObjectMapper buildObjectMapper(String timePattern) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(timePattern)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(timePattern)));
        javaTimeModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                SimpleDateFormat formatter = new SimpleDateFormat(timePattern);
                String formattedDate = formatter.format(date);
                jsonGenerator.writeString(formattedDate);
            }
        });
        javaTimeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                SimpleDateFormat format = new SimpleDateFormat(timePattern);
                String date = jsonParser.getText();
                try {
                    return format.parse(date);
                } catch (ParseException var6) {
                    throw new RuntimeException(var6);
                }
            }
        });
        objectMapper.registerModule(javaTimeModule);
        // Long类型返回前端转为String类型，防止过长展示出错
        // SimpleModule simpleModule = new SimpleModule();
        // simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // objectMapper.registerModule(simpleModule);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public String toJson(Object object) {
        if (object == null) return null;
        if (object instanceof String) return (String) object;
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException var2) {
            log.error("write to json string error:" + object, var2);
            return "";
        }
    }

    public Map<String, Object> toMap(String json) {
        Map<String, Object> map = new HashMap<>();
        try {
            JsonNode rootNode = DEFAULT_OBJECT_MAPPER.readTree(json);
            Iterator<String> fieldNames = rootNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode jsonNode = rootNode.get(fieldName);
                if (jsonNode.isValueNode()) {
                    map.put(fieldName, getNodeValue(jsonNode));
                } else if (jsonNode.isObject()) {
                    map.put(fieldName, toMap(jsonNode.toString()));
                } else if (jsonNode.isArray()) {
                    List<Object> list = new ArrayList<>();
                    for (JsonNode childNode : jsonNode) {
                        if (childNode.isValueNode()) {
                            list.add(getNodeValue(childNode));
                        } else if (childNode.isObject()) {
                            list.add(toMap(childNode.toString()));
                        }
                    }
                    map.put(fieldName, list);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("parse json to map error:" + json, e);
        }
        return map;
    }

    public List<Map<String, Object>> toMapList(String json) {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            JsonNode rootNode = DEFAULT_OBJECT_MAPPER.readTree(json);
            for (JsonNode childNode : rootNode) {
                if (childNode.isObject()) {
                    list.add(toMap(childNode.toString()));
                }
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("parse json to map error:" + json, e);
        }
        return null;
    }

    public String toJsonWithDefaultPrettyPrinter(Object object) {
        try {
            return DEFAULT_OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException var2) {
            log.error("write to json string error:" + object, var2);
            return "";
        }
    }

    public <T> T toObject(Object object, Class<T> clazz) {
        if (object == null) return null;
        if (!(object instanceof String)) {
            return (T) object;
        }
        String json = (String) object;
        if (json.isEmpty()) {
            return null;
        } else {
            try {
                return DEFAULT_OBJECT_MAPPER.readValue(json, clazz);
            } catch (IOException var3) {
                log.error("parse json string error:" + json, var3);
                return null;
            }
        }
    }

    public <T> T toObject(Object object, JavaType javaType) {
        if (object == null) return null;
        if (!(object instanceof String)) {
            return (T) object;
        }
        String json = (String) object;
        if (json.isEmpty()) {
            return null;
        } else {
            try {
                return DEFAULT_OBJECT_MAPPER.readValue(json, javaType);
            } catch (IOException var3) {
                log.error("parse json string error:" + json, var3);
                return null;
            }
        }
    }

    public <T> List<T> toList(Object object, Class<T> beanType) {
        if (object == null) return new ArrayList<>();
        if (!(object instanceof String)) {
            return (List<T>) object;
        }
        String jsonArray = (String) object;
        if (jsonArray.isEmpty()) {
            return null;
        }
        JavaType javaType = DEFAULT_OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, new Class[]{beanType});

        try {
            return DEFAULT_OBJECT_MAPPER.readValue(jsonArray, javaType);
        } catch (IOException var4) {
            log.error("translate to POJO failed. jsonArray=" + jsonArray, var4);
            return new ArrayList();
        }
    }

    public <T> T jsonToPojo(String jsonData, TypeReference<T> typeReference) {
        if (jsonData == null || jsonData.isEmpty()) {
            return null;
        } else {
            try {
                return DEFAULT_OBJECT_MAPPER.readValue(jsonData, typeReference);
            } catch (IOException var3) {
                log.error("translate to POJO failed. json=" + jsonData, var3);
                return null;
            }
        }
    }

    public boolean isJson(String str) {
        return str.startsWith("{") && str.endsWith("}");
    }

    public boolean isJsonArray(String str) {
        return str.startsWith("[") && str.endsWith("]");
    }

    public JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return DEFAULT_OBJECT_MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return DEFAULT_OBJECT_MAPPER.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public void update(String jsonString, Object object) {
        try {
            DEFAULT_OBJECT_MAPPER.readerForUpdating(object).readValue(jsonString);
        } catch (IOException var3) {
            log.error("update json string:" + jsonString + " to object:" + object + " error.", var3);
        }

    }

    private static Object getNodeValue(JsonNode childNode) {
        return childNode.isDouble() ? childNode.asDouble() : childNode.isInt() ? childNode.asInt() : childNode.isBoolean() ? childNode.asBoolean() : childNode.asText();
    }

    private class BaseSimpleDateFormat extends SimpleDateFormat {
        public BaseSimpleDateFormat() {
            super("yyyy-MM-dd HH:mm:ss.SSS");
        }

        public Date parse(String source) throws ParseException {
            if (source.length() == 19) {
                source = source.concat(".000");
            }

            return super.parse(source);
        }
    }


}