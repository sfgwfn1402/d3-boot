package com.dddframework.core.contract;

import com.dddframework.core.utils.BizAssert;

import java.io.Serializable;

/**
 * 统一接口响应，标准的响应数据结构
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
public interface IR extends Serializable {
    Serializable getCode();

    String getMsg();

    <T> T getData();

    Boolean isOk();

    default void isOk(String notOkThrows) {
        BizAssert.isTrue(isOk(), notOkThrows + " -> {}", this);
    }

    default <T> T getData(String notOkThrows) {
        BizAssert.isTrue(isOk(), notOkThrows + " -> {}", this);
        T data = getData();
        BizAssert.notNull(data, notOkThrows + " -> {}", this);
        return data;
    }
}