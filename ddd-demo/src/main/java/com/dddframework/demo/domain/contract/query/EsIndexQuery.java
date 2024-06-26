package com.dddframework.demo.domain.contract.query;

import com.dddframework.core.elasticsearch.contract.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Es查询
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EsIndexQuery extends Query {
    private String index;

}
