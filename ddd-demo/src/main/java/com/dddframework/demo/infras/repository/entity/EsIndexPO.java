package com.dddframework.demo.infras.repository.entity;

import lombok.Data;

import java.util.Map;

/**
 * 用户
 */
@Data
public class EsIndexPO extends PO {
    // mapping信息
    private Map<String, Object> properties;

}
