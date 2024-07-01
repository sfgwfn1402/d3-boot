package com.dddframework.demo.infras.repository.mongodb.entity;

import com.dddframework.data.elasticsearch.annotation.OnUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * es PO
 */
@Data
@NoArgsConstructor
public class MongoDocCityPO extends MongoPO {

    /**
     * 城市编号
     */
    private Long id;

    /**
     * 省份编号
     */
    private Long provinceId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;
}
