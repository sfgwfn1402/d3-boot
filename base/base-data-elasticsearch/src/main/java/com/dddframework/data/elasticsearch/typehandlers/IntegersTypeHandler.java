package com.dddframework.data.elasticsearch.typehandlers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换：varchar <-> Integer[]，使用英文逗号,分割
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2021/9/12 14:52
 * @since jdk1.8
 */
@Slf4j(topic = "### BASE-DATA : TypeHandlers ###")
public class IntegersTypeHandler extends BaseTypeHandler<Integer[]> {

    public IntegersTypeHandler() {
        log.info("Loading IntegersTypeHandler, type: Integer[]");
    }

    @Override
    protected String convert(Integer[] obj) {
        if (obj.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (Integer l : obj) {
            sb.append(l).append(",");
        }
        sb.append("<END>");
        return sb.toString().replace(",<END>", "");
    }

    @Override
    protected Integer[] parse(String result) {
        String[] split = result.split(",");
        List<Integer> integers = new ArrayList<>();
        for (String s : split) {
            integers.add(Integer.valueOf(s));
        }
        return integers.toArray(new Integer[]{});
    }

}