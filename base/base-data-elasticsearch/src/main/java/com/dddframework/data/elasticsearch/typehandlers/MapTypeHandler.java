package com.dddframework.data.elasticsearch.typehandlers;

import java.util.Map;

/**
 * 自定义类型转换：json/varchar <-> Map<String, Object>
 * PO类该字段需要注解@TableField(typeHandler = MapTypeHandler.class)，否则update会失效
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2024/5/6 14:52
 */
@Deprecated
public class MapTypeHandler extends JsonStringTypeHandler<Map> {

}