package com.dddframework.demo.domain.contract.query;

import com.dddframework.core.mongodb.contract.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mongo查询
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoDocCityQuery extends Query {
    private Long id;
}
