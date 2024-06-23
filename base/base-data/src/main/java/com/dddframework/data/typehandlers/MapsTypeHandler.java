package com.dddframework.data.typehandlers;

import java.util.Map;

/**
 * 自定义数组类型转换：json/varchar <-> Map<String, Object>[]
 * PO类该字段需要注解@TableField(typeHandler = MapsTypeHandler.class)，否则update会失效
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2024/5/6 14:52
 */
@Deprecated
public class MapsTypeHandler extends JsonStringTypeHandler<Map[]> {

}