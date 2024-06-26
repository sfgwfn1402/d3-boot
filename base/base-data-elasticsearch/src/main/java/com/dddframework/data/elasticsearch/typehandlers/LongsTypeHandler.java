package com.dddframework.data.elasticsearch.typehandlers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换：varchar <-> Long[]，使用英文逗号,分割
 *
 * @author Jensen
 * @公众号 架构师修行录
 * @date 2021/9/12 14:52
 * @since jdk1.8
 */
@Slf4j(topic = "### BASE-DATA : TypeHandlers ###")
public class LongsTypeHandler extends BaseTypeHandler<Long[]> {
    public LongsTypeHandler() {
        log.info("Loading LongsTypeHandler, type: Long[]");
    }

    @Override
    protected String convert(Long[] obj) {
        if (obj.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (Long l : obj) {
            sb.append(l).append(",");
        }
        sb.append("<END>");
        return sb.toString().replace(",<END>", "");
    }

    @Override
    protected Long[] parse(String result) {
        String[] split = result.split(",");
        List<Long> longs = new ArrayList<>();
        for (String s : split) {
            longs.add(Long.valueOf(s));
        }
        return longs.toArray(new Long[]{});
    }

}