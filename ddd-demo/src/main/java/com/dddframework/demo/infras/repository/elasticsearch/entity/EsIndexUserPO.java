package com.dddframework.demo.infras.repository.elasticsearch.entity;

import lombok.Data;

import java.util.Map;

/**
 * 用户
 */
@Data
public class EsIndexUserPO extends EsPO {
    // mapping信息
    private Map<String, Object> properties;

}
