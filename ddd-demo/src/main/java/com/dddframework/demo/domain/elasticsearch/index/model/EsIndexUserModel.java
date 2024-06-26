package com.dddframework.demo.domain.elasticsearch.index.model;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.core.elasticsearch.contract.Model;
import com.dddframework.demo.domain.elasticsearch.index.repository.EsIndexUserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * es index
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EsIndexUserModel extends Model {

    // mapping信息
    private Map<String, Object> properties;


    public static EsIndexUserRepository repository() {
        return BaseRepository.of(EsIndexUserModel.class);
    }
}
