package com.dddframework.data.typehandlers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换：varchar <-> Double[]，使用英文逗号,分割
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2021/9/12 14:52
 * @since jdk1.8
 */
@Slf4j(topic = "### BASE-DATA : TypeHandlers ###")
public class DoublesTypeHandler extends BaseTypeHandler<Double[]> {

    public DoublesTypeHandler() {
        log.info("Loading DoublesTypeHandler, type: Double[]");
    }

    @Override
    protected String convert(Double[] obj) {
        if (obj.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (Double d : obj) {
            sb.append(d).append(",");
        }
        sb.append("<END>");
        return sb.toString().replace(",<END>", "");
    }

    @Override
    protected Double[] parse(String result) {
        String[] split = result.split(",");
        List<Double> doubles = new ArrayList<>();
        for (String s : split) {
            doubles.add(Double.valueOf(s));
        }
        return doubles.toArray(new Double[]{});
    }

}