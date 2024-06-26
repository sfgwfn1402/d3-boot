package com.dddframework.data.elasticsearch.typehandlers;

import lombok.extern.slf4j.Slf4j;

/**
 * 类型转换：varchar <-> String[]，使用英文逗号,分割
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2021/9/12 14:52
 * @since jdk1.8
 */
@Slf4j(topic = "### BASE-DATA : TypeHandlers ###")
public class StringsTypeHandler extends BaseTypeHandler<String[]> {
    public StringsTypeHandler() {
        log.info("Loading StringsTypeHandler, type: String[]");
    }

    @Override
    protected String convert(String[] obj) {
        return String.join(",", obj);
    }

    @Override
    protected String[] parse(String result) {
        return result.split(",");
    }

}