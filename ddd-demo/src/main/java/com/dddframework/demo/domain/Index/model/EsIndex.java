package com.dddframework.demo.domain.Index.model;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.core.elasticsearch.contract.Model;
import com.dddframework.demo.domain.user.repository.EsIndexRepository;
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
public class EsIndex extends Model {

    // mapping信息
    private Map<String, Object> properties;


    public static EsIndexRepository repository() {
        return BaseRepository.of(EsIndex.class);
    }
}
