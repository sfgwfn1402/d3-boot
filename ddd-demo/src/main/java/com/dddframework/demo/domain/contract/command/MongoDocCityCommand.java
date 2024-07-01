package com.dddframework.demo.domain.contract.command;

import com.dddframework.core.mongodb.contract.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mongodb city
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoDocCityCommand {

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
